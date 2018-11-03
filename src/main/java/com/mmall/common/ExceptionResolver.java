package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/31
 * 处理全局异常的类，避免所有的异常信息直接返回到页面之上，并且将ModelAndView转化为一个jsonView给前端进行展示
 * 要注入到spring中的组件中
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {


		ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
		modelAndView.addObject("status",ResponseCode.ERROR.getCode());
		modelAndView.addObject("msg","接口异常，详情请查看服务端日志");
		modelAndView.addObject("data",e.toString());


		//当使用的是Jackson2.x的时候，使用MappingJackson2JsonView
		//将异常发出请求的地址和异常信息堆栈打印到日志当中。
		log.error("{} Exception",httpServletRequest.getRequestURI(),e);
		return modelAndView;
	}
}
