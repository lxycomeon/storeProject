package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/23
 */
public interface IOrderService {

	ServerResponse pay(Long orderNo,String path);

	ServerResponse aliCallback(Map<String, String> params);

	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

}
