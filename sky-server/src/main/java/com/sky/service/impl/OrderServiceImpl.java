package com.sky.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;

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


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        // 新功能，转换为对应字节码的类型
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        // 补全预交易订单号
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 分页查询历史订单【分页查询多个SQL】
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @author: zjy
     * @return: PageResult
     **/
    @Transactional
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        // 只能查询本用户的并且添加条件
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                // OrderVO继承了 Order 的所有属性!!!
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        // 此处由于并非直接使用分页，而是对分页中添加内容，故传入数据要进行修改
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @author: zjy
     * @return: OrderVO
     **/
    @Transactional
    public OrderVO details(Long id) {

        Orders orders = orderMapper.getById(id);
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /**
     * 用户取消订单
     * @author: zjy
     * @param id
     * @return: void
     **/
    public void userCancelById(Long id) throws Exception {
        Orders orders = orderMapper.getById(id);
        Integer status = orders.getStatus();
        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 业务规则：待支付和待接单状态下，用户可直接取消订单
        // 如果在待接单状态下取消订单，需要给用户退款
        // 商家已接单状态下，用户取消订单需电话沟通商家、派送中状态下，用户取消订单需电话沟通商家【隐含道理：不让退，直接报异常，需要你打电话
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        if (orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 订单处于待接单状态下取消，需要进行退款[Integer类型小数字可以直接用==号进行比较
        if (status.equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    orders.getNumber(), //商户订单号
                    orders.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款【并不需要将订单状态修改为退款，购买后退款才是退款】
            orders.setPayStatus(Orders.REFUND);
        }

        // 最后：取消订单后需要将订单状态修改为“已取消”
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }


    /**
     * 再来一单
     * @author: zjy
     * @param id
     * @return: void
     **/
    public void repetition(Long id) {
        // 将订单中的物品放入购物车中,
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        Long userId = BaseContext.getCurrentId();
        //区别不只有订单id，还有用户id以及创建时间[主键必要不然表不接受]
        orderDetails.forEach(orderDetail->{
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart,"id");
            // 更改区别数据，id不能复制
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        });
    }
}

