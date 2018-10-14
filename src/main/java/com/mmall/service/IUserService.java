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

	ServerResponse<String> getForgetQuestionByUsername(String username);

	ServerResponse<String> checkAnswerByQuestion(String username, String question, String answer);

	ServerResponse<String> ResetPasswordByUsername(String username, String passwordNew);

	ServerResponse<Integer> resetPassword(User currentUser, String passwordOld, String passwordNew);

	ServerResponse<User> updateInformationById(Integer id, User user);

	ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);
}
