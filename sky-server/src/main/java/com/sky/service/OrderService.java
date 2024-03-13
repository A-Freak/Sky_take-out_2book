package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @author: zjy
     * @param ordersSubmitDTO
     * @return: OrderSubmitVO
     **/
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询历史订单
     * @author: zjy
     * @param page
     * @param pageSize
     * @param status
     * @return: PageResult
     **/
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @author: zjy
     * @param id
     * @return: OrderVO
     **/
    OrderVO details(Long id);

    /**
     * 取消订单
     * @author: zjy
     * @param id
     * @return: void
     **/
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     * @author: zjy
     * @param id
     * @return: void
     **/
    void repetition(Long id);
}
