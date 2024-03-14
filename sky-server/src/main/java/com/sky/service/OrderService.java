package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @author: zjy
     * @return: OrderSubmitVO
     **/
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询历史订单
     *
     * @param page
     * @param pageSize
     * @param status
     * @author: zjy
     * @return: PageResult
     **/
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     *
     * @param id
     * @author: zjy
     * @return: OrderVO
     **/
    OrderVO details(Long id);

    /**
     * 取消订单[包含退款
     *
     * @param id
     * @author: zjy
     * @return: void
     **/
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     *
     * @param id
     * @author: zjy
     * @return: void
     **/
    void repetition(Long id);

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @author: zjy
     * @return: PageResult
     **/
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     *
     * @param
     * @author: zjy
     * @return: OrderStatisticsVO
     **/
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @author: zjy
     * @param ordersConfirmDTO
     * @return: void
     **/
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单[包含退款
     * @author: zjy
     * @param ordersRejectionDTO
     * @return: void
     **/
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 取消订单[包含退款
     * @author: zjy
     * @param ordersCancelDTO
     * @return: void
     **/
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 派送订单
     * @author: zjy
     * @param id
     * @return: void
     **/
    void delivery(Long id);


    /**
     * 完成订单
     * @author: zjy
     * @param id
     * @return: void
     **/
    void complete(Long id);
}
