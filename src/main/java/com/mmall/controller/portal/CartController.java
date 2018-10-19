package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/19
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

	@Autowired
	private ICartService iCartService;

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse listCart(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iCartService.listCartByUserId(user.getId());
		}

		System.out.println(response);
		return response;
	}

	@RequestMapping("add.do")
	@ResponseBody
	public ServerResponse addProduct(HttpSession session,Integer productId,Integer count){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;

		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iCartService.addProduct(productId,count,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("update.do")
	@ResponseBody
	public ServerResponse updateProductCount(HttpSession session,Integer productId,Integer count){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;

		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iCartService.updateProductCount(productId,count,user.getId());
		}
		System.out.println(response);
		return response;
	}




}
