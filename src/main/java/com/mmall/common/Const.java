package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/10
 * Time: 17:09
 */
public class Const {
	public static final String CURRENT_USER = "currentUser";
	public static final String FORGET_TOKEN_PREFIX = "forgetToken_";
	public interface RedisCacheName{
		String REDIS_CACHE_MIAOSHA_PRODUCT_LIST = "REDIS_CACHE_MIAOSHA_PRODUCT_LIST";
		String REDIS_CACHE_CART_PRODUCT_COUNT = "REDIS_CACHE_CART_PRODUCT_COUNT";
	}

	public interface RedisCacheExtime{
		int REDIS_SESSION_EXTIME = 60 * 60 * 24*7;//登陆信息7 天有效
		int REDIS_FORGET_TOKEN_EXTIME = 60 * 60;//忘记密码token，1h有效
	}


	public interface Role{
		int ROLE_CUSTOMER = 0;	//普通用户
		int ROLE_ADMIN = 1;		//管理员
	}


	public interface LimitQuantity{
		String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
		String LIMIT_NUM_FAIL  = "LIMIT_NUM_FAIL";
	}

	public interface ProductListOrderBy{
		Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc","stock_desc","stock_asc");
	}

	public interface ProductCheckStatus{
		Integer SELECT = 1;
		Integer UN_SELECT = 0;
	}

	public enum ProductStatusEnum{
		ON_SALE("在售",1);

		private String value;
		private int code;

		ProductStatusEnum(String value, int code) {
			this.value = value;
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}
	}
	public enum MiaoshaProductStatusEnum{
		ON_SALE("秒杀正在进行",0),
		NO_START("秒杀未开始",1),
		END_SALE("秒杀已经结束",2);


		private String value;
		private int code;

		MiaoshaProductStatusEnum(String value, int code) {
			this.value = value;
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}
	}



	public enum OrderStatusEnum{
		CANCELED(0,"已取消"),
		NO_PAY(10,"未支付"),
		PAID(20,"已付款"),
		SHIPPED(40,"已发货"),
		ORDER_SUCCESS(50,"订单完成"),
		ORDER_CLOSE(60,"订单关闭");


		OrderStatusEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}

		public static OrderStatusEnum codeOf(int code){
			for(OrderStatusEnum orderStatusEnum : values()){
				if(orderStatusEnum.getCode() == code){
					return orderStatusEnum;
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}
	}
	public interface  AlipayCallback{
		String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
		String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

		String RESPONSE_SUCCESS = "success";
		String RESPONSE_FAILED = "failed";
	}



	public enum PayPlatformEnum{
		ALIPAY(1,"支付宝");

		PayPlatformEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}
	}

	public enum PaymentTypeEnum{
		ONLINE_PAY(1,"在线支付");

		PaymentTypeEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}


		public static PaymentTypeEnum codeOf(int code){
			for(PaymentTypeEnum paymentTypeEnum : values()){
				if(paymentTypeEnum.getCode() == code){
					return paymentTypeEnum;
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}

	}

	public interface REDIS_LOCK{
//		关闭订单的分布式锁的key
		String CLOSE_OREDER_TASK_LOCK = "CLOSE_OREDER_TASK_LOCK";
	}



}
