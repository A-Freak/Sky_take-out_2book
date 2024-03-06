package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据 id 修改套餐
     * @author: zjy
     * @param setmeal
     * @return: void
     **/
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 新增套餐[并进行主键返回
     * @author: zjy
     * @param setmeal
     * @return: void
     **/
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);
}
