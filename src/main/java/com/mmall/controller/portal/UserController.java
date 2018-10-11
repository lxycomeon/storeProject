package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/10
 * Time: 15:51
 * portal门户的用户模块的接口，登陆后返回用户信息
 * 接口的设计文档：https://gitee.com/imooccode/happymmallwiki/wikis/%E9%97%A8%E6%88%B7_%E7%94%A8%E6%88%B7%E6%8E%A5%E5%8F%A3?sort_id=9917
 */
@Controller
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private IUserService iUserService;

	//对于/user/login.do的请求，只过滤POST请求
	@RequestMapping(value = "login.do")//method = RequestMethod.POST
	@ResponseBody					//返回时自动利用spring的jackon插件将返回结果自动转换为json
	public ServerResponse<User> login(String username, String password, HttpSession session){

		ServerResponse<User> response = iUserService.login(username,password);

		if (response.isSuccess()){
			session.setAttribute(Const.CURRENT_USER,response.getData());
			session.setMaxInactiveInterval(3600);	//登陆有效期3600s
		}

		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "register.do")
	@ResponseBody
	public ServerResponse<Integer> register(User user){
		ServerResponse<Integer> response = iUserService.register(user);

		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "check_valid.do")
	@ResponseBody
	public ServerResponse<Integer> checkUsernameValid(String str,Integer type){
		ServerResponse<Integer> response = iUserService.checkUsernameValid(str,type);
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "get_user_info.do")
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session ){
		User user = (User) session.getAttribute(Const.CURRENT_USER);	//获取session
		ServerResponse<User> response =null;
		if(user != null) {
			response = ServerResponse.createBySuccess(user);
		}else {
			response = ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "forget_get_question.do")
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
		ServerResponse<String> response = iUserService.getForgetQuestionByUsername(username);
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "forget_check_answer.do")
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer, HttpSession session){
		ServerResponse<String> response = iUserService.checkAnswerByQuestion(username,question,answer);
		if( ResponseCode.SUCCESS.getCode() == response.getStatus()){	//验证true
			session.setAttribute(Const.FORGET_TOKEN, response.getData());
			session.setMaxInactiveInterval(120);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "forget_reset_password.do")
	@ResponseBody
	public ServerResponse<Integer> forgetResetPassword(String username,String passwordNew,String forgetToken,HttpSession session){
		String serToken = (String) session.getAttribute(Const.FORGET_TOKEN);
		session.removeAttribute(Const.FORGET_TOKEN);
		Enumeration<String> seName = session.getAttributeNames();
		ServerResponse<Integer> response = null;
		if (serToken != null && serToken.equals(forgetToken) ) {
			response = iUserService.ResetPasswordByUsername(username,passwordNew);
		}else {
			response = ServerResponse.createByErrorMessage("token已经失效");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "reset_password.do")
	@ResponseBody
	public ServerResponse<Integer> resetPassword(String passwordOld,String passwordNew,HttpSession session){
		ServerResponse<Integer> response = null;
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);	//session中为了保护用户隐私，是不包含密码的
		//所以要将这些业务逻辑传回service层重新比对旧密码。
		response = iUserService.resetPassword(currentUser,passwordOld,passwordNew);

		System.out.println(response);
		return response;
	}

	@RequestMapping("update_information.do")
	@ResponseBody
	public ServerResponse<Integer> updateInformation(User user,HttpSession session){
		ServerResponse<Integer> response = null;
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			response = ServerResponse.createByErrorMessage("更新失败，用户未登陆");
		}
		else{
			response = iUserService.updateInformationById(currentUser.getId(),user);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("logout.do")
	@ResponseBody
	public ServerResponse<Integer> logout(HttpSession session){
		ServerResponse<Integer> response = null;
		session.removeAttribute(Const.CURRENT_USER);
		if(session.getAttribute(Const.CURRENT_USER) == null){
			response = ServerResponse.createBySuccessMessage("退出成功");
		}
		else{
			response = ServerResponse.createByErrorMessage("服务端异常");
		}
		System.out.println(response);
		return  response;
	}

	@RequestMapping("get_information.do")
	@ResponseBody
	public ServerResponse<User> getInformation(HttpSession session){
		ServerResponse<User> response = null;
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if (currentUser == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		else{
			response = ServerResponse.createBySuccess(currentUser);
		}
		System.out.println(response);
		return response;
	}

}
