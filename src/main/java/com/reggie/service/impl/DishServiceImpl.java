package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author m0v1
 * @date 2022年10月10日 01:41
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisTemplate redisTemplate;


    @Transactional
    @Override
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        super.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //获取菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将每条flavor的dishId赋上值
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public ResponseInfo<Page<DishDto>> listByPage(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 过滤逻辑删除 0-未删除
        queryWrapper.eq(Dish::getIsDeleted, 0);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        //获取原records数据
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return ResponseInfo.success(dishDtoPage);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 根据菜品id获取菜品
        Dish dish = getById(id);

        //条件查询flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }

    @Override
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public void updateWithFlavor(DishDto dishDto) {
        //根据id修改菜品的基本信息
        super.updateById(dishDto);

        //通过dish_id,删除菜品的flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //获取前端提交的flavor数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        //将每条flavor的dishId赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());


        //将数据批量保存到dish_flavor数据库
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public void updateStatus(String status, List<String> dishIds) {
        // 根据条件字段更新部分字段:根据菜品id更新菜品状态
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, dishIds);
        updateWrapper.set(Dish::getStatus, status);

        update(updateWrapper);
    }

    @Override
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public void deleteDishByIds(List<String> dishIds) {
        // 逻辑删除
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, dishIds);
        updateWrapper.set(Dish::getIsDeleted, "1");

        update(updateWrapper);
    }

}
