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

}
