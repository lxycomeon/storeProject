package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/22
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

	@Autowired
	private ShippingMapper shippingMapper;

	@Override
	public ServerResponse addShipping(Shipping shipping,Integer userId) {
		if (shipping == null){
			return ServerResponse.createByErrorMessage("请传入正确的参数");
		}
		shipping.setUserId(userId);
		int rowCount = shippingMapper.insert(shipping);
		if (rowCount >0 ){
			Map result = Maps.newHashMap();
			result.put("shippingId",shipping.getId());
			return ServerResponse.createBySuccess("新建地址成功",result);
		}
		return ServerResponse.createByErrorMessage("新建地址失败");
	}

	@Override
	public ServerResponse deleteSippingByIdAndUserId(Integer shippingId,Integer userId) {
		if (shippingId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		int rowCount = shippingMapper.deleteByPrimaryKeyAndUserId(shippingId,userId);//防止删除时候的横向越权问题，所以删除是时候加上只能删除本人的id
		if (rowCount > 0){
			return ServerResponse.createByErrorMessage("删除地址成功");
		}
		return ServerResponse.createByErrorMessage("删除地址失败");
	}

	@Override
	public ServerResponse updateShippingByUserId(Shipping shipping, Integer userId) {
		if (shipping == null|| userId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		shipping.setUserId(userId);//防止横向越权
		int rowCount = shippingMapper.updateByPrimaryKeyAndUserId(shipping);
		if (rowCount > 0){
			return ServerResponse.createBySuccessMessage("更新地址成功");
		}
		return ServerResponse.createByErrorMessage("更新地址失败");
	}

	@Override
	public ServerResponse<Shipping> selectByIdAndUserId(Integer shippingId, Integer userId) {
		if (shippingId == null|| userId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Shipping shipping = shippingMapper.selectByPrimaryKeyAndUserId(shippingId,userId);

		if (shipping == null){
			return ServerResponse.createByErrorMessage("查询失败");
		}
		return ServerResponse.createBySuccess(shipping);
	}

	@Override
	public ServerResponse<PageInfo> listByUserId(int pageNum, int pageSize, Integer userId) {
		PageHelper.startPage(pageNum,pageSize);
		if (userId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);

		PageInfo pageResult = new PageInfo(shippingList);
		return ServerResponse.createBySuccess(pageResult);
	}



}
