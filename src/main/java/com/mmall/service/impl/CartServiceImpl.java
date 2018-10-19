package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
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
			cartProductVo.setQuantity(cartItem.getQuantity());
			cartProductVo.setUserId(cartItem.getUserId());
			cartProductVo.setProductId(cartItem.getProductId());

			cartProductVo.setProductMainImage(product.getMainImage());
			cartProductVo.setProductName(product.getName());
			cartProductVo.setProductPrice(product.getPrice());
			cartProductVo.setProductStatus(product.getStatus());
			cartProductVo.setProductStock(product.getStock());
			cartProductVo.setProductSubtitle(product.getSubtitle());
			cartProductVo.setProductChecked(cartItem.getChecked());
			if (cartProductVo.getProductChecked() != 1){
				allChecked = false;
			}
			cartProductVo.setProductTotalPrice(cartProductVo.getProductTotalPrice());

			if (product.getStock() >= cartItem.getQuantity()){
				cartProductVo.setLimitQuantity(Const.LimitQuantity.LIMIT_NUM_SUCCESS);
			}else {
				cartProductVo.setLimitQuantity(Const.LimitQuantity.LIMIT_NUM_FAIL);
			}
			cartProductVoList.add(cartProductVo);
		}
		CartListVo cartListVo =new CartListVo();
		cartListVo.setCartProductVoList(cartProductVoList);
		cartListVo.setAllChecked(allChecked);
		cartListVo.setCartTotalPrice(cartListVo.getCartTotalPrice());

		return ServerResponse.createBySuccess(cartListVo);
	}


	@Override
	public ServerResponse addProduct(Integer productId, Integer count, Integer userId) {
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
			return ServerResponse.createByErrorMessage("此产品已经删除或者下架");
		}
		Cart cartItem = cartMapper.selectByUserIdAndProduct(productId,userId);	//查询此用户是否已经添加过此商品
		if (cartItem == null) {
			Cart cart = new Cart();
			cart.setUserId(userId);
			cart.setProductId(productId);
			cart.setQuantity(count);
			cart.setChecked(1);
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


}
