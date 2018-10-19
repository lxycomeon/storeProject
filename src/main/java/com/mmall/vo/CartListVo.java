package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/19
 */
public class CartListVo {

	private List<CartProductVo> cartProductVoList;
	private boolean allChecked;
	private BigDecimal cartTotalPrice;

	public CartListVo() {
	}

	public CartListVo(List<CartProductVo> cartProductVoList, boolean allChecked, BigDecimal cartTotalPrice) {
		this.cartProductVoList = cartProductVoList;
		this.allChecked = allChecked;
		this.cartTotalPrice = cartTotalPrice;
	}

	public List<CartProductVo> getCartProductVoList() {
		return cartProductVoList;
	}

	public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
		this.cartProductVoList = cartProductVoList;
	}

	public boolean isAllChecked() {
		return allChecked;
	}

	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}

	public BigDecimal getCartTotalPrice() {
		BigDecimal total = new BigDecimal("0.0");
		for (CartProductVo cartProductItem:cartProductVoList) {
			total = total.add(cartProductItem.getProductTotalPrice());
		}

		return total;
	}

	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}
}
