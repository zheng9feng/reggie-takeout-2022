package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.OrderDetail;
import com.reggie.mapper.OrderDetailMapper;
import com.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author m0v1
 * @date 2022年10月21日 02:02
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
