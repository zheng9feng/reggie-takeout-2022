package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.Category;
import com.reggie.entity.SetMeal;
import com.reggie.entity.SetMealDish;
import com.reggie.mapper.SetMealMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.SetMealDishService;
import com.reggie.service.SetMealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author m0v1
 * @date 2022年10月10日 01:41
 */
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    SetMealDishService setMealDishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ResponseInfo<Page<SetMealDto>> listByPage(int page, int pageSize, String setMealName) {
        Page<SetMeal> setMealPage = new Page<>(page, pageSize);
        Page<SetMealDto> setMealDtoPage = new Page<>();
        // 条件构造器
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        // 排除逻辑删除的数据
        queryWrapper.eq(SetMeal::getIsDeleted, "0");
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

    @Transactional
    @Override
    public void deleteSetMealByIds(List<Long> ids) {
        cleanCache();
        // 判断套餐状态,如果套餐处于启售状态,则不能删除
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMeal::getStatus, "1");
        queryWrapper.in(SetMeal::getId, ids);
        int count = count(queryWrapper);
        if (count > 0) {
            throw new CustomException("该套餐处于启售状态,不能被删除!");
        }

        // 删除套餐(逻辑删除)
        LambdaUpdateWrapper<SetMeal> setMealUpdateWrapper = new LambdaUpdateWrapper<>();
        setMealUpdateWrapper.set(SetMeal::getIsDeleted, "1");
        setMealUpdateWrapper.in(SetMeal::getId, ids);
        update(setMealUpdateWrapper);
        // 删除套餐菜品关联关系
        setMealDishService.deleteSetMealDishBySetMealId(ids);
    }

    @Transactional
    @Override
    public void updateStatus(String status, List<String> setMealIds) {
        // 根据条件字段更新部分字段:根据套餐id更新套餐状态
        LambdaUpdateWrapper<SetMeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SetMeal::getId, setMealIds);
        updateWrapper.set(SetMeal::getStatus, status);

        update(updateWrapper);
    }

    @Transactional
    @Override
    public void addSetMeal(SetMealDto setMealDto) {
        // 保存套餐信息
        save(setMealDto);

        setMealDishService.addSetMealDish(setMealDto);
    }

    @Override
    public SetMealDto getSetMeal(Long setMealId) {
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMeal::getId, setMealId);
        queryWrapper.eq(SetMeal::getStatus, "1");
        queryWrapper.eq(SetMeal::getIsDeleted, 0);
        SetMeal setMeal = getOne(queryWrapper);

        LambdaQueryWrapper<SetMealDish> setMealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishLambdaQueryWrapper.eq(SetMealDish::getSetMealId, setMealId);
        setMealDishLambdaQueryWrapper.eq(SetMealDish::getIsDeleted, 0);
        List<SetMealDish> setMealDishList = setMealDishService.list(setMealDishLambdaQueryWrapper);

        SetMealDto setMealDto = new SetMealDto();
        BeanUtils.copyProperties(setMeal, setMealDto);
        setMealDto.setSetmealDishes(setMealDishList);

        return setMealDto;
    }

    @Transactional
    @Override
    public void updateSetMeal(SetMealDto setMealDto) {
        cleanCache();
        updateById(setMealDto);
        setMealDishService.updateSetMealDish(setMealDto);
    }

    /**
     * 全量清空套餐缓存的数据
     */
    private void cleanCache() {
        String keyPattern = "setmeal:*";
        Set keys = redisTemplate.keys(keyPattern);
        redisTemplate.delete(keys);
    }
}
