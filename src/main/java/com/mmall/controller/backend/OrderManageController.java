package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
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
 * Created with IntelliJ IDEA By lxy on 2018/10/24
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

	@Autowired
	IOrderService iOrderService;

	@Autowired
	IUserService iUserService;

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse listOrder(@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
									@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		ServerResponse response = null;
		response = iOrderService.manageListOrder(pageNum,pageSize);
		System.out.println(response);
		return response;
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse<PageInfo> searchOrder(Long orderNo,
												@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
												@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		ServerResponse response = null;
		response = iOrderService.manageSearch(orderNo,pageNum,pageSize);
		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(Long orderNo){
		ServerResponse response = null;
		response = iOrderService.manageGetDetailByOrderNo(orderNo);
		System.out.println(response);
		return response;
	}

	@RequestMapping("send_goods.do")
	@ResponseBody
	public ServerResponse<String> orderSendGoods(Long orderNo){
		ServerResponse response = null;
		response = iOrderService.manageSendGoods(orderNo);
		System.out.println(response);
		return response;
	}



}
