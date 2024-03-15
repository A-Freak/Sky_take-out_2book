package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 自定义定时任务，实现订单状态定时处理
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理支付超时订单[每8分钟1次]
     */
    @Scheduled(cron = "0 0/8 * * * ?")
    public void processTimeoutOrder() {
        log.info("处理支付超时订单：{}", new Date());
        // 非常标准的命名法
        // select * from orders where state = #{status} and order_time < #{orderTime}
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT,LocalDateTime.now().minusMinutes(15));
        // 进行判断
        if (ordersList != null && ordersList.size() > 0) {
            ordersList.forEach(orders -> {
                // 修改为取消、补充修改时间以及修改原因
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单支付超时，自动取消！");
                orders.setCancelTime(LocalDateTime.now());
                // 进行修改
                orderMapper.update(orders);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单[每天凌晨一点]
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理派送中订单：{}", new Date());
        // 相同写法
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().minusHours(1));
        // 进行判断
        if (ordersList != null && ordersList.size() > 0) {
            ordersList.forEach(orders -> {
                // 修改为完成
                orders.setStatus(Orders.COMPLETED);
                // 进行修改
                orderMapper.update(orders);
            });
        }
    }

}