package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Category;
import com.reggie.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 *
 * @author m0v1
 * @date 2022年10月10日 00:46
 */
@Slf4j
@RestController
@RequestMapping("/category")
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页行数", required = true, paramType = "query", dataType = "int")
    })

    public ResponseInfo<Page<Category>> listCategory(int page, int pageSize) {
        log.info("分类信息分页查询:page = {}, pageSize = {}.", page, pageSize);
        return categoryService.listByPage(page, pageSize);
    }

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public ResponseInfo<String> addCategory(@RequestBody Category category) {
        log.info("新增分类:{}", category);
        categoryService.save(category);
        return ResponseInfo.success("新增分类成功");
    }

    /**
     * 删除分类信息
     *
     * @param categoryID 分类ID
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public ResponseInfo<String> delete(Long categoryID) {
        log.info("将要删除的分类id:{}", categoryID);

        categoryService.deleteCategoryById(categoryID);
        return ResponseInfo.success("分类信息删除成功");
    }

    /**
     * 获取菜品分类列表
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取菜品分类列表")
    public ResponseInfo<List<Category>> listCategory(Category category) {
        return categoryService.listCategory(category);
    }
}
