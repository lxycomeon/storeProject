package com.mmall.controller.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/16
 */
@Slf4j
public class CrossDomainInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//请求中Controller中的方法名
		HandlerMethod handlerMethod = (HandlerMethod)handler;

		//解析HandlerMethod，
		String methodName = handlerMethod.getMethod().getName();
		//getSimpleName也即是只获取一个最后的类名，不包括包名
		String className = handlerMethod.getBean().getClass().getSimpleName();

		StringBuffer requestParamBuffer = new StringBuffer();
		Map paramMap = request.getParameterMap();
		Iterator it = paramMap.entrySet().iterator();
		while (it.hasNext()){
			//提取请求的路径以及参数等信息
			Map.Entry entry = (Map.Entry) it.next();
			String mapKey = (String) entry.getKey();
			String mapValue = StringUtils.EMPTY;
			Object obj = entry.getValue();
			if (obj instanceof String[]){
				String[] strs = (String[]) obj;
				mapValue = Arrays.toString(strs);
			}
			requestParamBuffer.append(mapKey).append(" ").append(mapValue);
		}
		log.info("跨域拦截器拦截到请求，className：{}，methodName:{},param:{}",className,methodName,requestParamBuffer.toString());
		//response.setHeader("withCredentials", "true");
		//解决跨域请求
		response.setHeader("Access-Control-Allow-Origin", "http://192.168.50.118:8089");
	//	response.setHeader("Access-Control-Allow-Credentials", "true");
	//	response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	//	response.setHeader("Access-Control-Max-Age", "3600");
	//	response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");




		return true;	//返回true的时候才会继续进行后面的controller方法的执行
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

	}
}
