package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据 openid 查询用户
     * @author: zjy
     * @param openid
     * @return: User
     **/
    @Select("select * from user where openid = #{openid}")
    User getbyOpenId(String openid);


    /**
     * 插入新用户[获取主键 id 返回]
     * @author: zjy
     * @param user
     * @return: void
     **/
    void insert(User user);
}
