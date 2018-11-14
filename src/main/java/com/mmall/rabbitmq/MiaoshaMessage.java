package com.mmall.rabbitmq;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/13
 */
public class MiaoshaMessage {

	private Integer userId;
	private Integer miaoshaProductId;
	private Integer shippingId;

	public MiaoshaMessage() {
	}

	public MiaoshaMessage(Integer userId, Integer miaoshaProductId, Integer shippingId) {
		this.userId = userId;
		this.miaoshaProductId = miaoshaProductId;
		this.shippingId = shippingId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getMiaoshaProductId() {
		return miaoshaProductId;
	}

	public void setMiaoshaProductId(Integer miaoshaProductId) {
		this.miaoshaProductId = miaoshaProductId;
	}

	public Integer getShippingId() {
		return shippingId;
	}

	public void setShippingId(Integer shippingId) {
		this.shippingId = shippingId;
	}

	@Override
	public String toString() {
		return "MiaoshaMessage{" +
				"userId=" + userId +
				", miaoshaProductId=" + miaoshaProductId +
				", shippingId=" + shippingId +
				'}';
	}
}
