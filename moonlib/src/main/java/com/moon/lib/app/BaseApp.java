package com.moon.lib.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import com.moon.lib.R;
import com.moon.lib.tools.BaseTools;
import com.moon.lib.tools.LogUtils;
import com.moon.lib.view.RefreshHeaderView;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.smtt.sdk.QbSdk;

public class BaseApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initLog();
        initX5();
    }

    /**
     * 获取全局上下文
     *
     * @return
     */
    public static Context getContext() {
        return context;
    }

    /**
     * 初始化Log
     */
    private void initLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  //是否显示线程信息 默认true
                .methodCount(10)         //要显示多少条方法线 默认值2
                .methodOffset(7)        //隐藏内部方法调用到偏移量 默认值5
//                .logStrategy(customLog) //更改日志策略以打印输出. 默认值 LogCat
//                .tag("My custom tag")   //每个日志的全局标记. 默认值 PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BaseTools.isApkInDebug(getApplicationContext());
            }
        });
    }

    /**
     * 初始化腾讯X5内核WebView
     */
    private void initX5() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.t(LogUtils.TAG_INFO).e("腾讯X5：" + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }


    /**
     * 设置全局的刷新头部和尾部
     */
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new RefreshHeaderView(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableSize(20).setProgressResource(R.drawable.img_refresh_anim).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }
}
