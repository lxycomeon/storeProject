package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.Set;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/15
 * Time: 22:35
 */
public interface ICategoryService {

	ServerResponse<Integer> addCategory(String categoryName, Integer parentId) ;

	ServerResponse<Integer> checkParentId(int parentId);

	ServerResponse getCategoryByParentId(int categoryId);

	ServerResponse setCategoryName(int categoryId, String categoryName);

	Set<Category> findChildCategory(Integer categoryId, Set<Category> categorySet);

	ServerResponse selectCategoryAndChildrenById(Integer categoryId);


}
