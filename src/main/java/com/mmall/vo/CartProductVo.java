package com.mmall.vo;

import com.mmall.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/19
 */
public class CartProductVo {
	private Integer id;

	private Integer userId;

	private Integer productId;

	private Integer quantity;

	private String productName;
	private String productSubtitle;
	private String productMainImage;
	private BigDecimal productPrice;
	private Integer productStatus;
	private BigDecimal productTotalPrice;
	private Integer productStock;
	private Integer productChecked;

	private String limitQuantity;

	public CartProductVo() {
	}

	public CartProductVo(Integer id, Integer userId, Integer productId, Integer quantity, String productName, String productSubtitle, String productMainImage, BigDecimal productPrice, Integer productStatus, BigDecimal productTotalPrice, Integer productStock, Integer productChecked, String limitQuantity) {
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.quantity = quantity;
		this.productName = productName;
		this.productSubtitle = productSubtitle;
		this.productMainImage = productMainImage;
		this.productPrice = productPrice;
		this.productStatus = productStatus;
		this.productTotalPrice = productPrice.multiply(BigDecimal.valueOf(quantity));
		this.productStock = productStock;
		this.productChecked = productChecked;
		this.limitQuantity = limitQuantity;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductSubtitle() {
		return productSubtitle;
	}

	public void setProductSubtitle(String productSubtitle) {
		this.productSubtitle = productSubtitle;
	}

	public String getProductMainImage() {
		return productMainImage;
	}

	public void setProductMainImage(String productMainImage) {
		this.productMainImage = productMainImage;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}

	public BigDecimal getProductTotalPrice() {	//避免丢失精度
		return BigDecimalUtil.mul(productPrice.doubleValue(),quantity);//productPrice.multiply(BigDecimal.valueOf(quantity));
	}

	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice =  productTotalPrice;
	}

	public Integer getProductStock() {
		return productStock;
	}

	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}

	public Integer getProductChecked() {
		return productChecked;
	}

	public void setProductChecked(Integer productChecked) {
		this.productChecked = productChecked;
	}

	public String getLimitQuantity() {
		return limitQuantity;
	}

	public void setLimitQuantity(String limitQuantity) {
		this.limitQuantity = limitQuantity;
	}
}
