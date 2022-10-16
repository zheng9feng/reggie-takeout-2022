package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.DishDto;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
