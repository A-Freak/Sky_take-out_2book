package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
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

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @author: zjy
     * @return: PageResult
     **/
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 固定格式
        // 1.设置分页参数
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 2.执行分页查询【3.使用其固定类型】
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void deleteBatch(Long[] ids) {
        // 并且在售是不能进行删除的
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                //当前套餐处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // 同删除菜品一样，需要删除其套餐菜品的连接表
        // 上对下,并非同菜品一样，有关联就无法删除
        setmealDishMapper.deleteBySetmealIds(ids);
        setmealMapper.deleteByIds(ids);
    }

    /**
     * 修改套餐
     * @author: zjy
     * @param setmealDTO
     * @return: void
     **/
    public void updateWithDish(SetmealDTO setmealDTO) {
        // 既要修改套餐，也要通过套餐id修改中间表中对应的关系
        // TODO 今天就到这里
    }


}
