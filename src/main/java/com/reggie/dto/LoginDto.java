package com.reggie.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author m0v1
 * @date 2022年10月18日 01:04
 */
@Data
public class LoginDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;
}
