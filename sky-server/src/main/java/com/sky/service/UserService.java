package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 用户微信登录
     * @author: zjy
     * @param userLoginDTO
     * @return: UserLoginVO
     **/
    User wxlogin(UserLoginDTO userLoginDTO);
}
