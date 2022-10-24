package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.OrderDetailDto;
import com.reggie.dto.OrderDto;
import com.reggie.entity.*;
import com.reggie.mapper.OrderMapper;
import com.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author m0v1
 * @date 2022年10月21日 02:02
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submit(Orders orders) {
        //获取当前用户id
        Long currentUserId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentUserId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list();

        if (CollectionUtils.isEmpty(shoppingCarts)) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(currentUserId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误，不能下单");
        }

        //向订单表插入数据，一条数据
        //设置订单id
        long orderId = IdWorker.getId();
        // 累加求出实付金额
        AtomicInteger amount = new AtomicInteger(0);
        // 订单明细
        List<OrderDetail> orderDetailList = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(currentUserId);
        orders.setAddressBookId(addressBookId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress("");
        orders.setAddress(
                (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) +
                        (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                        (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()) +
                        (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public ResponseInfo<Page<OrderDto>> listOrderDtoByPage(int page, int pageSize) {

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        // 用户ID
        Long userID = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userID);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        page(ordersPage, queryWrapper);
        List<Orders> records = ordersPage.getRecords();
        if (records == null) {
            return null;
        }

        List<OrderDto> orderDtoList = records.stream().map(item -> {
            OrderDto orderDto = new OrderDto();

            orderDto.setStatus(item.getStatus());
            orderDto.setOrderTime(item.getOrderTime());

            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailQueryWrapper.in(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailQueryWrapper);
            AtomicInteger atomicInteger = new AtomicInteger();
            AtomicLong atomicLong = new AtomicLong();
            List<OrderDetailDto> orderDetailDtoList = orderDetailList.stream().map(orderDetailItem -> {
                OrderDetailDto orderDetailDto = new OrderDetailDto();
                orderDetailDto.setName(orderDetailItem.getName());
                Integer number = orderDetailItem.getNumber();
                orderDetailDto.setNumber(number);
                atomicInteger.addAndGet(number);
                atomicLong.addAndGet(orderDetailItem.getAmount().longValue());

                return orderDetailDto;
            }).collect(Collectors.toList());

            orderDto.setOrderDetails(orderDetailDtoList);
            orderDto.setSumNum(atomicInteger.get());
            orderDto.setAmount(new BigDecimal(atomicLong.get()));
            return orderDto;
        }).collect(Collectors.toList());

        Page<OrderDto> orderDtoPage = new Page<>();
        orderDtoPage.setRecords(orderDtoList);
        return ResponseInfo.success(orderDtoPage);
    }
}
