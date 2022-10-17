package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.SetMeal;

import java.util.List;

/**
 * 套餐服务
 *
 * @author m0v1
 * @date 2022年10月10日 00:52
 */
public interface SetMealService extends IService<SetMeal> {

    /**
     * 分页查询套餐信息
     *
     * @param page        当前页码
     * @param pageSize    每页行数
     * @param setMealName 套餐名称
     * @return
     */
    ResponseInfo<Page<SetMealDto>> listByPage(int page, int pageSize, String setMealName);

    /**
     * 根据套餐id删除套餐
     *
     * @param ids 套餐id集合
     */
    void deleteSetMealByIds(List<Long> ids);

    /**
     * 更新套餐状态
     *
     * @param status     0-停售 1-启售
     * @param setMealIds 套餐id集合
     */
    void updateStatus(String status, List<String> setMealIds);

    /**
     * 新增套餐
     *
     * @param setMealDto 套餐DTO
     */
    void addSetMeal(SetMealDto setMealDto);

    /**
     * 获取套餐信息
     *
     * @param setMealId 套餐id
     * @return
     */
    SetMealDto getSetMeal(Long setMealId);

    /**
     * 更新套餐
     *
     * @param setMealDto 套餐DTO
     */
    void updateSetMeal(SetMealDto setMealDto);
}
