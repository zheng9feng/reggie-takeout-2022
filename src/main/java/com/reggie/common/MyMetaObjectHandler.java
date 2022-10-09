package com.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 元数据对象处理器
 *
 * @author m0v1
 * @date 2022年10月08日 21:48
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充【insert】");
        log.info("metaObject is :" + metaObject.toString());
        // 自动填充时间字段
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 自动填充用户字段
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充【update】");
        log.info("metaObject is :" + metaObject.toString());
        // 自动填充时间字段
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 自动填充用户字段
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
