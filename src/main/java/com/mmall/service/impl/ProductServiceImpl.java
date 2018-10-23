package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/18
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public ServerResponse saveOrUpdateProduct(Product product) {

		if (product != null){
			if (!StringUtils.isBlank(product.getMainImage())){
				if (StringUtils.isBlank(product.getMainImage())){
					String[] subImageArray = product.getSubImages().split(",");
					if (subImageArray.length >0){
						product.setMainImage(subImageArray[0]);
					}
				}
			}
			if (product.getId() != null){
				int rowCount = productMapper.updateByPrimaryKeySelective(product);
				if (rowCount > 0){
					return ServerResponse.createBySuccessMessage("更新产品成功");
				}else {
					return ServerResponse.createByErrorMessage("更新产品失败");
				}
			}else {
				int rowCount = productMapper.insert(product);
				if (rowCount > 0){
					return ServerResponse.createBySuccessMessage("新增产品成功");
				}else {
					return ServerResponse.createByErrorMessage("新增产品失败");
				}
			}

		}else{
			return ServerResponse.createByErrorMessage("新增或者更新产品参数不正确");
		}
	}

	@Override
	public ServerResponse updateSaleStatus(Integer productId, Integer status) {
		if (productId == null || status == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product != null){
			product.setStatus(status);
			int rowCount = productMapper.updateByPrimaryKeySelective(product);
			if (rowCount > 0){
				return ServerResponse.createBySuccessMessage("修改产品状态成功");
			}else {
				return ServerResponse.createByErrorMessage("修改产品状态失败");
			}
		}else {
			return ServerResponse.createByErrorMessage("不存在此商品");
		}
	}

	@Override
	public ServerResponse<ProductDetailVo> getProductById(Integer productId) {
		if (productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product != null){
			return ServerResponse.createBySuccess(assembleProductDetailVo(product));
		}else {
			return ServerResponse.createByErrorMessage("产品已经下架或删除");
		}
	}

	@Override
	public ServerResponse<ProductDetailVo> getProductDetailById(Integer productId) {
		if (productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null){
			return ServerResponse.createByErrorMessage("产品已经下架或删除");
		}
		if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
			return ServerResponse.createByErrorMessage("产品已经下架或删除");
		}
		return ServerResponse.createBySuccess(assembleProductDetailVo(product));
	}


	private ProductDetailVo assembleProductDetailVo(Product product){
		ProductDetailVo productDetailVo = new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStock(product.getStock());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setSubImages(product.getSubImages());

		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if (category == null){
			productDetailVo.setParentCategoryId(0);	//默认为根节点
		}else {
			productDetailVo.setParentCategoryId(category.getParentId());
		}
		productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		return productDetailVo;

	}

	private ProductListVo assembleProductListVo(Product product){
		ProductListVo productListVo = new ProductListVo();
		productListVo.setId(product.getId());
		productListVo.setName(product.getName());
		productListVo.setCategoryId(product.getCategoryId());
		productListVo.setSubtitle(product.getSubtitle());
		productListVo.setPrice(product.getPrice());
		productListVo.setMainImage(product.getMainImage());
		productListVo.setStatus(product.getStatus());

		productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
		return productListVo;
	}


	@Override
	public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
		//startPage--start
		//填充自己的sql查询逻辑
		//PageHelper--收尾
		PageHelper.startPage(pageNum,pageSize);
		List<Product> productList = productMapper.selectList();
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product productItem:productList) {
			ProductListVo productListVo = assembleProductListVo(productItem);
			productListVoList.add(productListVo);
		}
		PageInfo pageResult = new PageInfo(productListVoList);	//使用原返回的list计算分页的一些信息
		//pageResult.setList(productListVoList);				//将转为视图的Vo返回,没区别
		return ServerResponse.createBySuccess(pageResult);
	}

	@Override
	public ServerResponse<PageInfo> searchProduct(int pageNum, int pageSize, String productName, String productId) {
		PageHelper.startPage(pageNum,pageSize);
		if(StringUtils.isNotBlank(productName)){
			productName = new StringBuilder().append("%").append(productName).append("%").toString();
		}else {
			productName = null;
		}
		if (StringUtils.isBlank(productId)){
			productId = null;
		}
		List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product productItem:productList) {
			productListVoList.add(assembleProductListVo(productItem));
		}
		PageInfo pageResult = new PageInfo(productListVoList);

		return ServerResponse.createBySuccess(pageResult);
	}

	@Override
	public ServerResponse listProduct(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize) {


		PageHelper.startPage(pageNum,pageSize);
		//PageHelper.orderBy();   使用它也可以传入排序的方法，一个是price desc，另一个是price asc
		if(StringUtils.isNotBlank(keyword)){
			keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
		}else {
			keyword = null;
		}
		String sortOrder = null;
		String sortField = null;
		if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
			//判断排序字段名的有效性和排序的有效性
			//分割orderBy字段，排序.或者使用pagehelper的排序功能
			sortOrder =orderBy.substring(orderBy.lastIndexOf("_")+1);
			sortField = orderBy.substring(0,orderBy.lastIndexOf("_"));
		}else {
			return ServerResponse.createByErrorMessage("排序字段名无效，参数错误");
		}
		List<Product> productList = productMapper.selectByKeywordAndCategoryId(categoryId,keyword,sortField,sortOrder);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product productItem:productList) {
			productListVoList.add(assembleProductListVo(productItem));
		}
		PageInfo pageResult = new PageInfo(productListVoList);

		return ServerResponse.createBySuccess(pageResult);
	}
}
