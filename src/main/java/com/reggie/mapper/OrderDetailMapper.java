package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年10月21日 02:01
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
