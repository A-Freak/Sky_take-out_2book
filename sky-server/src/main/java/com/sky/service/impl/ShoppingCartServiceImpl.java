package com.sky.service.impl;


import com.sky.dto.ShoppingCartDTO;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @author: zjy
     * @return: void
     **/
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 补全剩下信息以及"冗余字段"
    }
}
