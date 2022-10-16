package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.SetMealDto;
import com.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 套餐管理控制器
 *
 * @author m0v1
 * @date 2022年10月16日 11:05
 */
@Slf4j
@RequestMapping("/setmeal")
@RestController
public class SetMealController {

    @Autowired
    SetMealService setMealService;


    /**
     * 分页查询套餐信息
     *
     * @param page        当前页码
     * @param pageSize    每页行数
     * @param setMealName 套餐名称
     * @return
     */
    @RequestMapping("/page")
    public ResponseInfo<Page<SetMealDto>> listSetMeal(int page, int pageSize, String setMealName) {
        log.info("分页查询套餐信息:当前页为第{}页,每页行数为{}.");
        return setMealService.listByPage(page, pageSize, setMealName);
    }
}
