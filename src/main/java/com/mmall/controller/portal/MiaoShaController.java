package com.mmall.controller.portal;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.MiaoshaOrder;
import com.mmall.pojo.MiaoshaProduct;
import com.mmall.pojo.User;
import com.mmall.rabbitmq.MQSender;
import com.mmall.rabbitmq.MiaoshaMessage;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/8
 * 秒杀系统controller
 */
@Controller
@RequestMapping("/miaosha/")
public class MiaoShaController  implements InitializingBean{


	@Autowired
	IProductService iProductService;

	@Autowired
	IOrderService iOrderService;

	@Autowired
	MQSender sender;


	//缓存订单数量状态
	HashMap<Integer,Boolean> productStockStatus = Maps.newHashMap();

	@Override
	public void afterPropertiesSet() {
		List<MiaoshaProduct> list = iProductService.selectAllProduct();
		for (MiaoshaProduct Item:list) {
			RedisPoolUtil.set(Const.REDIS_MIAOSHA_PRODUCT_STOCK_PREFIX+Item.getId(),Item.getMiaoshaStock().toString());
			if (Item.getMiaoshaStock()>0){
				productStockStatus.put(Item.getId(),true);
			}
		}
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
			////使用消息队列异步下单，减少阻塞时间
			if (!productStockStatus.get(miaoshaProductId)){
				return ServerResponse.createByErrorMessage("商品已经秒杀完毕");
			}
			//预减redis库存
			Long productStock = RedisPoolUtil.decr(Const.REDIS_MIAOSHA_PRODUCT_STOCK_PREFIX+miaoshaProductId);
			if (productStock < 0 ){
				productStockStatus.put(miaoshaProductId,false);
				return  ServerResponse.createByErrorMessage("商品已经秒杀完毕");
			}
			String OrderjsonStr = RedisPoolUtil.get(Const.REDIS_MIAOSHA_ORDER_PREFIX+user.getId()+"_"+miaoshaProductId);
			MiaoshaOrder miaoshaOrder = JsonUtil.string2Obj(OrderjsonStr,MiaoshaOrder.class);
			if (miaoshaOrder != null){
				return  ServerResponse.createByResponseCodeAndData(ResponseCode.MIAOSHA_SUCCESS,miaoshaOrder);
			}
			//消息入队
			sender.sendMiaoshaMessage(new MiaoshaMessage(user.getId(),miaoshaProductId,shippingId));

			return  ServerResponse.createByResponseCode(ResponseCode.WAIT_MIAOSHA_RESULT);
			//-------------------以上为使用消息队列进行秒杀下单，减少阻塞-----------------------------------;

			//原生下单方式
//			response =iOrderService.createMiaoshaOrder(user.getId(),shippingId,miaoshaProductId );
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("queryResult.do")
	@ResponseBody
	public ServerResponse result(HttpServletRequest httpServletRequest,Integer miaoshaProductId){
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
			response = iOrderService.queryMiaoshaResult(user.getId(),miaoshaProductId);
		}
		System.out.println(response);
		return response;
	}






}
