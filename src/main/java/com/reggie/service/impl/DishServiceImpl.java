package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.Dish;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @author m0v1
 * @date 2022年10月10日 01:41
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
