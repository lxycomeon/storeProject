package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByPrimaryKeyAndUserId(@Param(value = "shippingId") Integer shippingId, @Param(value = "userId")Integer userId);

	int updateByPrimaryKeyAndUserId( Shipping shipping);

	Shipping selectByPrimaryKeyAndUserId(@Param(value = "shippingId")Integer shippingId, @Param(value = "userId")Integer userId);

	List<Shipping> selectByUserId(Integer userId);

}