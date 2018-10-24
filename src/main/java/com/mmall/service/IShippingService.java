package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/22
 */
public interface IShippingService {

	ServerResponse addShipping(Shipping shipping,Integer userId);

	ServerResponse deleteSippingByIdAndUserId(Integer shippingId,Integer userId);

	ServerResponse updateShippingByUserId(Shipping shipping, Integer id);

	ServerResponse<Shipping> selectByIdAndUserId(Integer shippingId, Integer id);

	ServerResponse<PageInfo> listByUserId(int pageNum, int pageSize, Integer userId);

}
