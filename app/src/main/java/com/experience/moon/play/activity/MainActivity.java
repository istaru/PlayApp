package com.experience.moon.play.activity;

import android.Manifest;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.experience.moon.play.R;
import com.experience.moon.play.tools.JsUtils;
import com.moon.lib.view.web.X5WebView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import butterknife.BindColor;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

@Route(path = "/activity/MainActivity")
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
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        webView.addJavascriptInterface(new JsUtils(MainActivity.this, webView), "native_android");
        getPermissions(Manifest.permission.READ_PHONE_STATE);
    }

    private void getPermissions(String permission) {
        RxPermissions rxPermissions = new RxPermissions(mContext);
        rxPermissions
                .requestEach(permission)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {//已获取权限
                            if(TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE,permission.name)){
                                webView.loadUrl("https://t.dijiadijia.com/dist/");
                            } else {
                                getPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {//已拒绝权限
                            getPermissions(permission.name);
                        } else {//拒绝权限请求,并不再询问

                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {

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
