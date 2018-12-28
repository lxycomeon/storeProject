package com.mmall.util;

import lombok.ConfigurationKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/29
 */
@Slf4j
public class CookieUtil {

	private final static String COOKIE_DOMAIN = "192.168.50.111";
	private final static String COOKIE_NAME = "mmall_login_token";

	//a:


	public static void writeLoginToken(HttpServletResponse response,String token){
		Cookie ck = new Cookie(COOKIE_NAME,token);
		ck.setDomain(COOKIE_DOMAIN);
		ck.setPath("/");
		ck.setHttpOnly(true);	//防止脚本攻击，的访问允许。无法通过脚本发送给第三方

		//单位是秒
		//如果这个setMaxAge不设置的话，cookie就不会写入硬盘，而是写在内存中，只是在当前页面有效。
		ck.setMaxAge(60 * 60 * 24 * 365);	//如果是-1代表永久
		log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
		response.addCookie(ck);
	}

	public static String readLoginToken(HttpServletRequest request){
		Cookie[] cks = request.getCookies();
		if (cks != null){
			for (Cookie ck:cks) {
				log.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
				if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
					log.info("return cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
					return ck.getValue();
				}

			}
		}
		return null;
	}

	public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
		Cookie[] cks = request.getCookies();
		if (cks != null){
			for (Cookie ck:cks) {
				if (StringUtils.equals(ck.getName(),COOKIE_NAME)){
					ck.setDomain(COOKIE_DOMAIN);
					ck.setPath("/");
					ck.setMaxAge(0);//设置为0，代表删除此cookie
					log.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
					response.addCookie(ck);
					return;
				}
			}
		}


	}





}
