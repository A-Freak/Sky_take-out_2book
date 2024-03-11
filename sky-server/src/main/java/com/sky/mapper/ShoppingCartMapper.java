package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据用户id以及菜品套餐id查询购物车
     *
     * @param shoppingCart
     * @author: zjy
     * @return: ShoppingCart
     **/
    // 菜品口味表还是要的不然有可能菜品相同但是口味不同
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 直接根据id修改个数
     *
     * @param shoppingCartAll
     * @author: zjy
     * @return: void
     **/
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCartAll);

    /**
     * 插入购物车数据
     * @author: zjy
     * @param shoppingCart
     * @return: void
     **/
    // 为空也能插入[没有所谓的判断为空
    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);


    /**
     * 清空购物车
     * @author: zjy
     * @param userId
     * @return: void
     **/
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 删除购物车中一个商品
     * @author: zjy
     * @param shoppingCart
     * @return: void
     **/
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
