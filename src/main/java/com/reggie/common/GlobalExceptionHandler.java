package com.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author m0v1
 * @date 2022年10月07日 17:07
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseInfo<String> sqlIntegrityConstraintViolationExceptionHandler(SQLIntegrityConstraintViolationException exception) {
        String message = exception.getMessage();
        log.error(message);
        String warn = null;
        if (message.contains("Duplicate entry")) {
            String[] infos = message.split(" ");
            warn = "用户" + infos[2] + "已存在!";
        } else {
            warn = "未知错误!";
        }
        return ResponseInfo.error(warn);
    }
}
