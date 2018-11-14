package com.mmall.common;

import com.mmall.pojo.MiaoshaOrder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/10
 * Time: 16:07
 * 一个通用的系统响应，T泛型作为返回不同类型data的设计
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//此注解，如果返回的json的时候，如果是null的对象，key也会消失
public class ServerResponse<T> implements Serializable {

	private int status;
	private String msg;
	private T data;

	//私有构造器，便于一会方便封装public方法
	private ServerResponse(int status){
		this.status = status;
	}
	private ServerResponse(int status,T data){
		this.status = status;
		this.data	= data;
	}
	private ServerResponse(int status,String msg,T data){
		this.status = status;
		this.data	= data;
		this.msg	= msg;
	}
	private ServerResponse(int status,String msg){
		this.status = status;
		this.msg	= msg;
	}

	public static <T> ServerResponse<T> createByResponseCode(ResponseCode responseCode) {
		return new ServerResponse<T>(responseCode.getCode(),responseCode.getDesc());
	}

	public static <T> ServerResponse<T> createByResponseCodeAndData(ResponseCode responseCode, T data) {
		return new ServerResponse<T>(responseCode.getCode(),responseCode.getDesc(),data);
	}


	@JsonIgnore			//此注解，可以在返回时，不把status序列化，忽略
	public boolean isSuccess(){
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	public int getStatus() {
		return status;
	}
	public String getMsg() {
		return msg;
	}
	public T getData() {
		return data;
	}

	public static <T> ServerResponse<T> createBySuccess(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}
	public static <T> ServerResponse<T> createBySuccessMessage(String msg){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}
	public static <T> ServerResponse<T> createBySuccess(T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	public static <T> ServerResponse<T> createBySuccess(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
	}

	public static <T> ServerResponse<T> createByError(){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
	}
	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
	}
	public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
		return new ServerResponse<T>(errorCode,errorMessage);
	}

	@Override
	public String toString() {
		return "ServerResponse{" +
				"status=" + status +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
