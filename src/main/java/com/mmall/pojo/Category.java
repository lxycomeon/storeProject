package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

//使用lombok注解进行自动生成get，set方法，toString，equalsAndHashcode，无参构造，全参构造。
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id","name"})   //声明equalsAndHashcode方法时候只使用id和name两个属性值
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;


}