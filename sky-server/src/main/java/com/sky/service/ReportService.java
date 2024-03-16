package com.sky.service;


import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {

    /**
     * 营业额数据统计
     *
     * @param begin
     * @param end
     * @author: zjy
     * @return: TurnoverReportVO
     **/
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);


    /**
     * 用户统计接口
     * @author: zjy
     * @param begin
     * @param end
     * @return: TurnoverReportVO
     **/
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);


    /**
     * 订单统计接口
     * @author: zjy
     * @param begin
     * @param end
     * @return: OrderReportVO
     **/
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);


    /**
     * 查询销量排名top10接口
     * @author: zjy
     * @param begin
     * @param end
     * @return: SalesTop10ReportVO
     **/
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
