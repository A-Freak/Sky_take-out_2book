package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @author: zjy
     * @return: PageResult
     **/
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     *
     * @param ids
     * @author: zjy
     * @return: void
     **/
    void deleteBatch(Long[] ids);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @author: zjy
     * @return: DishVO
     **/
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @author: zjy
     * @return: void
     **/
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 启用禁用菜品
     *
     * @param status
     * @param id
     * @author: zjy
     * @return: void
     **/
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @author: zjy
     * @return: List<Dish>
     **/
    List<Dish> list(Long categoryId);

    /**
     * 根据菜品【包含状态以及分类 id】查询菜品【包含菜品口味表】
     * @param dish
     * @author: zjy
     * @return: List<DishVO>
     **/
    List<DishVO> listWithFlavor(Dish dish);
}