package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @author: zjy
     * @param shoppingCartDTO
     * @return: void
     **/
    void add(ShoppingCartDTO shoppingCartDTO);
}
