package com.mmall.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 */
public class MiaoshaProductDetailVo {
	private Integer id;
	private Integer categoryId;
	private String name;
	private String subtitle;
	private String mainImage;
	private String subImages;
	private String detail;
	private BigDecimal productPrice;
	private BigDecimal miaoshaPrice;
	private Integer miaoshaStock;

	private Integer miaoshaStatus;
	private Integer remainSeconds;
	private String startTime;
	private String endTime;

	private Date createTime;
	private Date updateTime;

	private String imageHost;
	private Integer parentCategoryId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public String getSubImages() {
		return subImages;
	}

	public void setSubImages(String subImages) {
		this.subImages = subImages;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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

	public Integer getMiaoshaStatus() {
		return miaoshaStatus;
	}

	public void setMiaoshaStatus(Integer miaoshaStatus) {
		this.miaoshaStatus = miaoshaStatus;
	}

	public Integer getRemainSeconds() {
		return remainSeconds;
	}

	public void setRemainSeconds(Integer remainSeconds) {
		this.remainSeconds = remainSeconds;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getImageHost() {
		return imageHost;
	}

	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}

	public Integer getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
}
