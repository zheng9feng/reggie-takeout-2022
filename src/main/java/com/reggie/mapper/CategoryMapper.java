package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年09月30日 22:26
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
