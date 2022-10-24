package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.OrderDto;
import com.reggie.entity.Orders;

/**
 * @author m0v1
 * @date 2022年10月21日 02:01
 */
public interface OrderService extends IService<Orders> {

    /**
     * 支付提交
     *
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 分页查询订单信息
     *
     * @param page     当前页
     * @param pageSize 每页行数
     * @return
     */
    ResponseInfo<Page<OrderDto>> listOrderDtoByPage(int page, int pageSize);
}
