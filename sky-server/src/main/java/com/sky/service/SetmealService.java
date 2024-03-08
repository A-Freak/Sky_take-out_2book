package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;


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
    void updateWithSetmealDish(SetmealDTO setmealDTO);
}
