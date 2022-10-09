package com.reggie.common;

/**
 * 基于ThreadLocal封装工具类,用于保存和获取当前登录的用户ID
 *
 * @author m0v1
 * @date 2022年10月09日 19:40
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 保存用户ID
     *
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取用户ID
     *
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
