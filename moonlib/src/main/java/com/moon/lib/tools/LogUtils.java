package com.moon.lib.tools;

import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Printer;

public class LogUtils {
    public static final String TAG_OKHTTP = "OkHttp";
    public static final String TAG_JS = "Js";
    public static final String TAG_INFO = "Info";

    public static Printer t(String tag) {
        return Logger.t(tag);
    }

//    public static void d(){
//        Logger.v(String message); // VERBOSE级别，可添加占位符
//        Logger.d(Object object); // DEBUG级别，打印对象
//        Logger.d(String message); // DEBUG级别，可添加占位符
//        Logger.i(String message); // INFO级别，可添加占位符
//        Logger.w(String message); // WARN级别，可添加占位符
//        Logger.e(String message); // ERROR级别，可添加占位符
//        Logger.e(Throwable throwable, String message); // ERROR级别，可添加占位符
//        Logger.wtf(String message); // ASSERT级别，可添加占位符
//        Logger.xml(String xml);
//        Logger.json(String json);
//    }
}
