package com.sky.service;

import com.sky.dto.SetmealDTO;
import org.springframework.stereotype.Service;


public interface SetmealService {

    /**
     * 新增套餐
     * @author: zjy
     * @param setmealDTO
     * @return: void
     **/
    void saveWithDish(SetmealDTO setmealDTO);
}
