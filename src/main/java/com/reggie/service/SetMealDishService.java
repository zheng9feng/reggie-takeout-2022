package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.SetMealDish;

import java.util.List;

/**
 * 套餐与菜品关联
 *
 * @author m0v1
 * @date 2022年10月16日 16:38
 */
public interface SetMealDishService extends IService<SetMealDish> {

    /**
     * 删除套餐与菜品关联关系
     *
     * @param setMealIds 套餐id集合
     */
    void deleteSetMealDishBySetMealId(List<Long> setMealIds);

    /**
     * 新增套餐与菜品关联关系
     *
     * @param setMealDto 套餐DTO
     */
    void addSetMealDish(SetMealDto setMealDto);

    /**
     * 更新套餐与菜品关联关系
     *
     * @param setMealDto 套餐DTO
     */
    void updateSetMealDish(SetMealDto setMealDto);
}
