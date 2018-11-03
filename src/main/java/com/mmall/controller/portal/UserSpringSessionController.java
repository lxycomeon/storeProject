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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * 测试使用springSession框架，实现单点登陆，对业务代码无侵入性，也即是仍使用原来的session进行业务代码，该框架就会自动将session信息保存到配置的redis中，但这个版本不支持分片的redis
 */
@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {

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


	@RequestMapping(value = "get_user_info.do")
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session ){

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse<User> response =null;
		if(user != null) {
			response = ServerResponse.createBySuccess(user);
		}else {
			response = ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
		}
		System.out.println(response);
		return response;
	}







	@RequestMapping("logout.do")
	@ResponseBody
	public ServerResponse<Integer> logout( HttpSession session){
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


}


