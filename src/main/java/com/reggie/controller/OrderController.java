package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.OrderDto;
import com.reggie.entity.Orders;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author m0v1
 * @date 2022年10月21日 02:03
 */
@Slf4j
@RequestMapping("/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 支付提交
     *
     * @param orders 待支付订单
     * @return
     */
    @PostMapping("/submit")
    public ResponseInfo<String> submit(@RequestBody Orders orders) {
        log.info("用户提交订单,订单信息:orders={}", orders);

        orderService.submit(orders);
        return ResponseInfo.success("用户下单成功");
    }

    /**
     * 查看订单
     */
    @GetMapping("/userPage")
    ResponseInfo<Page<OrderDto>> listOrder(int page, int pageSize) {
        log.info("查看订单信息:page={},pageSize={}.", page, pageSize);
        return orderService.listOrderDtoByPage(page, pageSize);
    }
}
