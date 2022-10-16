package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.CustomException;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.DishDto;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
}
