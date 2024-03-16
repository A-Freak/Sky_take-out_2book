package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    UserMapper userMapper;

    /**
     * 营业额数据统计
     *
     * @param begin
     * @param end
     * @author: zjy
     * @return: TurnoverReportVO
     **/
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {

        // 获取其 begin 与 end 之间的天数
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
        }

        // 获取金额
        List<String> turnverList = new ArrayList<>();

        dateList.forEach(date -> {
            // 将LocalDate变为LocalDateTime使用SQL语句查询
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            // 【默认为0000也就是MIN】将时间扩充到当天的23:59:59 或者标准中的 LocalTime.MAX
            LocalDateTime endOfDay = beginTime.withHour(23).withMinute(59).withSecond(59);

            // SQL语句也能进行累加操作【不用在外面对麻烦的【不可修改】BigDecimal进行累加
            Double turnover = orderMapper.sumByMap(beginTime, endOfDay);

            // 缺点就是有可能获取不到就为null，需要添加判断
            turnover = turnover == null ? 0.0 : turnover;
            turnverList.add(turnover.toString());
        });

        // 构造封装VO
        return TurnoverReportVO.builder()
                // 引用了新的拼接方式，可对任意集合【顶层接口】进行拼接，不需要特意变为String
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(String.join(",", turnverList))
                .build();
    }

    /**
     * 用户统计接口
     *
     * @param begin
     * @param end
     * @author: zjy
     * @return: UserReportVO
     **/
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 同上获取其 begin 与 end 之间的天数
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
        }

        // 总用户以及新增用户
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        dateList.forEach(date -> {
            // 将LocalDate变为LocalDateTime使用SQL语句查询
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

            Integer newUser = userMapper.getUserCount(beginTime, endOfDay);
            Integer totalUser = userMapper.getUserCount(null, endOfDay);

            newUserList.add(newUser);
            totalUserList.add(totalUser);
        });

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计接口
     *
     * @param begin
     * @param end
     * @author: zjy
     * @return: OrderReportVO
     **/
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        // 同上获取其 begin 与 end 之间的天数
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
        }

        // 获取完了每日订单数以及每日有效订单数，2个总数累加即可，最后相除
        //每天订单总数集合、每天有效订单数集合
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        dateList.forEach(date -> {
            // 将LocalDate变为LocalDateTime使用SQL语句查询
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //select count(*) from order where order_time > begin and order_time < end and (status = 5)
            Integer orderCount = orderMapper.countByMap(beginTime, endTime, null);
            Integer validOrderCount = orderMapper.countByMap(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        });

        // 总数获取【又见高级流写法！！要补充的知识】
        //时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    /**
     * 查询销量排名top10接口
     *
     * @param begin
     * @param end
     * @author: zjy
     * @return: SalesTop10ReportVO
     **/
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 均为对数据库的SQL语言使用,订单明细表还需要连接订单表观察是否为有效订单【分组的使用
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        // 将其进行转化后，收集将值分割
        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName)
                .collect(Collectors.toList()), ",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList()), ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
