package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.MiaoshaProductMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.MiaoshaProduct;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
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
	@Autowired
	private MiaoshaProductMapper miaoshaProductMapper;

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
		if (orderBy.equals("default"))
			orderBy = "price_desc";
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

	//////////////////秒杀商品

	@Override
	public ServerResponse saveOrUpdateMiaoshaProduct(MiaoshaProduct miaoshaProduct) {
		//确保添加的秒杀商品，是存在于商品库中的
		Product product = productMapper.selectByPrimaryKey(miaoshaProduct.getProductId());
		if (miaoshaProduct.getMiaoshaStock() > product.getStock()){
			return ServerResponse.createByErrorMessage("商品库存不足，最多添加"+product.getStock()+"件商品进行秒杀");
		}
		if (miaoshaProduct != null && product != null){
			if (miaoshaProduct.getId() != null){
				int rowCount = miaoshaProductMapper.updateByPrimaryKeySelective(miaoshaProduct);
				if (rowCount > 0){
					return ServerResponse.createBySuccessMessage("更新秒杀产品成功");
				}else {
					return ServerResponse.createByErrorMessage("更新秒杀产品失败");
				}
			}else {
				int rowCount = miaoshaProductMapper.insertSelective(miaoshaProduct);
				if (rowCount > 0){
					return ServerResponse.createBySuccessMessage("新增秒杀产品成功");
				}else {
					return ServerResponse.createByErrorMessage("新增秒杀产品失败");
				}
			}

		}else{
			return ServerResponse.createByErrorMessage("新增或者更新产品参数不正确");
		}
	}

	@Override
	public ServerResponse listMiaoshaProduct() {
		MiaoshaProductListVo result = new MiaoshaProductListVo();
		List<MiaoshaProductVo> miaoshaProductVos = Lists.newArrayList();
		String jsonStr = RedisPoolUtil.get(Const.RedisCacheName.REDIS_CACHE_MIAOSHA_PRODUCT_LIST);
		if ( jsonStr != null){
			miaoshaProductVos = JsonUtil.string2Obj(jsonStr,List.class,MiaoshaProductVo.class);
		}else {
			List<MiaoshaProduct> list = miaoshaProductMapper.selectAllProduct();
			for (MiaoshaProduct productItem:list) {
				miaoshaProductVos.add(assembleMiaoshaProductListVo(productItem));
			}
			RedisPoolUtil.setEx(Const.RedisCacheName.REDIS_CACHE_MIAOSHA_PRODUCT_LIST,JsonUtil.obj2String(miaoshaProductVos),120);
		}

		result.setMiaoshaProductVoList(miaoshaProductVos);
		return ServerResponse.createBySuccess(result);
	}

	private MiaoshaProductVo assembleMiaoshaProductListVo(MiaoshaProduct miaoshaProduct){
		MiaoshaProductVo miaoshaProductVo = new MiaoshaProductVo();
		Product product = productMapper.selectByPrimaryKey(miaoshaProduct.getProductId());

		miaoshaProductVo.setId(miaoshaProduct.getId());
		miaoshaProductVo.setProductId(miaoshaProduct.getProductId());
		miaoshaProductVo.setMiaoshaPrice(miaoshaProduct.getMiaoshaPrice());
		miaoshaProductVo.setProductPrice(product.getPrice());
		miaoshaProductVo.setProductMainImage(product.getMainImage());
		miaoshaProductVo.setMiaoshaStock(miaoshaProduct.getMiaoshaStock());
		miaoshaProductVo.setName(product.getName());
		miaoshaProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

		return miaoshaProductVo;
	}

	@Override
	public ServerResponse getMiaoshaProductDetailById(Integer miaoshaProductId) {
		MiaoshaProduct miaoshaProduct = miaoshaProductMapper.selectByPrimaryKey(miaoshaProductId);
		if (miaoshaProduct != null){
			MiaoshaProductDetailVo miaoshaProductDetailVo = assembleMiaoshaProductDetailVo(miaoshaProduct);
			return ServerResponse.createBySuccess(miaoshaProductDetailVo);
		}else
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
	}

	private MiaoshaProductDetailVo assembleMiaoshaProductDetailVo(MiaoshaProduct miaoshaProduct) {
		MiaoshaProductDetailVo miaoshaProductDetailVo = new MiaoshaProductDetailVo();
		Product product = productMapper.selectByPrimaryKey(miaoshaProduct.getProductId());

		miaoshaProductDetailVo.setId(miaoshaProduct.getId());
		miaoshaProductDetailVo.setSubtitle(product.getSubtitle());
		miaoshaProductDetailVo.setProductPrice(product.getPrice());
		miaoshaProductDetailVo.setMiaoshaPrice(miaoshaProduct.getMiaoshaPrice());
		miaoshaProductDetailVo.setCategoryId(product.getCategoryId());
		miaoshaProductDetailVo.setMainImage(product.getMainImage());
		miaoshaProductDetailVo.setDetail(product.getDetail());
		miaoshaProductDetailVo.setName(product.getName());
		miaoshaProductDetailVo.setMiaoshaStock(miaoshaProduct.getMiaoshaStock());
		miaoshaProductDetailVo.setSubImages(product.getSubImages());

		//还没有秒杀结束
		if (miaoshaProduct.getEndTime().getTime() >= System.currentTimeMillis()){//获得的为毫秒
			Integer remainSeconds = (int) (miaoshaProduct.getStartTime().getTime() - System.currentTimeMillis())/1000;
			if (remainSeconds > 0){
				miaoshaProductDetailVo.setMiaoshaStatus(Const.MiaoshaProductStatusEnum.NO_START.getCode());
				miaoshaProductDetailVo.setRemainSeconds(remainSeconds);
			}else {
				miaoshaProductDetailVo.setMiaoshaStatus(Const.MiaoshaProductStatusEnum.ON_SALE.getCode());
				miaoshaProductDetailVo.setRemainSeconds(0);
			}
		}else {
			miaoshaProductDetailVo.setMiaoshaStatus(Const.MiaoshaProductStatusEnum.END_SALE.getCode());
			miaoshaProductDetailVo.setRemainSeconds(-1);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		miaoshaProductDetailVo.setEndTime(sdf.format(miaoshaProduct.getEndTime()));
		miaoshaProductDetailVo.setStartTime(sdf.format( miaoshaProduct.getStartTime()));

		miaoshaProductDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if (category == null){
			miaoshaProductDetailVo.setParentCategoryId(0);	//默认为根节点
		}else {
			miaoshaProductDetailVo.setParentCategoryId(category.getParentId());
		}
		miaoshaProductDetailVo.setCreateTime(miaoshaProduct.getCreateTime());
		miaoshaProductDetailVo.setUpdateTime(miaoshaProduct.getUpdateTime());

		return miaoshaProductDetailVo;
	}

	@Override
	public List<MiaoshaProduct> selectAllProduct() {
		return miaoshaProductMapper.selectAllProduct();
	}
}
