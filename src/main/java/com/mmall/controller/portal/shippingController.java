package com.mmall.controller.portal;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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
	public ServerResponse add(Shipping shipping, HttpServletRequest httpServletRequest){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
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
	public ServerResponse delete(HttpServletRequest httpServletRequest , Integer shippingId){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
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
	public ServerResponse update(HttpServletRequest httpServletRequest , Shipping shipping ){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
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
	public ServerResponse selectById(HttpServletRequest httpServletRequest , Integer shippingId  ){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
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
	public ServerResponse listByUserId(HttpServletRequest httpServletRequest , @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
									   @RequestParam(value = "pageSize",defaultValue = "10") int pageSize  ){


		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iShippingService.listByUserId(pageNum,pageSize,user.getId());
		}
		System.out.println(response);
		return response;
	}




}
