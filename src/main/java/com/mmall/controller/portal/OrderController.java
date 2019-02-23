package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/23
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

	@Autowired
	IOrderService iOrderService;

	Logger logger = LoggerFactory.getLogger(OrderController.class);


	@RequestMapping("pay.do")
	@ResponseBody
	public ServerResponse pay(Long orderNo, HttpServletRequest request){
		ServerResponse response = null;

		String path = request.getSession().getServletContext().getRealPath("upload");
		response = iOrderService.pay(orderNo,"upload");
		System.out.println(response);
		return response;
	}

	@RequestMapping("alipy_callback.do")
	@ResponseBody
	public Object alipayCallback(HttpServletRequest request){
		Map<String,String> params = Maps.newHashMap();

		Map requestParams = request.getParameterMap();

		for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
			String name = (String)iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for(int i = 0 ; i <values.length;i++){

				valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
			}
			params.put(name,valueStr);
		}
		logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

		//非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

		params.remove("sign_type");
		try {
			boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

			if(!alipayRSACheckedV2){
				return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
			}
		} catch (AlipayApiException e) {
			logger.error("支付宝验证回调异常",e);
		}

		//todo 验证各种返回的订单数据和发出的订单数据是否一致


		//
		ServerResponse serverResponse = iOrderService.aliCallback(params);
		if(serverResponse.isSuccess()){
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallback.RESPONSE_FAILED;
	}

	@RequestMapping("query_order_pay_status.do")
	@ResponseBody
	public ServerResponse queryOrderPayStatus(HttpServletRequest httpServletRequest ,Long orderNo){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("create.do")
	@ResponseBody
	public ServerResponse createOrder(HttpServletRequest httpServletRequest ,Integer shippingId){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iOrderService.createOrder(user.getId(),shippingId);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse listOrder(HttpServletRequest httpServletRequest  ,
									 @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
									 @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iOrderService.listOrder(user.getId(),pageNum,pageSize);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetailByOrderNo(HttpServletRequest httpServletRequest ,Long orderNo){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iOrderService.getDetailByOrderNo(user.getId(),orderNo);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("cancel.do")
	@ResponseBody
	public ServerResponse cancelOrder(HttpServletRequest httpServletRequest ,Long orderNo){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iOrderService.cancelOrder(user.getId(),orderNo);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("get_order_cart_product.do")
	@ResponseBody
	public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest ){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
		}else {
			response = iOrderService.getOrderCartProduct(user.getId());
		}
		System.out.println(response);
		return response;
	}



}
