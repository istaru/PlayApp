package com.moon.lib.request.response;

/**
 * Created by Moon on 2018/7/13.
 */

public class BaseResDto<T> {
    public int code;
    public String msg;
    public T data;
}
