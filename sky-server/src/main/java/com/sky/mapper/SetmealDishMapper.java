package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
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
     *
     * @param dishId
     * @author: zjy
     * @return: Long
     **/
    @Select("select setmeal_id from setmeal_dish where dish_id = #{dishId}")
    List<Long> getSetmealIdsByDishId(Long dishId);

    /**
     * 批量插入数据
     *
     * @param setmealDishes
     * @author: zjy
     * @return: void
     **/
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 通过套餐id 对中间表进行批量删除
     *
     * @param setmealIds
     * @author: zjy
     * @return: void
     **/
    void deleteBySetmealIds(Long[] setmealIds);

    /**
     * 适配与修改套餐的id删除
     * @author: zjy
     * @param setmealIds
     * @return: void
     **/
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealIds}")
    void deleteBySetmealId(Long setmealIds);

    /**
     * 通过套餐id 查询套餐菜品表
     *
     * @param setmealId
     * @author: zjy
     * @return: List<SetmealDish>
     **/
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);


    /**
     * 根据【单个】套餐id查询对应的菜品id【可能多个】[套餐禁用中间表查询
     * @author: zjy
     * @param setmealId
     * @return: List<Long>
     **/
    //select dish_id from setmeal_dish where setmeal_id = ?
    // 查询的批量对其动态SQL的foreach进行了补充
    @Select("select  dish_id from setmeal_dish where  setmeal_id = #{setmealId}")
    List<Long> getDishIdBySetmealId(Long setmealId);
}
