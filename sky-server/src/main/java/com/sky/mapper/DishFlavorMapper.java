package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     *
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 批量删除口味数据
     *
     * @param
     * @param ids
     * @author: zjy
     * @return: void
     **/
    void deleteByIds(Long[] ids);

    /**
     * 通过菜品id查询菜品口味表
     *
     * @param id
     * @author: zjy
     * @return: List<DishFlavor>
     **/
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}