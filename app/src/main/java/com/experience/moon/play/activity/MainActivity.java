package com.experience.moon.play.activity;

import android.Manifest;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.experience.moon.play.R;
import com.moon.lib.tools.LogUtils;
import com.moon.lib.view.web.X5WebView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindColor;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity implements OnRefreshLoadMoreListener {

    @BindColor(R.color.app_color)
    int appColor;
    @BindView(R.id.web_view)
    X5WebView webView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
//        refreshLayout.setEnableRefresh(false);
//        refreshLayout.setEnableLoadMore(false);
//        webView.addJavascriptInterface(new JsUtils(MainActivity.this, webView), "native_android");
//        webView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
//            @Override
//            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
//                sslErrorHandler.proceed();// 接受所有网站的证书
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
//                return super.shouldOverrideUrlLoading(webView, url);
//            }
//
//            @Override
//            public void onPageFinished(WebView webView, String s) {
//                super.onPageFinished(webView, s);
//            }
//        });
//        webView.loadUrl("https://t.dijiadijia.com/dist/");
//        //        webView.loadUrl("https://www.baidu.com/");
//        //        webView.loadUrl("http://soft.imtt.qq.com/browser/tes/feedback.html");
        getPermissions(Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void getPermissions(String...permission) {
        RxPermissions rxPermissions = new RxPermissions(mContext);
        rxPermissions
                .requestEach(permission)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        getStatus(permission);
//                        if(TextUtils.equals(permission.name,Manifest.permission.READ_PHONE_STATE)){
//                        }
                    }
                });
    }

    private void getStatus(Permission permission) {
        if (permission.granted) {//已获取权限
            LogUtils.t(LogUtils.TAG_INFO).e(permission.name,"权限获取");
        } else if (permission.shouldShowRequestPermissionRationale) {//已拒绝权限
            LogUtils.t(LogUtils.TAG_INFO).e(permission.name,"权限拒绝");
        } else {//拒绝权限请求,并不再询问
            LogUtils.t(LogUtils.TAG_INFO).e(permission.name,"权限拒绝并不再询问");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}
