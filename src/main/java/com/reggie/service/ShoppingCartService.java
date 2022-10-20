package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.entity.ShoppingCart;

/**
 * @author m0v1
 * @date 2022年10月19日 02:03
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加菜品/套餐到购物车
     *
     * @param shoppingCart
     * @return
     */
    ShoppingCart addItem2ShoppingCart(ShoppingCart shoppingCart);
}
