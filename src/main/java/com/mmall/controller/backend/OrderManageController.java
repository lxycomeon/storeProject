package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
	public ServerResponse listOrder(HttpSession session ,
									@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
									@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iOrderService.manageListOrder(pageNum,pageSize);
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse<PageInfo> searchOrder(HttpSession session , Long orderNo,
												@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
												@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iOrderService.manageSearch(orderNo,pageNum,pageSize);
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session ,Long orderNo){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iOrderService.manageGetDetailByOrderNo(orderNo);
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("send_goods.do")
	@ResponseBody
	public ServerResponse<String> orderSendGoods(HttpSession session ,Long orderNo){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iOrderService.manageSendGoods(orderNo);
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}



}
