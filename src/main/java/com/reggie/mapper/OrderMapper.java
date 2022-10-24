package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年10月21日 02:00
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
