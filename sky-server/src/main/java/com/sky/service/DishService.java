package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;


public interface DishService {

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @author: zjy
     * @param dishPageQueryDTO
     * @return: PageResult
     **/
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

/**
 * 批量删除菜品
 * @author: zjy
 * @param ids
 * @return: void
 **/
    void deleteBatch(Long[] ids);
}