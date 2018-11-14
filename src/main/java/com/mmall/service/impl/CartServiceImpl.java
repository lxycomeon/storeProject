package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CartListVo;
import com.mmall.vo.CartProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/19
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;

	@Override
	public ServerResponse listCartByUserId(Integer userId) {
		List<Cart> cartList = cartMapper.selectByUserId(userId);
		List<CartProductVo> cartProductVoList = Lists.newArrayList();
		boolean allChecked = true;	//是否购物车中的产品是否allChecked

		for (Cart cartItem:cartList) {
			CartProductVo cartProductVo = new CartProductVo();
			Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
			cartProductVo.setId(cartItem.getId());
			//cartProductVo.setQuantity(cartItem.getQuantity());
			cartProductVo.setUserId(cartItem.getUserId());
			cartProductVo.setProductId(cartItem.getProductId());
			if (product != null){
				cartProductVo.setProductMainImage(product.getMainImage());
				cartProductVo.setProductName(product.getName());
				cartProductVo.setProductPrice(product.getPrice());
				cartProductVo.setProductStatus(product.getStatus());
				cartProductVo.setProductStock(product.getStock());
				cartProductVo.setProductSubtitle(product.getSubtitle());
				cartProductVo.setProductChecked(cartItem.getChecked());
				if (product.getStock() >= cartItem.getQuantity()){	//检查库存是否充足
					cartProductVo.setLimitQuantity(Const.LimitQuantity.LIMIT_NUM_SUCCESS);
					cartProductVo.setQuantity(cartItem.getQuantity());
				}else {
					cartProductVo.setLimitQuantity(Const.LimitQuantity.LIMIT_NUM_FAIL);
					//做一个购物车中有效库存的更新
					Cart cartForQuantity = new Cart();
					cartForQuantity.setId(cartItem.getId());
					cartForQuantity.setQuantity(product.getStock());	//更新为最大库存
					cartMapper.updateByPrimaryKeySelective(cartForQuantity);	//更新
					cartProductVo.setQuantity(product.getStock());
				}
			}
			if (cartProductVo.getProductChecked() != 1){
				allChecked = false;
			}
			cartProductVo.setProductTotalPrice(cartProductVo.getProductTotalPrice());


			cartProductVoList.add(cartProductVo);
		}
		CartListVo cartListVo =new CartListVo();
		cartListVo.setCartProductVoList(cartProductVoList);
		cartListVo.setAllChecked(allChecked);	//这里可以写成查询sql中。
		cartListVo.setCartTotalPrice(cartListVo.getCartTotalPrice());
		cartListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

		return ServerResponse.createBySuccess(cartListVo);
	}


	@Override
	public ServerResponse addProduct(Integer productId, Integer count, Integer userId) {
		if (productId == null || count == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
			return ServerResponse.createByErrorMessage("此产品已经删除或者下架");
		}
		RedisPoolUtil.expire(Const.RedisCacheName.REDIS_CACHE_CART_PRODUCT_COUNT+userId,0);
		Cart cartItem = cartMapper.selectByUserIdAndProduct(productId,userId);	//查询此用户是否已经添加过此商品
		if (cartItem == null) {
			Cart cart = new Cart();
			cart.setUserId(userId);
			cart.setProductId(productId);
			cart.setQuantity(count);
			cart.setChecked(Const.ProductCheckStatus.SELECT);
			int rowCount = cartMapper.insertSelective(cart);
			if (rowCount > 0) {
				return listCartByUserId(userId);
			} else {
				return ServerResponse.createByErrorMessage("添加商品失败");
			}
		}else {
				return updateProductCount(cartItem.getProductId(),cartItem.getQuantity() + count,cartItem.getUserId());
		}
	}

	@Override
	public ServerResponse updateProductCount(Integer productId, Integer count, Integer userId) {
		Cart cartItem = cartMapper.selectByUserIdAndProduct(productId,userId);
		if (cartItem == null){
			return ServerResponse.createByErrorMessage("参数输入有误");
		}
		cartItem.setQuantity(count);
		int rowCount = cartMapper.updateByPrimaryKeySelective(cartItem);
		if (rowCount > 0){
			return listCartByUserId(userId);
		}
		return ServerResponse.createByErrorMessage("数量更新失败");
	}

	@Override
	public ServerResponse deleteProductByIds(String productIds, Integer userId) {
		String[] ids = productIds.split(",");
		if (ids.length <= 0){
			return ServerResponse.createByErrorMessage("参数输入有误");
		}
		//此处删除多个id ，也可以直接写一个sql删除
		for (String id : ids) {
			Cart cart = cartMapper.selectByUserIdAndProduct(Integer.parseInt(id), userId);
			if (cart == null)
				return ServerResponse.createByErrorMessage("未能查到此项");
			int rowCount = cartMapper.deleteByPrimaryKey(cart.getId());
			if (rowCount <= 0)
				return ServerResponse.createByErrorMessage("删除购物车商品项失败");
		}
		return listCartByUserId(userId);
	}

	@Override
	public ServerResponse updateSelectProduct(Integer productId, Integer userId,Integer CheckedStatus) {
		Cart cartItem = cartMapper.selectByUserIdAndProduct(productId,userId);
		if (cartItem == null){
			return ServerResponse.createByErrorMessage("参数输入有误");
		}
		cartItem.setChecked(CheckedStatus);
		int rowCount = cartMapper.updateByPrimaryKeySelective(cartItem);
		if (rowCount > 0){
			return listCartByUserId(userId);
		}
		return ServerResponse.createByErrorMessage("选中状态更新失败");
	}

	@Override
	public ServerResponse getCartProductCount(Integer userId) {
		if (userId == null){
			return ServerResponse.createBySuccess(0);
		}
		//很多逻辑，如果查询sql可以直接解决的话，不要再写一些业务逻辑去计算，这样会造成程序冗杂。比如此处
//		List<Cart> cartList = cartMapper.selectByUserId(userId);
//		int totalProductCount = 0;
//		for (Cart cartItem:cartList) {
//			totalProductCount += cartItem.getQuantity();
//		}
		int totalProductCount;
		String jsonStr = RedisPoolUtil.get(Const.RedisCacheName.REDIS_CACHE_CART_PRODUCT_COUNT+userId);
		if (jsonStr != null){
			totalProductCount = Integer.parseInt(jsonStr);
		}else {
			totalProductCount = cartMapper.selectProductCountByUserId(userId);
			RedisPoolUtil.setEx(Const.RedisCacheName.REDIS_CACHE_CART_PRODUCT_COUNT+userId,totalProductCount+"",180);
		}

		return ServerResponse.createBySuccess(totalProductCount);
	}

	@Override
	public ServerResponse updateSelectAllProduct(Integer userId, Integer selectStatus) {
		List<Cart> cartList = cartMapper.selectByUserId(userId);
		for (Cart cartItem:cartList) {
			cartItem.setChecked(selectStatus);
			int rowCount = cartMapper.updateByPrimaryKeySelective(cartItem);
			if (rowCount <= 0){
				return ServerResponse.createByErrorMessage("更新选中状态失败");
			}
		}
		return listCartByUserId(userId);
	}


}
