package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param order
     * @author: zjy
     * @return: void
     **/
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页条件查询并按下单时间排序[两用订单搜索
     *
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单
     *
     * @param
     * @author: zjy
     * @return: Orders
     **/
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    /**
     * 根据状态统计订单数量[节约性能
     *
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态以及订单时间查询订单
     *
     * @param status
     * @param orderTime
     * @author: zjy
     * @return: List<Orders>
     **/
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);


    /**
     * 营业额数据统计[统计完成状态的下单时间
     *
     * @param localDateTime
     * @param endOfDay
     * @author: zjy
     * @return: Double
     **/
    @Select("SELECT sum(amount) FROM orders WHERE status = 5 and  order_time >= #{localDateTime} " +
            "AND order_time <= #{endOfDay}")
    Double sumByMap(LocalDateTime localDateTime, LocalDateTime endOfDay);

    /**
     * 订单统计接口
     * @author: zjy
     * @param beginTime
     * @param endTime
     * @param status
     * @return: Integer
     **/
    Integer countByMap(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 查询销量排名top10接口
     * @author: zjy
     * @param begin
     * @param end
     * @return: List<GoodsSalesDTO>
     **/
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
