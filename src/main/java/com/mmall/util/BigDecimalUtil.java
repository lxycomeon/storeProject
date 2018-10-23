package com.mmall.util;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/22
 */
public class BigDecimalUtil {

	private BigDecimalUtil(){}	//使其不能够在外部使用

	public static BigDecimal add(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2);	//add方法会返回一个新的bigDecimal的对象，其本身b1的值并没有改变
	}
	public static BigDecimal sub(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2);
	}
	public static BigDecimal mul(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2);
	}
	public static BigDecimal div(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);	//四舍五入，保留两位小数
	}


}
