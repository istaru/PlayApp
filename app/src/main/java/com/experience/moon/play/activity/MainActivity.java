package com.experience.moon.play.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

//import com.alibaba.android.arouter.facade.annotation.Autowired;
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONObject;
import com.experience.moon.play.R;
import com.experience.moon.play.params.PhoneInfo;
import com.experience.moon.play.params.request.FirstReqDto;
import com.experience.moon.play.params.response.FirstResDto;
import com.experience.moon.play.params.response.ResSubscriber;
import com.experience.moon.play.ports.ApiService;
import com.experience.moon.play.tools.JsUtils;
import com.moon.lib.request.HttpClient;
import com.moon.lib.request.response.BaseResDto;
import com.moon.lib.request.response.Transformer;
import com.moon.lib.tools.Constants;
import com.moon.lib.tools.LogUtils;
import com.moon.lib.tools.PrefShared;
import com.moon.lib.view.web.X5WebView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindColor;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

//@Route(path = "/activity/MainActivity")
public class MainActivity extends BaseActivity implements OnRefreshLoadMoreListener {

    @BindColor(R.color.app_color)
    int appColor;
    @BindView(R.id.web_view)
    X5WebView webView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

//    @Autowired
    String uId = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        Uri uri = getIntent().getData();
        if(null != uri){
            uId = uri.getQueryParameter("uid");
            if(null != uId){
                activationUser();
            }
        }
        getPermissions(Manifest.permission.READ_PHONE_STATE);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        webView.addJavascriptInterface(new JsUtils(MainActivity.this, webView), "native_android");
    }

    private void activationUser() {
        PhoneInfo phoneInfo = new PhoneInfo(this);
        FirstReqDto reqDto = new FirstReqDto();
        reqDto.VPN = phoneInfo.isVpnUsed();
        reqDto.plid = phoneInfo.getImei();
        reqDto.imei = phoneInfo.getImei();
        reqDto.sysVer = phoneInfo.getSystemVersion();
        reqDto.deviceVersion = phoneInfo.getSystemModel();
        reqDto.appVer = phoneInfo.getVerName();
        reqDto.bid = phoneInfo.getPkName();
        reqDto.simType = phoneInfo.getProvidersName();
        reqDto.WIFI_SSID = phoneInfo.getWifiSsId();
        reqDto.WIFI_BSSID = phoneInfo.getWifiBSsId();
        reqDto.sfuid = uId;
        HttpClient.getInstance()
                .createService(ApiService.class)
                .activationUser(reqDto)
                .compose(Transformer.<FirstResDto>call())
                .subscribe(new ResSubscriber<FirstResDto>() {
                    @Override
                    protected void onSuccess(FirstResDto resDto) {
                        PrefShared.saveString(mContext,Constants.USER_ID,resDto.data.uid);
                    }

                    @Override
                    protected void onError(int code, String message) {

                    }
                });
    }

    private void getPermissions(String permission) {
        RxPermissions rxPermissions = new RxPermissions(mContext);
        rxPermissions
                .requestEach(permission)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {//已获取权限
                            if (TextUtils.equals(Manifest.permission.READ_PHONE_STATE, permission.name) &&
                                    null == PrefShared.getString(mContext, Constants.USER_ID)) {
                                sendPlid();
                            }
                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
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

    /**
     * 如果本地没有保存uId
     */
    private void sendPlid() {
        PhoneInfo phoneInfo = new PhoneInfo(this);
        FirstReqDto reqDto = new FirstReqDto();
        reqDto.VPN = phoneInfo.isVpnUsed();
        reqDto.plid = phoneInfo.getImei();
        reqDto.imei = phoneInfo.getImei();
        reqDto.sysVer = phoneInfo.getSystemVersion();
        reqDto.deviceVersion = phoneInfo.getSystemModel();
        reqDto.appVer = phoneInfo.getVerName();
        reqDto.bid = phoneInfo.getPkName();
        reqDto.simType = phoneInfo.getProvidersName();
        reqDto.WIFI_SSID = phoneInfo.getWifiSsId();
        reqDto.WIFI_BSSID = phoneInfo.getWifiBSsId();
        HttpClient.getInstance()
                .createService(ApiService.class)
                .startApp(reqDto)
                .compose(Transformer.<FirstResDto>call())
                .subscribe(new ResSubscriber<FirstResDto>() {
                    @Override
                    protected void onSuccess(FirstResDto resDto) {
                        if(resDto.code == Constants.CODE_102){
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(resDto.data.url));//Url 就是你要打开的网址
                            intent.setAction(Intent.ACTION_VIEW);
                            startActivity(intent); //启动浏览器
                            finish();
                        } else if(resDto.code == Constants.CODE_200){
                            PrefShared.saveString(mContext,Constants.USER_ID,resDto.data.uid);
                        }
                    }

                    @Override
                    protected void onError(int code, String message) {

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
