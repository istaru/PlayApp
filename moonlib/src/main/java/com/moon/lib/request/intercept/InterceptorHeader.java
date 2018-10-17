package com.moon.lib.request.intercept;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加全局header，token等等
 * Created by Moon on 2018/4/18.
 */
public class InterceptorHeader implements Interceptor {

    private String key;
    private String value;

    public InterceptorHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.header(key, value);
        return chain.proceed(builder.build());
    }
}