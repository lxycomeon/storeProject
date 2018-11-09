package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderListVo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/23
 */
public interface IOrderService {

	ServerResponse pay(Long orderNo,String path);

	ServerResponse aliCallback(Map<String, String> params);

	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServerResponse createOrder(Integer id, Integer shippingId);

	ServerResponse<PageInfo> listOrder(Integer userId, int pageNum, int pageSize);

	OrderItemVo assembleOrderItemVo(OrderItem orderItemItem);

	OrderListVo assembleOrderListVoByOrder(Order orderItem);

	ServerResponse getDetailByOrderNo(Integer userId, Long orderNo);

	ServerResponse cancelOrder(Integer userId, Long orderNo);

	ServerResponse getOrderCartProduct(Integer userId);

	//backend

	ServerResponse<PageInfo> manageListOrder(int pageNum,int pageSize);

	ServerResponse manageGetDetailByOrderNo( Long orderNo);

	ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

	ServerResponse manageSendGoods(Long orderNo);


	//定时关闭订单,传入前多长时间未付款的订单进行关闭
	void closeOrder(int hour);

	//秒杀订单
	ServerResponse createMiaoshaOrder(Integer id, Integer shippingId, Integer miaoshaProductId);

}
