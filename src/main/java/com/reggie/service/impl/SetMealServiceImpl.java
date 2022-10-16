package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.Category;
import com.reggie.entity.SetMeal;
import com.reggie.mapper.SetMealMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.SetMealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author m0v1
 * @date 2022年10月10日 01:41
 */
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {

    @Autowired
    CategoryService categoryService;

    @Override
    public ResponseInfo<Page<SetMealDto>> listByPage(int page, int pageSize, String setMealName) {
        Page<SetMeal> setMealPage = new Page<>(page, pageSize);
        Page<SetMealDto> setMealDtoPage = new Page<>();
        // 条件构造器
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setMealName), SetMeal::getName, setMealName);
        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        // 执行分页查询
        page(setMealPage, queryWrapper);
        // 对象拷贝,且忽略对查询到的结果集的浅拷贝
        BeanUtils.copyProperties(setMealPage, setMealDtoPage, "records");
        // 遍历结果集,迭代更新setMealDto属性
        List<SetMealDto> setMealDtoList = setMealPage.getRecords().stream().map(item -> {
            Long categoryId = item.getCategoryId();
            LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
            categoryLambdaQueryWrapper.eq(Category::getId, categoryId);
            Category category = categoryService.getOne(categoryLambdaQueryWrapper);

            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(item, setMealDto);
            // 赋值套餐名称
            setMealDto.setCategoryName(category.getName());
            return setMealDto;
        }).collect(Collectors.toList());

        setMealDtoPage.setRecords(setMealDtoList);

        return ResponseInfo.success(setMealDtoPage);
    }
}
