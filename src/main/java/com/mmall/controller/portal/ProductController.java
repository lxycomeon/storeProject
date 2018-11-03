package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

	//改造资源占位的时候，可以添加一些占位的字段名，避免模糊的资源请求路径。
	@RequestMapping("/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}")
	@ResponseBody
	public ServerResponse listProductRESTful(@PathVariable(value ="categoryId") Integer categoryId,
									  @PathVariable(value ="keyword")String keyword,
									  @PathVariable(value = "orderBy") String orderBy,
									  @PathVariable(value = "pageNum") Integer pageNum,
									  @PathVariable(value = "pageSize") Integer pageSize){
		if (pageNum == null){
			pageNum = 1;
		}
		if (pageSize == null){
			pageSize =10;
		}
		if (StringUtils.isBlank(orderBy)){
			orderBy = "price_asc";
		}

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

	@RequestMapping(value = "/{productId}",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse detailProductRESTful(@PathVariable Integer productId){
		ServerResponse response = iProductService.getProductDetailById(productId);
		System.out.println(response);
		return response;
	}



}
