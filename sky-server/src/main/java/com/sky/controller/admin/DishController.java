package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品管理
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
     * @author: zjy
     * @param dishDTO
     * @return: Result
     **/
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);//后绪步骤开发
        return Result.success();
    }

/**
 * 菜品分页查询
 * @author: zjy
 * @param dishPageQueryDTO
 * @return: Result<PageResult>
 **/
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询，参数为：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

/**
 * 批量删除菜品
 * @author: zjy
 * @param ids
 * @return: Result
 **/
    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    // 两种写法请求可为数组也可为集合[要进行映射]
    public Result delete(Long[] ids){
        log.info("菜品批量删除，参数为：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }





}
