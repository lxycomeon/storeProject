package com.mmall.dao;

import com.mmall.pojo.MiaoshaOrder;

public interface MiaoshaOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MiaoshaOrder record);

    int insertSelective(MiaoshaOrder record);

    MiaoshaOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MiaoshaOrder record);

    int updateByPrimaryKey(MiaoshaOrder record);
}