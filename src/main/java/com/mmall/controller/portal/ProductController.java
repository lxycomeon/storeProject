package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

	@Autowired
	IProductService iProductService;

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse listProduct(@RequestParam(value ="categoryId",required = false) Integer categoryId,
									  @RequestParam(value ="keyword",required = false)String keyword,
									  @RequestParam(value = "orderBy",defaultValue = "price_asc") String orderBy,
									  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
									  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
		ServerResponse response = iProductService.listProduct(categoryId,keyword,orderBy,pageNum,pageSize);
		System.out.println(response);
		return response;
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse detailProduct(Integer productId){
		ServerResponse response = iProductService.getProductDetailById(productId);
		System.out.println(response);
		return response;
	}

}
