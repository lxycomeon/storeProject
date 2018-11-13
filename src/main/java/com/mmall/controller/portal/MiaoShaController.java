package com.mmall.controller.portal;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IProductService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/8
 * 秒杀系统controller
 */
@Controller
@RequestMapping("/miaosha/")
public class MiaoShaController implements InitializingBean {


	@Autowired
	IProductService iProductService;

	@Autowired
	IOrderService iOrderService;

	//系统初始化的时候，将秒杀商品的库存存入里面
	@Override
	public void afterPropertiesSet() throws Exception {

	}

	//列出秒杀商品
	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse listProduct(HttpServletRequest httpServletRequest){
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
			response = iProductService.listMiaoshaProduct();
		}

		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse detailProduct(HttpServletRequest httpServletRequest,Integer miaoshaProductId){
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
			response = iProductService.getMiaoshaProductDetailById(miaoshaProductId);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("createOrder.do")
	@ResponseBody
	public ServerResponse creatMiaoshaOrder(HttpServletRequest httpServletRequest,Integer miaoshaProductId,Integer shippingId){	//传回的是秒杀列表中的id
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
			response =iOrderService.createMiaoshaOrder(user.getId(),shippingId,miaoshaProductId );
		}
		System.out.println(response);
		return response;
	}




}
