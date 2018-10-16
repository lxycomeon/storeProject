package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * Date: 2018/10/15
 * Time: 22:35
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	@Autowired
	private CategoryMapper categoryMapper;

	private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Override
	public ServerResponse addCategory(String categoryName, Integer parentId) {
		if (parentId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.createByErrorMessage("添加品类，参数错误");
		}
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);
		Integer resultCount = categoryMapper.insertSelective(category);
		if (resultCount > 0){
			return ServerResponse.createBySuccessMessage("添加品类成功");
		}else{
			return ServerResponse.createByErrorMessage("添加品类失败");
		}
	}

	@Override
	public ServerResponse<Integer> checkParentId(int parentId) {
		Integer resultCount = categoryMapper.checkParentId(parentId);
		if (resultCount > 0){
			return ServerResponse.createBySuccess();
		}else{
			return ServerResponse.createByError();
		}
	}

	@Override
	public ServerResponse getCategoryByParentId(int categoryId) {
		List<Category> list = categoryMapper.queryByParentId(categoryId);
		if (CollectionUtils.isEmpty(list)){
			logger.info("未找到当前分类的子分类信息！");
			//未找到分类也能给前端报错，这里用日志解决
//			return ServerResponse.createByErrorMessage("未找到该品类");
		}
		return ServerResponse.createBySuccess(list);
	}

	@Override
	public ServerResponse setCategoryName(int categoryId, String categoryName) {
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if (category == null){
			return ServerResponse.createByErrorMessage("并没有此品类，请重新核对");
		}else {
			category.setName(categoryName);
			Integer resultCount = categoryMapper.updateByPrimaryKeySelective(category);
			if (resultCount > 0){
				return ServerResponse.createBySuccessMessage("更新品类成功");
			}
		}
		return ServerResponse.createByErrorMessage("更新品类名字失败");
	}

	//查询孩子节点的id
	@Override
	public Set<Category> findChildCategory(Integer categoryId, Set<Category> categorySet) {
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if (category != null){
			categorySet.add(category);
		}
		List<Category> categoryList = categoryMapper.queryByParentId(categoryId);
		for ( Category categoryItem:categoryList) {
			findChildCategory(categoryItem.getId(),categorySet);
		}
		//这里用set集合，直接去重。但是对于自己的类，要自己重写equal和hashcode方法
		return categorySet;
	}

	//递归查询本节点的id以及子节点的所有id
	@Override
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildCategory(categoryId,categorySet);

		List<Integer> categoryIdList = Lists.newArrayList();
		if (categoryId != null){
			for (Category categoryItem:categorySet) {
				categoryIdList.add(categoryItem.getId());
			}
		}
		return ServerResponse.createBySuccess(categoryIdList);
	}

}
