package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.entity.ShoppingCart;
import com.reggie.mapper.ShoppingCartMapper;
import com.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author m0v1
 * @date 2022年10月19日 02:03
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Override
    public ShoppingCart addItem2ShoppingCart(ShoppingCart shoppingCart) {
        // 根据添加的菜品/套餐id和用户id查询用户购物车
        // 获取用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        // 根据菜品/套餐id添加条件构造器
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart existedItem = getOne(queryWrapper);
        // 如果被添加的菜品/套餐已存在,则更新其数量
        if (existedItem != null) {
            existedItem.setNumber(existedItem.getNumber() + 1);
            updateById(existedItem);
        } else {
            // 否则就新增到购物车
            save(shoppingCart);
            existedItem = shoppingCart;
        }
        return existedItem;
    }
}
