package com.mmall.controller.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/8
 */
public class MyConverter implements Converter<String, Date> {

	@Override
	public Date convert(String source) {
		// TODO Auto-generated method stub
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		try {
			// 转成直接返回
			return simpleDateFormat.parse(source);
		} catch (Exception e) {

			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return simpleDateFormat.parse(source);
			} catch (Exception e1) {

				System.out.println("日期转换失败->" + this.getClass().getName());
			}
		}
		// 如果参数绑定失败返回null
		return null;
	}

}
