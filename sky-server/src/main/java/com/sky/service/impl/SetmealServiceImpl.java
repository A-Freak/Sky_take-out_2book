package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        // DTO中包含两个表的内容[分别进行存储
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insert(setmeal);

        // 以上进行主键返回，进行获取主键
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 此处不应添加判断，因为其为必要项
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        // 进行套餐关联的菜品添加[中间表
        setmealDishMapper.insertBatch(setmealDishes);

    }


}
