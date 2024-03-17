package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    WorkspaceService workspaceService;

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


    /**
     * 导出近30天的运营数据报表
     *
     * @param response
     **/
    public void exportBusinessData(HttpServletResponse response) {

        // 从今天往前推30天,到昨天共30天【不包括今天】
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览运营数据，提供给Excel模板文件
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
