package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.CustomException;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author m0v1
 * @date 2022年10月13日 08:12
 */
@Slf4j
@RequestMapping("/dish")
@RestController
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public ResponseInfo<String> save(@RequestBody DishDto dishDto) {
        log.info("接收的dishDto数据：{}", dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return ResponseInfo.success("新增菜品成功");
    }

    @GetMapping("/page")
    public ResponseInfo<Page<DishDto>> listDishDto(int page, int pageSize, String name) {

        return dishService.listByPage(page, pageSize, name);
    }

    /**
     * 根据菜品id查询菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseInfo<DishDto> get(@PathVariable Long id) {
        //查询
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return ResponseInfo.success(dishDto);
    }

    @PutMapping
    public ResponseInfo<String> update(@RequestBody DishDto dishDto) {

        log.info("接收的dishDto数据：{}", dishDto.toString());

        //更新数据库中的数据
        dishService.updateWithFlavor(dishDto);

        return ResponseInfo.success("新增菜品成功");
    }

    /**
     * 删除菜品
     *
     * @param dishIds
     * @return
     */
    @DeleteMapping
    public ResponseInfo<String> delete(@RequestParam("ids") List<String> dishIds) {
        if (CollectionUtils.isEmpty(dishIds)) {
            throw new CustomException("未选定待删除的菜品!");
        }

        dishService.deleteDishByIds(dishIds);
        return ResponseInfo.success("菜品删除成功");
    }


    /**
     * 菜品停启售
     *
     * @param status
     * @param dishIds
     * @return
     */
    @PostMapping("/status/{status}")
    public ResponseInfo<String> stopSell(@PathVariable String status, @RequestParam("ids") List<String> dishIds) {
        log.info("菜品停启售:status={},dishIds={}", status, dishIds);
        dishService.updateStatus(status, dishIds);

        String message = "0".equals(status) ? "菜品已停售" : "菜品已起售";
        return ResponseInfo.success(message);
    }

    /**
     * 根据菜品列表查询
     *
     * @param dish 菜品信息
     * @return
     */
    @GetMapping("/list")
    public ResponseInfo<List<DishDto>> list(Dish dish) {
        String key = "dish:" + dish.getCategoryId() + ":" + dish.getStatus();
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtos != null) {
            return ResponseInfo.success(dishDtos);
        }
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（1为起售，0为停售）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 过滤掉逻辑删除的数据
        queryWrapper.eq(Dish::getIsDeleted, 0);

        List<Dish> dishList = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;

        }).collect(Collectors.toList());

        // 存入缓存
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return ResponseInfo.success(dishDtoList);
    }
}
