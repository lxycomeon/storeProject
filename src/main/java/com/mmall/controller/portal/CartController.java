package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
	public ServerResponse listCart(HttpServletRequest httpServletRequest){
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
			response = iCartService.listCartByUserId(user.getId());
		}

		System.out.println(response);
		return response;
	}

	@RequestMapping("add.do")
	@ResponseBody
	public ServerResponse addProduct(HttpServletRequest httpServletRequest ,Integer productId,Integer count){
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
			response = iCartService.addProduct(productId,count,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("update.do")
	@ResponseBody
	public ServerResponse updateProductCount(HttpServletRequest httpServletRequest ,Integer productId,Integer count){
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
			response = iCartService.updateProductCount(productId,count,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("delete_product.do")
	@ResponseBody
	public ServerResponse deleteProduct(HttpServletRequest httpServletRequest ,String productIds){
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
			response = iCartService.deleteProductByIds(productIds,user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("select.do")
	@ResponseBody
	public ServerResponse selectProduct(HttpServletRequest httpServletRequest,Integer productId){
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
			response = iCartService.updateSelectProduct(productId,user.getId(),Const.ProductCheckStatus.SELECT);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("un_select.do")
	@ResponseBody
	public ServerResponse unSelectProduct(HttpServletRequest httpServletRequest ,Integer productId){
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
			response = iCartService.updateSelectProduct(productId,user.getId(),Const.ProductCheckStatus.UN_SELECT);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("get_cart_product_count.do")
	@ResponseBody
	public ServerResponse getCartProductCount( HttpServletRequest httpServletRequest ){
		ServerResponse response = null;
		String loginToken = CookieUtil.readLoginToken(httpServletRequest);
		if(StringUtils.isBlank(loginToken)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					"用户未登陆，无法获取当前用户信息");
		}
		String jsonStr = RedisPoolUtil.get(loginToken);
		User user = JsonUtil.string2Obj(jsonStr,User.class);

		if (user == null){
			response = ServerResponse.createBySuccess(0);
		}else {
			response = iCartService.getCartProductCount(user.getId());
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("select_all.do")
	@ResponseBody
	public ServerResponse selectAllProduct(HttpServletRequest httpServletRequest ){
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
			response = iCartService.updateSelectAllProduct(user.getId(),Const.ProductCheckStatus.SELECT);
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("un_select_all.do")
	@ResponseBody
	public ServerResponse unSelectAllProduct(HttpServletRequest httpServletRequest  ){
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
			response = iCartService.updateSelectAllProduct(user.getId(),Const.ProductCheckStatus.UN_SELECT);
		}
		System.out.println(response);
		return response;
	}








}
