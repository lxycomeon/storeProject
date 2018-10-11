package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
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
public class UserServiceImpl<校验成功> implements IUserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if (resultCount == 0){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//todo 密码登陆MD5
		User user = userMapper.selectLogin(username,password);
		if (user == null){
			return ServerResponse.createByErrorMessage("密码错误");
		}

		user.setPassword(StringUtils.EMPTY);//保护用户隐私，将返回的密码为空，避免被破解
		return ServerResponse.createBySuccess("登陆成功",user);


	}

	@Override
	public ServerResponse<Integer> register(User user) {
		int resultCount = userMapper.checkUsername(user.getUsername());
		if (resultCount == 1){
			return ServerResponse.createByErrorMessage("用户名已经存在,请重新输入");
		}
//		Integer maxId = userMapper.findMaxId();
//		user.setId(maxId+1);
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
		String token = UUID.randomUUID().toString();//TokenProccessor.getInstance().makeToken();
		return ServerResponse.createBySuccess(token);
	}

	@Override
	public ServerResponse<Integer> ResetPasswordByUsername(String username, String passwordNew) {
		User user = new User();
		Integer userId = userMapper.selectIdByUsername(username);
		user.setId(userId);
		user.setUsername(username);
		user.setPassword(passwordNew);
		Integer isSuccess = userMapper.updateByPrimaryKeySelective(user);
		if (isSuccess == 0){
			return ServerResponse.createByErrorMessage("修改密码操作失败，请联系管理员");
		}
		return ServerResponse.createBySuccessMessage("修改密码成功");
	}

	@Override
	public ServerResponse<Integer> resetPassword(User currentUser, String passwordOld, String passwordNew) {
		String oldPassword = userMapper.getPasswordById(currentUser.getId());
		if (oldPassword.equals(passwordOld)){
			currentUser.setPassword(passwordNew);
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
	public ServerResponse<Integer> updateInformationById(Integer id, User user) {
		user.setId(id);
		Integer isSuccess = userMapper.updateByPrimaryKeySelective(user);
		if(isSuccess == 0){
			return ServerResponse.createByErrorMessage("更新失败，请联系管理员");
		}
		return ServerResponse.createBySuccessMessage("更新信息成功");
	}


}
