package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
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

    /**
     * 套餐分页查询
     * @author: zjy
     * @param setmealPageQueryDTO
     * @return: Page<SetmealVO>
     **/
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据 id 查询对应套餐
     * @author: zjy
     * @param id
     * @return: Setmeal
     **/
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据 id 进行套餐的批量删除
     * @author: zjy
     * @param ids
     * @return: void
     **/
    void deleteByIds(Long[] ids);
}
