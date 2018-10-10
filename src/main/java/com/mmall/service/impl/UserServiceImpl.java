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
public class UserServiceImpl implements IUserService {

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


}
