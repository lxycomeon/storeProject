package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username); //因为mybatis中的插件在其mapper的xml中没有这个方法对应，所以报错

    //mybatis中，如果输入的参数中有多个参数，则需要添加@Param注解，在sql中对应param中的值调用传入的值
    User selectLogin(@Param("username") String username, @Param("password") String password);

    Integer findMaxId();

    int checkEmail(String str);

    String getForgetQuestionByUsername(String username);

    Integer checkAnswerByQuestion(@Param("username")String username, @Param("question") String question, @Param("answer") String answer);

    Integer selectIdByUsername(String username);

    String getPasswordById(Integer id);

    int checkEmailByUserId(@Param("email")String email, @Param("id")Integer id);
}