package com.mmall.vo;

import com.mmall.common.Const;
import com.mmall.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/19
 */
public class CartListVo {

	private List<CartProductVo> cartProductVoList;
	private boolean allChecked;
	private BigDecimal cartTotalPrice;
	private String imageHost;

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
			if (cartProductItem.getProductChecked() == Const.ProductCheckStatus.SELECT)	//compute checked product total price
				total = BigDecimalUtil.add(total.doubleValue(),cartProductItem.getProductTotalPrice().doubleValue());
				//total = total.add(cartProductItem.getProductTotalPrice());
		}

		return total;
	}

	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}

	public String getImageHost() {
		return imageHost;
	}

	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
}
