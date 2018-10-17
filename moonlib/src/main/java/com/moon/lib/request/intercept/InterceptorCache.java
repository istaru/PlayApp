package com.moon.lib.request.intercept;

import com.moon.lib.app.BaseApp;
import com.moon.lib.tools.BaseTools;
import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络缓存
 * Created by Moon on 2018/4/18.
 */

public class InterceptorCache implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();//获取请求
        //如果没有网络，则启用 FORCE_CACHE
        if (!BaseTools.isNetworkConnected(BaseApp.getContext())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if (BaseTools.isNetworkConnected(BaseApp.getContext())) {
            //有网的时候读接口上的@Headers里的配置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            int maxTime = 4 * 24 * 60 * 60;//缓存的时间，单位秒
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxTime)
                    .removeHeader("Pragma")
                    .build();
        }
//        Response response = chain.proceed(request);
//        int length = 60;//缓存的时间是60秒
//        Response.Builder rpBuilder = response.newBuilder();
//        rpBuilder.removeHeader("Pragma");//清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
//        rpBuilder.removeHeader("Cache-Control");
//        rpBuilder.header("Cache-Control", "public, max-age=" + length);
//        return rpBuilder.build();
    }
}
