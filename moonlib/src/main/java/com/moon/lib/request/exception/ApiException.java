package com.moon.lib.request.exception;

/**
 * 请求错误处理类
 * Created by Moon on 2018/6/22.
 */

public class ApiException extends Exception {
    public int code;
    public String message;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }
}
