package com.experience.moon.play.application;

//import com.alibaba.android.arouter.launcher.ARouter;

import com.moon.lib.app.BaseApp;
import com.moon.lib.tools.Constants;

import cde.ewd.adw.AdManager;

public class MainApplication extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
//        initARouter();
        initYM();
    }

    /**
     * 初始化有米
     */
    private void initYM() {
        AdManager.getInstance(mContext).init(Constants.YOUMI_KEY, Constants.YOUMI_SECRET, true);
    }

//    /**
//     * 初始化阿里Router
//     */
//    private void initARouter() {
//        ARouter.openLog();     // 打印日志
//        ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        ARouter.init( this ); // 尽可能早，推荐在Application中初始化
//    }
}
