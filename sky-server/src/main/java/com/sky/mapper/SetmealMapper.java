package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据 id 修改套餐
     *
     * @param setmeal
     * @author: zjy
     * @return: void
     **/
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 新增套餐[并进行主键返回
     *
     * @param setmeal
     * @author: zjy
     * @return: void
     **/
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @author: zjy
     * @return: Page<SetmealVO>
     **/
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据 id 查询对应套餐
     *
     * @param id
     * @author: zjy
     * @return: Setmeal
     **/
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据 id 进行套餐的批量删除
     *
     * @param ids
     * @author: zjy
     * @return: void
     **/
    void deleteByIds(Long[] ids);

    /**
     * 条件查询[导入]
     * 根据分类 id 查询在售的套餐
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);


    /**
     * 根据id查询菜品选项[导入]
     * 没有菜品口味表[但是涉及到了中间表的东西只能进行连接表] * @author: zjy
     *
     * @param setmealId
     * @return: List<DishItemVO>
     **/
    @Select("select sd.name,sd.copies,d.image,d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id =  d.id " +
            "where sd.setmeal_id = #{setmealId} ")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
