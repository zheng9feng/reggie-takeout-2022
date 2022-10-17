package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author m0v1
 * @date 2022年10月18日 01:47
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
