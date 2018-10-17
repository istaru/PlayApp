package com.moon.lib.request;


import com.google.gson.GsonBuilder;
import com.moon.lib.app.BaseApp;
import com.moon.lib.request.intercept.InterceptorCache;
import com.moon.lib.request.intercept.InterceptorLog;
import com.moon.lib.tools.BaseTools;
import com.moon.lib.tools.Constants;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求类
 * Created by Moon on 2018/6/22.
 */

public class HttpClient {
    private static HttpClient instance;
    private OkHttpClient.Builder clientBuilder;
    public String url = UrlConfig.BASE_URL;

    public static HttpClient getInstance() {
        if (null == instance) {
            synchronized (HttpClient.class) {
                if (null == instance) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }

    public HttpClient() {
        InterceptorCache cacheInterceptor = new InterceptorCache();
        Cache cache = new Cache(BaseTools.makeFile(Constants.FILE_NET), 1024 * 1024 * 100);//将网络数据缓存到本地
        InterceptorLog logInterceptor = new InterceptorLog();
        if (BaseTools.isApkInDebug(BaseApp.getContext())) {//显示日志
            new InterceptorLog(InterceptorLog.Level.BASIC);
        } else {
            new InterceptorLog(InterceptorLog.Level.HEADERS);
        }
        clientBuilder = new OkHttpClient.Builder();
        clientBuilder.retryOnConnectionFailure(true)//连接失败后是否重新连接
                .connectTimeout(15, TimeUnit.SECONDS)//超时时间秒为单位
                .addInterceptor(cacheInterceptor)//设置本地缓存
                .addInterceptor(logInterceptor)
                .addNetworkInterceptor(cacheInterceptor)//设置网络缓存
                .cache(cache);//设置网络缓存;
    }

    /**
     * 设置网络请求域名
     *
     * @param url
     * @return
     */
    public HttpClient setBaseUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 创建Retrofit
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createService(Class<T> clazz) {
        Retrofit.Builder builder = new Retrofit.Builder();
        return builder.baseUrl(url)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))//请求的结果转为实体类
                .addConverterFactory(FastJsonConverterFactory.create())//请求的结果直返回JSON
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//适配RxJava2.0, RxJava1.x则为RxJavaCallAdapterFactory.create()
                .build()
                .create(clazz);
    }

    public OkHttpClient.Builder getBuilder() {
        return clientBuilder;
    }

    /**
     * 设置header
     *
     * @return
     */
//    public HttpClient setHeaders() {
//        if (!TextUtils.isEmpty(PrefShared.getString(BaseApplication.getContext(), Constants.TOKEN))) {
//            HttpClient.getInstance().getBuilder().addInterceptor(new InterceptorHeader("access_token", PrefShared.getString(BaseApplication.getContext(), Constants.TOKEN)));
//        } else {
//            HttpClient.getInstance().getBuilder().addInterceptor(new InterceptorHeader("access_token", ""));
//        }
//        return this;
//    }

}
