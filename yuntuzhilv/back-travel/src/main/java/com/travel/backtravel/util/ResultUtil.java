package com.travel.backtravel.util;

import lombok.Data;

@Data
public class ResultUtil<T> {

    private Integer code;

    private String message;

    private T data;

    private ResultUtil() {}

    public static <T> ResultUtil<T> success() {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static <T> ResultUtil<T> success(T data) {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> ResultUtil<T> success(String message, T data) {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> ResultUtil<T> error(Integer code, String message) {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> ResultUtil<T> unauthorized() {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(401);
        result.setMessage("未登录");
        return result;
    }

    public static <T> ResultUtil<T> forbidden() {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(403);
        result.setMessage("无权限");
        return result;
    }

    public static <T> ResultUtil<T> serverError(String message) {
        ResultUtil<T> result = new ResultUtil<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
