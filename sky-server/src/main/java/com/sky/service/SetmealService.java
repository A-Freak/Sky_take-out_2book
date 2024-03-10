package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService {

    /**
     * 新增套餐
     * @author: zjy
     * @param setmealDTO
     * @return: void
     **/
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @author: zjy
     * @param setmealPageQueryDTO
     * @return: PageResult
     **/
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @author: zjy
     * @param ids
     * @return: void
     **/
    void deleteBatch(Long[] ids);

    /**
     * 根据id查询套餐【回显
     * @author: zjy
     * @param id
     * @return: SetmealVO
     **/
    SetmealVO getById(Long id);

    /**
     * 修改套餐
     * @author: zjy
     * @param setmealDTO
     * @return: void
     **/
    void updateWithSD(SetmealDTO setmealDTO);

    /**
     * 套餐的启售停售
     * @author: zjy
     * @param status
     * @param id
     * @return: void
     **/
    void startOrStop(Integer status, Long id);


    /**
     * 条件查询[导入]
     * 根据分类 id 查询在售的套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);


    /**
     * 根据id查询菜品选项[导入]
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
