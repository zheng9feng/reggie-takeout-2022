package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.SetMeal;

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
}
