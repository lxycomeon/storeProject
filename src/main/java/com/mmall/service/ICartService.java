package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/19
 */
public interface ICartService {

	ServerResponse listCartByUserId(Integer id);

	ServerResponse addProduct(Integer productId, Integer count, Integer id);

	ServerResponse updateProductCount(Integer productId, Integer count, Integer id);

}
