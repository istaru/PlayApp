package com.experience.moon.play.tools;

import android.webkit.JavascriptInterface;

import com.experience.moon.play.activity.BaseActivity;
import com.moon.lib.view.web.X5WebView;

public class JsUtils {

    private BaseActivity mContext;
    private X5WebView webView;

    public JsUtils(BaseActivity mContext, X5WebView webView) {
        this.mContext = mContext;
        this.webView = webView;
    }

    @JavascriptInterface
    public void js_netFull(String data){

    }

    @JavascriptInterface
    public void js_netFull(){

    }
}
