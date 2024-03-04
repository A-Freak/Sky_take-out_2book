package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.*;

/**
 * 自定义注解用于填充公共字段
 * @author: zjy
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
// 只在运行中存在
@Target(ElementType.METHOD)
// 可以写在方法上
public @interface AutoFill {
    //数据库操作类型：UPDATE INSERT
    // 写法借鉴
    OperationType value();
}
