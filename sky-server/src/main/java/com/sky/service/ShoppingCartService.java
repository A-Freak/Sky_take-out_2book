package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @author: zjy
     * @param shoppingCartDTO
     * @return: void
     **/
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @author: zjy
     * @param
     * @return: List<ShoppingCart>
     **/
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     * @author: zjy
     * @param
     * @return: void
     **/
    void cleanShoppingCart();

    /**
     * 删除购物车中一个商品
     * @author: zjy
     * @param shoppingCartDTO
     * @return: void
     **/
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
