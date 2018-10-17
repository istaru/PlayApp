package com.moon.lib.request.exception;

/**
 * 请求错误处理类
 * Created by Moon on 2018/6/22.
 */

public class ApiException extends Exception {
    private int code;
    private String message;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
