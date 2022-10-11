package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.SetMeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author m0v1
 * @date 2022年10月10日 00:58
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;

    @Override
    public ResponseInfo<Page<Category>> listByPage(int page, int pageSize) {
        // 构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 排序
        queryWrapper.orderByAsc(Category::getSort);

        this.page(pageInfo, queryWrapper);
        return ResponseInfo.success(pageInfo);
    }

    @Override
    public void deleteCategoryById(Long categoryID) {
        //查看当前分类是否关联了菜品，如果已经关联，则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加dish查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryID);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除!");
        }
        //查看当前分类是否关联了套餐，如果已经关联，则抛出异常
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加dish查询条件，根据分类id进行查询
        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId, categoryID);
        int setMealCount = setMealService.count(setMealLambdaQueryWrapper);
        if (setMealCount > 0) {
            throw new CustomException("当前分类下关联了套餐，不能删除!");
        }

        //正常删除
        removeById(categoryID);
    }
}
