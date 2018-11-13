package com.mmall.pojo;

import java.util.Date;

public class MiaoshaOrder {
    private Integer id;

    private Integer miaoshaProductId;

    private Long orderId;

    private Integer userId;

    private Date createTime;

    private Date updateTime;

    public MiaoshaOrder(Integer id, Integer miaoshaProductId, Long orderId, Integer userId, Date createTime, Date updateTime) {
        this.id = id;
        this.miaoshaProductId = miaoshaProductId;
        this.orderId = orderId;
        this.userId = userId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public MiaoshaOrder() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMiaoshaProductId() {
        return miaoshaProductId;
    }

    public void setMiaoshaProductId(Integer miaoshaProductId) {
        this.miaoshaProductId = miaoshaProductId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}