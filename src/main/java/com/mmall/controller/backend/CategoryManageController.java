package com.mmall.controller.backend;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/15
 * Time: 20:42
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

	@Autowired
	private IUserService iUserService;
	@Autowired
	private ICategoryService iCategoryService;

	@RequestMapping("add_category.do")
	@ResponseBody
	public ServerResponse addCategory( String categoryName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
		ServerResponse response = null;
		//用户权限验证部分在拦截器中实现，只需要关注业务代码
		if (iCategoryService.checkParentId(parentId).isSuccess()){
			response = iCategoryService.addCategory(categoryName,parentId);
		}
		else{
			response = ServerResponse.createByErrorMessage("没有该父类目录，请重新输入");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("get_category.do")
	@ResponseBody
	public ServerResponse getCategory( @RequestParam(value = "categoryId",defaultValue = "0")int categoryId){
		ServerResponse response = null;
		response = iCategoryService.getCategoryByParentId(categoryId);

		System.out.println(response);
		return response;
	}

	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(int categoryId,String categoryName){
		ServerResponse response = null;
		response = iCategoryService.setCategoryName(categoryId,categoryName);
		System.out.println(response);
		return response;
	}

	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse<List<Integer>> getDeepCategory(int categoryId){
		ServerResponse response = null;
		response = iCategoryService.selectCategoryAndChildrenById(categoryId);
		System.out.println(response);
		return response;
	}



}
