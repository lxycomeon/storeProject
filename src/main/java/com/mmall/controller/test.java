package com.mmall.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;


/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/10
 * Time: 14:29
 */
@Controller
@RequestMapping("/test")
public class test {



	@RequestMapping(value = "test1.do")
	public String test1(){

		System.out.print("test");
		return "text";
	}

	public static void main(String[] args) {
		BigDecimal a = new BigDecimal("0.5");
		BigDecimal b = new BigDecimal("0.8");
		System.out.println(a.add(b));
	}

}
