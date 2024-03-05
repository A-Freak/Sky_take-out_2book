package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表插入1条数据
        // 并且进行插入返回主键id操作
        dishMapper.insert(dish);//后绪步骤实现

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);//后绪步骤实现
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @author: zjy
     * @return: PageResult
     **/
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 固定格式
        // 1.设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        // 2.执行分页查询【3.使用其固定类型】
        //【此处返回值包含两个表故要进行表的连接】
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void deleteBatch(Long[] ids) {
        // 多表查询要结合实际情况【在售菜品不能进行删除
        // 自我编写【遗弃、应该想到其泛用性
        /*for (Long id : ids) {
           Integer i = dishMapper.getById(id);
            if(i != null && i > 0){
                // 抛出已知异常给前端
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }*/
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);//后绪步骤实现
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //  还未进行编辑，故无法正常测试
        // 多表查询关乎到表关系【此处是中间类 setmeal_dish 进行查询，有则不能进行删除
        // 类似DishFlavor新建一个Mapper
        /*for (Long id : ids) {
            Integer i = setmealDishMapper.getSetmealIdsByDishIds(id);
            if(i != null && i > 0){
                // 抛出已知异常给前端
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }*/
        //判断当前菜品是否能够删除---是否被套餐关联了？？
        // 查询的批量对其动态SQL的foreach进行了补充
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        // 进行删除既要对菜单表dish进行【动态SQL
        // 同样要对dish_flavor进行删除
        // 此处我将其修改为批量删除[自我修改]
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByIds(ids);

    }

}