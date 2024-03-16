package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

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


    /**
     * 根据 Id 查询用户
     * @author: zjy
     * @param id
     * @return: User
     **/
    @Select("select * from user where id = #{id}")
    User getById(Long id);


    /**
     * 用户统计接口[user表创建时间
     * @author: zjy
     * @param beginTime
     * @param endOfDay
     * @return: Integer
     **/
    Integer getUserCount(LocalDateTime beginTime, LocalDateTime endOfDay);
}
