package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Category;

/**
 * 分类服务
 *
 * @author m0v1
 * @date 2022年10月10日 00:52
 */
public interface CategoryService extends IService<Category> {

    /**
     * 分页查询
     *
     * @param page     当前页
     * @param pageSize 每页行数
     * @return
     */
    ResponseInfo<Page<Category>> listByPage(int page, int pageSize);
}
