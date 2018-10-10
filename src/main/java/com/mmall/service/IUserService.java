package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/10
 * Time: 16:04
 */
public interface IUserService {

	ServerResponse<User> login(String username, String password);

	ServerResponse<Integer> register(User user);

	ServerResponse<Integer> checkUsernameValid(String str, Integer type);


}
