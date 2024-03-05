package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
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
     *
     * @param dishPageQueryDTO
     * @author: zjy
     * @return: Page<DishVO>
     **/
    // 包含两个表故要进行表的连接：选用左外连接【包含前一个表的所有内容以及其交集部分】
    // 其中菜品口味为空【但是前端未展示故无所谓】
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 判断菜品id是否起售[自我编写-废弃]
     * @author: zjy
     * @param id
     * @return: Integer
     **/
/*
    @Select("select count(*) from dish where id = #{id} and status = 1")
    Integer getById(Long id);
*/
    /**
     * 根据主键查询菜品【优点：因其具有更加好的泛用性】
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 删除菜品
     *
     * @param id
     * @author: zjy
     * @param、
     * @return: void
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 通过id修改菜品对象
     * @author: zjy
     * @param dish
     * @return: void
     **/
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
}
