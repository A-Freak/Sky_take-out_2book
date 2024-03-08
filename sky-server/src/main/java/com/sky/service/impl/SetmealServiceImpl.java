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
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
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
    @Autowired
    private DishMapper dishMapper;


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
        // 此处不添加判断，因为其为必要项
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
        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    /**
     * 根据id查询套餐【回显
     *
     * @param id
     * @author: zjy
     * @return: SetmealVO
     **/
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);

        List<SetmealDish> list = setmealDishMapper.getBySetmealId(id);

        // 进行合并
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @author: zjy
     * @return: void
     **/
    public void updateWithSD(SetmealDTO setmealDTO) {
        // 既要修改套餐，也要通过套餐id修改中间表中对应的关系
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        // 先删除
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 后插入【同新增一样进行设置套餐 id
        // 新增判断[有可能将原本的菜品进行删除
        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐的启售停售
     *
     * @param status
     * @param id
     * @author: zjy
     * @return: void
     **/
    public void startOrStop(Integer status, Long id) {
        // 禁用时，无需考虑直接停用。
        // 启用时需要先通过中间表查询，再通过菜品表查询，最后进行判断内部菜品均为启用才可启用
        if (status == StatusConstant.ENABLE) {
            List<Long> dishIds = setmealDishMapper.getDishIdBySetmealId(id);
            // 再通过菜品id 获取菜品表中数据，并对其进行判断状态
            // [套餐下菜品不能为空，故百分之百还有对应关系]
            dishIds.forEach(dishId -> {
                Dish dish = dishMapper.getById(dishId);
                if (dish.getStatus() == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        Setmeal build = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        // 均通过测试后进行修改
        setmealMapper.update(build);
    }


}
