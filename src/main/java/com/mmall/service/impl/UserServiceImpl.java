package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/10
 * Time: 16:06
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if (resultCount == 0){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));//password
		if (user == null){
			return ServerResponse.createByErrorMessage("密码错误");
		}

		user.setPassword(StringUtils.EMPTY);//保护用户隐私，将返回的密码为空，避免被破解
		return ServerResponse.createBySuccess("登陆成功",user);


	}

	@Override
	public ServerResponse<Integer> register(User user) {
		int resultCount = userMapper.checkUsername(user.getUsername());
		if (resultCount > 0){
			return ServerResponse.createByErrorMessage("用户名已经存在,请重新输入");
		}
		resultCount = userMapper.checkEmail(user.getEmail());
		if (resultCount > 0){
			return ServerResponse.createByErrorMessage("邮箱已经存在,请重新输入");
		}
//		Integer maxId = userMapper.findMaxId();
//		user.setId(maxId+1);
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		Integer result = userMapper.insertSelective(user);
		return ServerResponse.createBySuccess("注册成功，欢迎使用mmall商城",result);
	}

	@Override
	public ServerResponse<Integer> checkUsernameValid(String str, Integer type) {
		int resultCount = 0;
		if(type == 0)	//0的时候为username，1的时候为email
			resultCount= userMapper.checkUsername(str);
		else
			resultCount= userMapper.checkEmail(str);
		if (resultCount != 0){
			return ServerResponse.createByErrorMessage("用户名已经存在,请重新输入");
		}
		return ServerResponse.createBySuccessMessage("校验成功");
	}

	@Override
	public ServerResponse<String> getForgetQuestionByUsername(String username) {
		int resultCount = userMapper.checkUsername(username);
		if (resultCount == 0){
			return ServerResponse.createByErrorMessage("用户名不存在，请重新输入");
		}
		String forgetQuestion = userMapper.getForgetQuestionByUsername(username);
		if(forgetQuestion == null || forgetQuestion.trim() == "") {
			return ServerResponse.createByErrorMessage("该用户未设置找回密码");
		}

		return ServerResponse.createBySuccess(forgetQuestion);
	}

	@Override
	public ServerResponse<String> checkAnswerByQuestion(String username, String question, String answer) {
		Integer isTrue = userMapper.checkAnswerByQuestion(username, question, answer);
		if(isTrue == 0){
			return ServerResponse.createByErrorMessage("问题答案错误,请从新输入");
		}
		String token = UUID.randomUUID().toString();//
		TokenCache.setKey(Const.FORGET_TOKEN_PREFIX+username,token);	//将token放到本地缓存
		return ServerResponse.createBySuccess(token);
	}

	@Override
	public ServerResponse<String> ResetPasswordByUsername(String username, String passwordNew) {
		User user = new User();
		Integer userId = userMapper.selectIdByUsername(username);
		user.setId(userId);
		user.setUsername(username);
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		Integer isSuccess = userMapper.updateByPrimaryKeySelective(user);
		if (isSuccess == 0){
			return ServerResponse.createByErrorMessage("修改密码操作失败，请联系管理员");
		}
		return ServerResponse.createBySuccessMessage("修改密码成功");
	}

	@Override
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		if(StringUtils.isBlank(forgetToken)){
			return ServerResponse.createByErrorMessage("参数错误，token需要传递");
		}
		ServerResponse validResponse = this.checkUsernameValid(username,0);
		if (validResponse.isSuccess()){
			return ServerResponse.createByErrorMessage("用户不存在");
		}
		String token = TokenCache.getKey(Const.FORGET_TOKEN_PREFIX+username);
		if (StringUtils.isBlank(token)){
			return ServerResponse.createByErrorMessage("token无效或者过期");
		}
		if (StringUtils.equals(forgetToken,token)){//

			return this.ResetPasswordByUsername(username,passwordNew);
		}else {
			return ServerResponse.createByErrorMessage("token错误，请重新获取");
		}
	}

	@Override
	public ServerResponse<Integer> resetPassword(User currentUser, String passwordOld, String passwordNew) {
		String oldPassword = userMapper.getPasswordById(currentUser.getId());
		if (oldPassword.equals(MD5Util.MD5EncodeUtf8(passwordOld))){
			currentUser.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
			Integer isSuccess = userMapper.updateByPrimaryKeySelective(currentUser);
			if (isSuccess == 0){
				return ServerResponse.createByErrorMessage("修改密码操作失败，请联系管理员");
			}
		}else {
			return ServerResponse.createByErrorMessage("旧密码输入错误，请检查核对");
		}
		return ServerResponse.createBySuccessMessage("修改密码成功");
	}

	@Override
	public ServerResponse<User> updateInformationById(Integer id, User user) {
		//更新个人信息时要校验，第一：username不能被更新，email也要进行一个校验，是否已经存在了，并且要出去当前用户的老email
		int resultCount = userMapper.checkEmailByUserId(user.getEmail(),id);
		if (resultCount > 0){
			return ServerResponse.createByErrorMessage("email已经存在，请更换email后再尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(id);
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());

		Integer isSuccess = userMapper.updateByPrimaryKeySelective(updateUser);
		if(isSuccess > 0){
			return ServerResponse.createBySuccessMessage("更新信息成功");
		}
		return ServerResponse.createByErrorMessage("更新失败，请联系管理员");
	}


	//backEnd后台代码

	@Override
	public ServerResponse checkAdminRole(User user) {
		if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}
}
