package com.reggie.util;

import java.util.Random;

/**
 * 随机生成验证码工具类
 */
public class ValidateCodeUtils {
    /**
     * 随机生成数字验证码
     *
     * @param length 长度为4位或者6位
     * @return 指定长度的数字验证码
     */
    public static Integer generateValidateCode(int length) {
        Integer captcha;
        if (length == 4) {
            captcha = new Random().nextInt(9999);//生成随机数，最大为9999
            if (captcha < 1000) {
                captcha = captcha + 1000;//保证随机数为4位数字
            }
        } else if (length == 6) {
            captcha = new Random().nextInt(999999);//生成随机数，最大为999999
            if (captcha < 100000) {
                captcha = captcha + 100000;//保证随机数为6位数字
            }
        } else {
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return captcha;
    }

    /**
     * 随机生成指定长度字符串验证码
     *
     * @param length 长度
     * @return 指定长度的字符验证码
     */
    public static String generateValidateCode4String(int length) {
        Random rdm = new Random();
        String hexString = Integer.toHexString(rdm.nextInt());
        return hexString.substring(0, length);
    }
}
