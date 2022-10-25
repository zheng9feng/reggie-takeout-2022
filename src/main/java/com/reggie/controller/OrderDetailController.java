package com.reggie.controller;

import com.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author m0v1
 * @date 2022年10月21日 02:04
 */
@Slf4j
@RequestMapping("/orderDetail")
@RestController
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

}
