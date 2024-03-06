package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 判断菜品id是否关联套餐【同样是扩展性的问题-废弃
     *
     * @param id
     * @author: zjy
     * @return: Integer
     **/
/*

    @Select("select count(*) from setmeal_dish where dish_id = #{id}")
    Integer getSetmealIdsByDishIds(Long id);
*/


    /**
     * 根据菜品id查询对应的套餐id【批量
     *
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    // 查询的批量对其动态SQL的foreach进行了补充
    List<Long> getSetmealIdsByDishIds(Long[] dishIds);

    /**
     * 菜品id查询对应的套餐id
     * @author: zjy
     * @param dishId
     * @return: Long
     **/
    @Select("select setmeal_id from setmeal_dish where dish_id = #{dishId}")
    Long getSetmealIdsByDishId(Long dishId);


    /**
     * 批量插入数据
     * @author: zjy
     * @param setmealDishes
     * @return: void
     **/
    void insertBatch(List<SetmealDish> setmealDishes);
}
