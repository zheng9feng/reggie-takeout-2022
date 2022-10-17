package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.AddressBook;
import com.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author m0v1
 * @date 2022年10月18日 01:49
 */
@Slf4j
@RequestMapping("/addressBook")
@RestController
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 根据用户id查询地址簿
     *
     * @param addressBook 地址簿
     * @return
     */
    @GetMapping("/list")
    public ResponseInfo<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("查询地址簿:addressBook={}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        return ResponseInfo.success(list);
    }

    /**
     * 新增地址簿
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public ResponseInfo<AddressBook> addAddressBook(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("新增地址簿:addressBook={}", addressBook);

        addressBookService.save(addressBook);
        return ResponseInfo.success(addressBook);
    }

    /**
     * 删除地址簿
     *
     * @param addressBookIds 地址簿id集合
     * @return
     */
    @DeleteMapping
    public ResponseInfo<String> deleteAddressBooks(@RequestParam("ids") List<Long> addressBookIds) {
        addressBookService.removeByIds(addressBookIds);

        return ResponseInfo.success("地址已删除!");
    }

    /**
     * 获取地址簿
     *
     * @param addressBookId 地址簿id
     * @return
     */
    @GetMapping("/{addressBookId}")
    public ResponseInfo<AddressBook> getAddressBook(@PathVariable Long addressBookId) {

        AddressBook addressBook = addressBookService.getById(addressBookId);

        return ResponseInfo.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public ResponseInfo<AddressBook> getDefault(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        //条件构造器
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        updateWrapper.set(AddressBook::getIsDefault, 0);

        //将与用户id所关联的所有地址的is_default字段更新为0
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        //再将前端传递的地址id的is_default字段更新为1
        addressBookService.updateById(addressBook);

        return ResponseInfo.success(addressBook);
    }
}
