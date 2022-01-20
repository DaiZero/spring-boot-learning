package com.dzero.shiro.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ResponseResult 返回结果
 *
 * @author DaiZedong
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseResult<T> implements Serializable {
    /**
     * 时间戳
     */
    private Long timestamp = System.currentTimeMillis();
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 状态码
     */
    private String code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    public ResponseResult(Boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(true, null, null, data);
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(true, null, null, null);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(false, null, message, null);
    }

    public static <T> ResponseResult<T> error(String code, String message) {
        return new ResponseResult<>(false, code, message, null);
    }
}
