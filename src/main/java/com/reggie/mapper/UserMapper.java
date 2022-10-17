package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年10月18日 00:48
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
