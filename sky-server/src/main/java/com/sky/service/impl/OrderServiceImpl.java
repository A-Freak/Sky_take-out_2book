package com.sky.service.impl;


import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @author: zjy
     * @return: OrderSubmitVO
     **/
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常(地址簿为空、购物车数据为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        // [除了主键id 还差14条数据
        // 收货人、手机号、地址在地址簿中可查询
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        // 订单号 此处实现使用时间戳[后续可改为 UUID]
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        // 下单用户id、订单状态、支付状态、下单时间
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);// 订单状态默认为,待付款
        order.setPayStatus(Orders.UN_PAID);// 支付状态默认为,未支付
        order.setOrderTime(LocalDateTime.now());
        // 以上还有六条数据未使用[结账时间、用户名、订单取消原因、订单拒绝原因、订单取消时间、送达时间]，在后续会进行使用股权
        //向订单表插入1条数据[需要进行主键返回
        orderMapper.insert(order);


        //订单明细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        list.forEach(cart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            // BeanUtils会进行覆盖
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        });
        //向订单明细表插入n条数据
        orderDetailMapper.insertBatch(orderDetailList);


        //清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        //封装V0返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return orderSubmitVO;
    }
}

