package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车管理控制器
 *
 * @author m0v1
 * @date 2022年10月19日 02:04
 */
@Slf4j
@RequestMapping("/shoppingCart")
@RestController
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品/套餐到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public ResponseInfo<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加菜品/套餐到购物车:shoppingCart={}", shoppingCart);
        ShoppingCart existedItem = shoppingCartService.addItem2ShoppingCart(shoppingCart);

        return ResponseInfo.success(existedItem);
    }

    /**
     * 查询用户购物车数据
     *
     * @return
     */
    @GetMapping("list")
    public ResponseInfo<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return ResponseInfo.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public ResponseInfo<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //SQL:delete from shopping_cart where user_id = ?
        shoppingCartService.remove(queryWrapper);

        return ResponseInfo.success("成功清空购物车");
    }
}
