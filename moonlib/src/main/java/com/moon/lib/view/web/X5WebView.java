package com.moon.lib.view.web;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import com.moon.lib.R;
import com.moon.lib.tools.BaseTools;
import com.moon.lib.tools.Constants;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import java.util.Map;

/**
 * Created by Moon on 2018/5/17.
 */

public class X5WebView extends WebView {

    private static ProgressView progressView;//进度条
    private Context context;

    public X5WebView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();

    }

    public X5WebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
        this.context = context;
        init();
    }

    public X5WebView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean b) {
        super(context, attributeSet, i, map, b);
        this.context = context;
        init();
    }

    private void init() {
        //初始化进度条
        progressView = new ProgressView(context);
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) BaseTools.dpChangePx(2)));
        progressView.setColor(ContextCompat.getColor(context, R.color.transparent));
        //把进度条加到Webview中
        addView(progressView);
        initWebSettings();
        setWebChromeClient(new MyWebCromeClient());
        setWebViewClient(new com.tencent.smtt.sdk.WebViewClient(){
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
    }

    /**
     * 初始化设置
     */
    private void initWebSettings() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);//支持JavaScript
        settings.setAllowFileAccess(true);//允许访问文件
        settings.setAllowFileAccessFromFileURLs(true);//通过此API可以设置是否允许通过file url加载的Javascript读取其他的本地文件
        settings.setAllowUniversalAccessFromFileURLs(true);//通过此API可以设置是否允许通过file url加载的Javascript可以访问其他的源，包括其他的文件和http,https等其他的源
        settings.setLoadsImagesAutomatically(true);//图片显示
        //设置缓存
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//如果内容已经存在cache 则使用cache，即使是过去的历史记录。如果cache中不存在，从网络中获取！
        settings.setDomStorageEnabled(true);//开启DOM storage API功能
        settings.setDatabaseEnabled(true);//开启database storage API 功能
        settings.setAppCacheEnabled(true);//开启 Application Caches 功能
        String path = BaseTools.makeFile(Constants.FILE_WEB).getPath();
        settings.setDatabasePath(path);
        settings.setAppCachePath(path);
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);//开启缩放1
        settings.setSupportZoom(true);//开启缩放2
        settings.setDisplayZoomControls(false);//去掉缩放按钮
    }

    public void setWebViewClient(WebViewClient webViewClient) {
    }

    public static class MyWebCromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int i) {
            if (i == 100) {
                //加载完毕进度条消失
                progressView.setVisibility(View.GONE);
            } else {
                //更新进度
                progressView.setProgress(i);
            }
            super.onProgressChanged(view, i);
        }
    }
}
