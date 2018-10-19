package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
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
	public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			if (iCategoryService.checkParentId(parentId).isSuccess()){
				response = iCategoryService.addCategory(categoryName,parentId);
			}
			else{
				response = ServerResponse.createByErrorMessage("没有该父类目录，请重新输入");
			}
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}

		System.out.println(response);
		return response;
	}

	@RequestMapping("get_category.do")
	@ResponseBody
	public ServerResponse getCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")int categoryId){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iCategoryService.getCategoryByParentId(categoryId);
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(HttpSession session,int categoryId,String categoryName){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iCategoryService.setCategoryName(categoryId,categoryName);

		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse<List<Integer>> getDeepCategory(HttpSession session,int categoryId){
		ServerResponse response = null;
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			response = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			response = iCategoryService.selectCategoryAndChildrenById(categoryId);
		} else {
			response = ServerResponse.createByErrorMessage("权限不够");
		}
		System.out.println(response);
		return response;
	}



}
