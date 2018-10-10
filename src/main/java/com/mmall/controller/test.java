package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
