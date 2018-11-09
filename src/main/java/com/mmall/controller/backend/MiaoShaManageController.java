package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.MiaoshaProduct;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/8
 */
@Controller
@RequestMapping("/manage/miaosha/")
public class MiaoShaManageController {

	@Autowired
	IProductService iProductService;

	//添加秒杀商品
	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse saveOrUpdateMiaoshaProduct(MiaoshaProduct miaoshaProduct){
		ServerResponse response = null;
		response = iProductService.saveOrUpdateMiaoshaProduct(miaoshaProduct);
		System.out.println(response);
		return response;
	}



}
