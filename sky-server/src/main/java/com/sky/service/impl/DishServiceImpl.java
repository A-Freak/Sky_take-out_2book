package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private SetmealMapper setmealMapper;

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
        // 此处我将其修改为批量删除[自我修改]笨比！！【对前端而言倒数无所谓】
        // 【一般进行删除都可以，但是其他业务层中进行调用不会删除多个】
        // 修改回单一删除【不早说都可以进行保留两个
        //删除菜品表中的菜品数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);

/*        for (Long id : ids) {
            dishMapper.deleteById(id);//后绪步骤实现
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);//后绪步骤实现
        }*/
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @author: zjy
     * @return: DishVO
     **/
    @Transactional
    public DishVO getByIdWithFlavor(Long id) {
        // 删除时编写的所谓的泛用SQL语句
        Dish dish = dishMapper.getById(id);

        // 新增对口味表的id查询【返回Entry
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = DishVO.builder()
                .flavors(dishFlavors)
                .build();
        BeanUtils.copyProperties(dish, dishVO);
        return dishVO;
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 类似于save
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表进行修改自带id
        dishMapper.update(dish);


        // 先根据dish_id对其进行删除口味表内容再进行添加【进行重复利用
        //获取主键值[自带不需要进行主键返回]
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新插入口味数据【新的【选的】没有主键id！！！
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 加个判断确认是否添加
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 启用禁用菜品
     *
     * @param status
     * @param id
     * @author: zjy
     * @return: void
     **/
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();

        dishMapper.update(dish);

        // 如果菜品与套餐有关联，套餐中的一个菜品进行了停售，那么这个套餐也要进行停售
        // 两个SQL，要开启事务
        if (status == StatusConstant.DISABLE) {
            // 此处同样是批量查询【需要进行笨笨的类型转换|也可新增一个单个查询
            Long setmealId = setmealDishMapper.getSetmealIdsByDishId(id);
            if (setmealId != null && setmealId > 0) {
                //当前菜品被套餐关联了，需要禁用套餐【修改
                Setmeal setmeal = Setmeal.builder()
                        .id(setmealId)
                        .status(status)
                        .build();
                setmealMapper.update(setmeal);
            }
        }
    }

    /**
     * 根据分类id查询菜品
     * @author: zjy
     * @param categoryId
     * @return: List<Dish>
     **/
    public List<Dish> list(Long categoryId) {
        List<Dish> dishs = dishMapper.list(categoryId);
        return dishs;
    }

    /**
     * 条件查询菜品和口味[导入]
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        // 或许可以,将菜品表以及菜品套餐表通过分类id进行连接，通过分类 id 进行查询

        // 通通过分类 id 获取菜品
        List<Dish> dishList = dishMapper.list(dish.getCategoryId());

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}