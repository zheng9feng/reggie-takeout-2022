package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.SetMealDto;
import com.reggie.service.SetMealDishService;
import com.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    SetMealDishService setMealDishService;


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
        log.info("分页查询套餐信息:当前页为第{}页,每页行数为{}.", page, pageSize);
        return setMealService.listByPage(page, pageSize, setMealName);
    }

    @DeleteMapping
    public ResponseInfo<String> delete(@RequestParam List<Long> ids) {
        log.info("套餐管理--套餐删除:ids={}.", ids);
        setMealService.deleteSetMealByIds(ids);

        return ResponseInfo.success("删除套餐成功!");
    }

    /**
     * 套餐停启售
     *
     * @param status     0-停售 1-启售
     * @param setMealIds 套餐id集合
     * @return
     */
    @PostMapping("/status/{status}")
    public ResponseInfo<String> stopSell(@PathVariable String status, @RequestParam("ids") List<String> setMealIds) {
        log.info("套餐停启售:status={},setMealIds={}", status, setMealIds);
        setMealService.updateStatus(status, setMealIds);

        String message = "0".equals(status) ? "套餐已停售" : "套餐已起售";
        return ResponseInfo.success(message);
    }

    @PostMapping
    public ResponseInfo<String> addSetMeal(@RequestBody SetMealDto setMealDto) {
        log.info("新增套餐,setMealDto:{}", setMealDto);
        setMealService.addSetMeal(setMealDto);

        return ResponseInfo.success("新增套餐成功!");
    }

    @GetMapping("/{setMealId}")
    public ResponseInfo<SetMealDto> getSetMeal(@PathVariable Long setMealId) {
        log.info("根据套餐id查询套餐信息,套餐id为{}.", setMealId);
        return ResponseInfo.success(setMealService.getSetMeal(setMealId));
    }

    @PutMapping
    public ResponseInfo<String> updateSetMeal(@RequestBody SetMealDto setMealDto) {
        log.info("更新套餐,setMealDto:{}", setMealDto);
        setMealService.updateSetMeal(setMealDto);

        return ResponseInfo.success("更新套餐成功!");
    }
}
