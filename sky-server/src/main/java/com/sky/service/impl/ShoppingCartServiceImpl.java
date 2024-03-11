package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.*;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 补全剩下信息以及"冗余字段"，存入为单个金额故无需叠加【总金额放在前端自动进行】
        // 先判断已有对number进行修改
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 一个用户对应一个购物车用userId查询
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 有可能多个吗？同一用户，同一菜品，同一口味还能有多个？[此处返回多个，是为了给其后的查询让路
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList != null && shoppingCartList.size() == 1) {
            // 那此处list就需要获取第一个[进行覆盖
            shoppingCart = shoppingCartList.get(0);
            Integer numberOld = shoppingCart.getNumber();
            shoppingCart.setNumber(numberOld + 1);
            // 进行修改购物车
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {
            // 对菜品还是套餐进行判断来获取 name、amount、image
            if (shoppingCart.getDishId() == null) {
                // 说明是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            } else {
                // 所谓口味表是自带的
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            }
            // 设置number为1
            shoppingCart.setNumber(1);

            // 最后通用的createTime
            LocalDateTime createTime = LocalDateTime.now();
            shoppingCart.setCreateTime(createTime);

            // 新增插入
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     *
     * @param
     * @author: zjy
     * @return: List<ShoppingCart>
     **/
    public List<ShoppingCart> showShoppingCart() {
        // 直接通过 userId 进行查询即可
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     *
     * @param
     * @author: zjy
     * @return: void
     **/
    public void cleanShoppingCart() {
        // 直接获取userId 对其进行清理即可
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中一个商品
     *
     * @param shoppingCartDTO
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 先进行查询
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 防止数据库出错
        if (list != null && list.size() > 0) {
            shoppingCart = list.get(0);
            Integer number = shoppingCart.getNumber();
            // 后判断number大于一则进行修改-1，否则进行删除
            if (number > 1) {
                shoppingCart.setNumber(number - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            } else {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }
        }
    }


}
