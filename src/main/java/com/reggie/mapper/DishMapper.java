package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Dish;
import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年09月30日 22:26
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
