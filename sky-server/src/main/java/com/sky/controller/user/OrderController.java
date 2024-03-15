package com.sky.controller.user;


import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C端-订单接口")
@RestController("userOrderController")
public class OrderController {


    @Autowired
    private OrderService orderService;


    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @author: zjy
     * @return: Result<OrderSubmitVO>
     **/
    @ApiOperation("用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }


    /**
     * 订单支付[修改
     *
     * @param ordersPaymentDTO
     * @author: zjy
     * @return: Result<OrderPaymentVO>
     **/
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
/*
        // 无法使用，返回的值就是null，直接不使用
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
*/

        // 修改为直接回调，修改订单
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("修改订单交易成功：{}", ordersPaymentDTO.getOrderNumber());

        return Result.success(null);
    }


    /**
     * 分页查询历史订单
     *
     * @param page
     * @param pageSize
     * @param status
     * @author: zjy
     * @return: Result<PageResult>
     **/
    @GetMapping("/historyOrders")
    @ApiOperation("分页查询历史订单")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        log.info("分页查询历史订单：{},{},{}", page, pageSize, status);
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }


    /**
     * 查询订单详情
     *
     * @param id
     * @author: zjy
     * @return: Result<OrderVO>
     **/
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询详情订单id：{}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }


    /**
     * 取消订单[困难在业务分析
     *
     * @param id
     * @author: zjy
     * @return: Result
     **/
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) throws Exception {
        log.info("取消订单id：{}", id);
        orderService.userCancelById(id);
        return Result.success();
    }


    /**
     * 再来一单
     *
     * @param id
     * @author: zjy
     * @return: Result
     **/
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) throws Exception {
        log.info("再来一单订单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }


    /**
     * 催单
     *
     * @param id
     * @author: zjy
     * @return: Result
     **/
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result reminder(@PathVariable Long id) throws Exception {
        log.info("催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }


}

