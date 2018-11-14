package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.MiaoshaProduct;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 */
public interface IProductService {

	ServerResponse saveOrUpdateProduct(Product product);

	ServerResponse updateSaleStatus(Integer productId, Integer status);

	ServerResponse<ProductDetailVo> getProductById(Integer productId);

	ServerResponse<ProductDetailVo> getProductDetailById(Integer productId);

	ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

	ServerResponse<PageInfo> searchProduct(int pageNum, int pageSize, String productName, String productId);

	ServerResponse listProduct(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize);

	ServerResponse saveOrUpdateMiaoshaProduct(MiaoshaProduct miaoshaProduct);

	ServerResponse listMiaoshaProduct();

	ServerResponse getMiaoshaProductDetailById(Integer miaoshaProductId);

	List<MiaoshaProduct> selectAllProduct();

}
