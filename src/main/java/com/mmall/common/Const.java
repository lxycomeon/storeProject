package com.mmall.common;

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

}
