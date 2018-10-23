package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/22
 */
@Controller
@RequestMapping("/shipping/")
public class shippingController {

	@Autowired
	IShippingService iShippingService;


	@RequestMapping("add.do")
	@ResponseBody
	public ServerResponse add(HttpSession session, Shipping shipping){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;

		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iShippingService.addShipping(shipping,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("del.do")
	@ResponseBody
	public ServerResponse delete(HttpSession session, Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iShippingService.deleteSippingByIdAndUserId(shippingId,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("update.do")
	@ResponseBody
	public ServerResponse update(HttpSession session, Shipping shipping ){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iShippingService.updateShippingByUserId(shipping,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("select.do")
	@ResponseBody
	public ServerResponse selectById(HttpSession session, Integer shippingId  ){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iShippingService.selectByIdAndUserId(shippingId,user.getId());
		}
		System.out.println(response);
		return response;
	}
	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse listByUserId(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
									   @RequestParam(value = "pageSize",defaultValue = "10") int pageSize  ){

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse response = null;
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iShippingService.listByUserId(pageNum,pageSize,user.getId());
		}
		System.out.println(response);
		return response;
	}




}
