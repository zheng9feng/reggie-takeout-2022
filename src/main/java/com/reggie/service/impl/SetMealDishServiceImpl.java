package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.SetMealDish;
import com.reggie.mapper.SetMealDishMapper;
import com.reggie.service.SetMealDishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author m0v1
 * @date 2022年10月16日 16:40
 */
@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetMealDish> implements SetMealDishService {

    @Transactional
    @Override
    public void deleteSetMealDishBySetMealId(List<Long> setMealIds) {
        // 删除套餐菜品关联关系
        LambdaUpdateWrapper<SetMealDish> setMealDishUpdateWrapper = new LambdaUpdateWrapper<>();
        setMealDishUpdateWrapper.set(SetMealDish::getIsDeleted, "1");
        setMealDishUpdateWrapper.in(SetMealDish::getSetMealId, setMealIds);
        update(setMealDishUpdateWrapper);
    }

    @Transactional
    @Override
    public void addSetMealDish(SetMealDto setMealDto) {
        // 保存套餐与菜品关联信息
        List<SetMealDish> setMealDishList = setMealDto.getSetmealDishes();
        // 套餐id
        Long id = setMealDto.getId();
        setMealDishList = setMealDishList.stream().map(item -> {
            item.setSetMealId(id);
            return item;
        }).collect(Collectors.toList());

        saveBatch(setMealDishList);
    }

    @Transactional
    @Override
    public void updateSetMealDish(SetMealDto setMealDto) {
        // 先逻辑删除已有的套餐与菜品关系
        deleteSetMealDishBySetMealId(Collections.singletonList(setMealDto.getId()));
        // 添加新的套餐和菜品关系
        addSetMealDish(setMealDto);
    }
}
