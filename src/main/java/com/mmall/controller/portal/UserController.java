package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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


}
