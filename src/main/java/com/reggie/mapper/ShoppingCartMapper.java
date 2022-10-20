package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年10月19日 02:02
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
