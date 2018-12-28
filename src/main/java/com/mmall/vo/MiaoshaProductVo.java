package com.mmall.vo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Queue;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/8
 */
public class MiaoshaProductVo {

	private Integer id;
	private Integer productId;
	private String name;
	private String productMainImage;
	private BigDecimal productPrice;
	private BigDecimal miaoshaPrice;
	private Integer miaoshaStock;

	private String imageHost;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public BigDecimal getMiaoshaPrice() {
		return miaoshaPrice;
	}

	public void setMiaoshaPrice(BigDecimal miaoshaPrice) {
		this.miaoshaPrice = miaoshaPrice;
	}

	public Integer getMiaoshaStock() {
		return miaoshaStock;
	}

	public void setMiaoshaStock(Integer miaoshaStock) {
		this.miaoshaStock = miaoshaStock;
	}

	public String getImageHost() {
		return imageHost;
	}

	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
}
