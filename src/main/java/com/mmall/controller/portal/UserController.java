package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.support.UUIDUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

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
	public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){

		ServerResponse<User> response = iUserService.login(username,password);

		if (response.isSuccess()){
//			session.setAttribute(Const.CURRENT_USER,response.getData());
//			session.setMaxInactiveInterval(3600);	//登陆有效期3600s
			//session.getId(),本来的token，为了测试将其改为UUID
			String token = UUID.randomUUID().toString().replace("-","");
			CookieUtil.writeLoginToken(httpServletResponse,token);
			RedisPoolUtil.setEx(token, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
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

	//只用一次
//	@RequestMapping(value = "testCreateUser.do")
//	@ResponseBody
//	public ServerResponse<Integer> testCreateUser(){
//		ServerResponse<Integer> response = iUserService.testCreateUser();
//
//		System.out.println(response);
//		return response;
//	}

	@RequestMapping(value = "check_valid.do" )
	@ResponseBody
	public ServerResponse<Integer> checkUsernameValid(String str,String type){
		ServerResponse<Integer> response = null;
		if (type.equals("username")){
			 response = iUserService.checkUsernameValid(str,0);
		}
		if (type.equals("email")){
			response = iUserService.checkUsernameValid(str,1);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "get_user_info.do")
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
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
		//以下注释代码为，将token放到session中，此处在service层以及利用TokenCache类中方法放入本地缓存了
//		if( ResponseCode.SUCCESS.getCode() == response.getStatus()){	//验证true
//			session.setAttribute(Const.FORGET_TOKEN, response.getData());
//			session.setMaxInactiveInterval(120);
//		}
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "forget_reset_password.do")
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken,
													HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
//		String serToken = (String) session.getAttribute(Const.FORGET_TOKEN);
//		session.removeAttribute(Const.FORGET_TOKEN);
		ServerResponse<String> response = null;
		if (forgetToken != null) {
			//response = iUserService.ResetPasswordByUsername(username,passwordNew);
			response = iUserService.forgetResetPassword(username,passwordNew,forgetToken);
		}else {
			response = ServerResponse.createByErrorMessage("token已经失效");
		}
		logout(httpServletResponse,httpServletRequest);
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "reset_password.do")
	@ResponseBody
	public ServerResponse<Integer> resetPassword(String passwordOld,String passwordNew,HttpServletRequest httpServletRequest){
		ServerResponse<Integer> response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		String jsonStr = RedisPoolUtil.get(loginToken);
		User currentUser = JsonUtil.string2Obj(jsonStr,User.class);
		//所以要将这些业务逻辑传回service层重新比对旧密码。
		if (currentUser ==null){
			response = ServerResponse.createByErrorMessage("用户未登陆");
		}
		response = iUserService.resetPassword(currentUser,passwordOld,passwordNew);

		System.out.println(response);
		return response;
	}

	//更新用户个人信息后，要把更新后的个人信息重新传回session，
	@RequestMapping("update_information.do")
	@ResponseBody
	public ServerResponse<User> updateInformation(User user, HttpServletRequest httpServletRequest){
		ServerResponse<User> response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorMessage("更新失败，用户未登陆");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User currentUser = JsonUtil.string2Obj(jsonStr,User.class);
		user.setUsername(currentUser.getUsername());	//username不能被更新，设置为session里面的username
		if(currentUser == null){
			response = ServerResponse.createByErrorMessage("更新失败，用户未登陆");
		}
		else{
			response = iUserService.updateInformationById(currentUser.getId(),user);
		}
		if (response.isSuccess()){
			response.getData().setUsername(currentUser.getUsername());
			RedisPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("logout.do")
	@ResponseBody
	public ServerResponse<Integer> logout( HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
		ServerResponse<Integer> response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		RedisPoolUtil.del(loginToken);
		CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
		if(RedisPoolUtil.get(loginToken) == null){
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
	public ServerResponse<User> getInformation(HttpServletRequest httpServletRequest ){

		ServerResponse<User> response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User currentUser = JsonUtil.string2Obj(jsonStr,User.class);
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
