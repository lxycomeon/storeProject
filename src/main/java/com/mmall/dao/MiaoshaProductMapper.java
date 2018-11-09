package com.mmall.dao;

import com.mmall.pojo.MiaoshaProduct;

import java.util.List;

public interface MiaoshaProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MiaoshaProduct record);

    int insertSelective(MiaoshaProduct record);

    MiaoshaProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MiaoshaProduct record);

    int updateByPrimaryKey(MiaoshaProduct record);

    List<MiaoshaProduct> selectAllProduct();

}