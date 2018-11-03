package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/14
 * Time: 20:03
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
	@Autowired
	private IUserService iUserService;

	@RequestMapping(value = "login.do")//method = RequestMethod.GET,用户相关的全部用get请求
	@ResponseBody                    //返回时自动利用spring的jackon插件将返回结果自动转换为json
	public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){

		ServerResponse<User> response = iUserService.login(username,password);
		if (response.isSuccess()){
			User user = response.getData();
			if (user.getRole() == Const.Role.ROLE_ADMIN){
				//说明登陆的是管理员
				CookieUtil.writeLoginToken(httpServletResponse,session.getId());
				RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
				//session.setAttribute(Const.CURRENT_USER,user);
			}else {
				response = ServerResponse.createByErrorMessage("不是管理员，无法登陆");
			}
		}

		System.out.println(response);
		return response;
	}


}
