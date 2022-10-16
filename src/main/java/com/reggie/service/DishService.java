package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

import java.util.List;

/**
 * 菜品服务
 *
 * @author m0v1
 * @date 2022年10月10日 00:52
 */
public interface DishService extends IService<Dish> {

    /**
     * 保存菜品和口味
     *
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 分页查询菜品
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    ResponseInfo<Page<DishDto>> listByPage(int page, int pageSize, String name);

    /**
     * 获取菜品信息
     *
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品
     *
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 更新菜品状态
     *
     * @param status  0-停售 1-起售
     * @param dishIds 菜品id集合
     */
    void updateStatus(String status, List<String> dishIds);

    /**
     * 逻辑删除菜品
     *
     * @param dishIds 菜品id集合
     */
    void deleteDishByIds(List<String> dishIds);
}
