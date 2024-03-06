package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 *
 * @CreateTime: 2024-03-04
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @author: zjy
     * @return: Result
     **/
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);//后绪步骤开发
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @author: zjy
     * @return: Result<PageResult>
     **/
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询，参数为：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @author: zjy
     * @return: Result
     **/
    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    // 两种写法请求可为数组也可为集合[要进行映射]
    public Result delete(Long[] ids) {
        log.info("菜品批量删除，参数为：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @author: zjy
     * @return: Result<DishVO>
     **/
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id：{} 进行菜品查询", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @author: zjy
     * @return: Result
     **/
    @PutMapping()
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品，参数为：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 启用禁用菜品
     * @author: zjy
     * @param status
     * @param id
     * @return: Result
     **/
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用菜品：{},{}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }


    /**
     * 根据分类id查询菜品
     * @author: zjy
     * @param categoryId
     * @return: Result<List < Dish>>
     **/
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id：{}", categoryId);
        List<Dish> list =  dishService.list(categoryId);
        return Result.success(list);
    }


}
