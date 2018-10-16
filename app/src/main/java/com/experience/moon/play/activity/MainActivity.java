package com.experience.moon.play.activity;

import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.experience.moon.play.R;
import com.experience.moon.play.tools.JSUtils;
import com.jaeger.library.StatusBarUtil;
import com.moon.lib.view.web.X5WebView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindColor;
import butterknife.BindView;

public class MainActivity extends BaseActivity implements OnRefreshLoadMoreListener{

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
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        webView.addJavascriptInterface(new JSUtils(this,webView),"native_android");
        webView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();// 接受所有网站的证书
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return super.shouldOverrideUrlLoading(webView, url);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }
        });
        webView.loadUrl("https://t.dijiadijia.com/dist/");
//        webView.loadUrl("https://www.baidu.com/");
//        webView.loadUrl("http://soft.imtt.qq.com/browser/tes/feedback.html");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webView.canGoBack()){
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
