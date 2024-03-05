package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     *
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @author: zjy
     * @param dishPageQueryDTO
     * @return: Page<DishVO>
     **/
    // 包含两个表故要进行表的连接：选用左外连接【包含前一个表的所有内容以及其交集部分】
    // 其中菜品口味为空【但是前端未展示故无所谓】
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
}
