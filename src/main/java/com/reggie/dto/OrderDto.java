package com.reggie.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author m0v1
 * @date 2022年10月24日 23:49
 */
@Data
public class OrderDto {


    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 订单状态
     * 1-待付款 2-待派送 3-已派送 4-已完成 5-已取消
     */
    private Integer status;

    /**
     * 订单详细信息
     */
    private List<OrderDetailDto> orderDetails;

    /**
     * 订单物品合计数量
     */
    private Integer sumNum;

    /**
     * 订单合计金额
     */
    private BigDecimal amount;

}
