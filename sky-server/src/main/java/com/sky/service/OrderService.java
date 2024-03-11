package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * 用户下单
     * @author: zjy
     * @param ordersSubmitDTO
     * @return: OrderSubmitVO
     **/
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
