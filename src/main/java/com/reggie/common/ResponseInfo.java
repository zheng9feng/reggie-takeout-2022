package com.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 后端通用返回
 *
 * @author m0v1
 * @date 2022年09月30日 22:32
 */
@Data
public class ResponseInfo<T> {

    /**
     * 编码：1成功，0和其它数字为失败
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 动态数据
     */
    private Map map = new HashMap();

    public static <T> ResponseInfo<T> success(T object) {
        ResponseInfo<T> r = new ResponseInfo<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> ResponseInfo<T> error(String msg) {
        ResponseInfo r = new ResponseInfo();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public ResponseInfo<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
