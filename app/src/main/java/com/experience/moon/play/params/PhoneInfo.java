package com.experience.moon.play.params;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.experience.moon.play.activity.BaseActivity;
import com.experience.moon.play.params.request.FirstReqDto;
import com.moon.lib.tools.LogUtils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class PhoneInfo {
    private BaseActivity mContext;
    private TelephonyManager telephonyManager;

    public PhoneInfo(BaseActivity context) {
        mContext = context;
        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取手机服务商信息
     */
    @SuppressLint("MissingPermission")
    public int getProvidersName() {
        int providersName = 0;
        String IMSI = "";
        try {
            IMSI = telephonyManager.getSubscriberId();//IMSI
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            if (null != IMSI) {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                    providersName = 1;
                } else if (IMSI.startsWith("46001")) {
                    providersName = 2;
                } else if (IMSI.startsWith("46003")) {
                    providersName = 3;
                } else {
                    providersName = 4;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return providersName;
    }

    /**
     * 是否使用代理(WiFi状态下的,避免被抓包)
     */
    public boolean isWifiProxy() {
        final boolean is_ics_or_later = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (is_ics_or_later) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portstr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portstr != null ? portstr : "-1"));
            System.out.println(proxyAddress + "~");
            System.out.println("port = " + proxyPort);
        } else {
            proxyAddress = android.net.Proxy.getHost(mContext);
            proxyPort = android.net.Proxy.getPort(mContext);
            LogUtils.t(LogUtils.TAG_INFO).e("address = ", proxyAddress + "~");
            LogUtils.t(LogUtils.TAG_INFO).e("port = ", proxyPort + "~");
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    /**
     * 是否正在使用VPN
     */
    public boolean isVpnUsed() {
        boolean flag = false;
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    LogUtils.t(LogUtils.TAG_INFO).e("isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        flag = true;// The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * WifiBSsId
     *
     * @return
     */
    @SuppressLint("WifiManagerLeak")
    public String getWifiBSsId() {
        String wifiBssId = "";
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiInfo wifiInfo = ((WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE)).getConnectionInfo();
                    wifiBssId = wifiInfo.getBSSID();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wifiBssId;
    }

    /**
     * WifiSsId
     *
     * @return
     */
    @SuppressLint("WifiManagerLeak")
    public String getWifiSsId() {
        String wifiSsId = "";
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiInfo wifiInfo = ((WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE)).getConnectionInfo();
                    wifiSsId = wifiInfo.getSSID();
                    if (wifiSsId.contains("\"")) {
                        wifiSsId.replace("\"", "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wifiSsId;
    }

    /**
     * app版本号
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public int getVerCode() {
        int verCode = 0;
        try {
            verCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {

        }
        return verCode;
    }

    /**
     * app版本名
     *
     * @return
     */
    public String getVerName() {
        String verName;
        try {
            verName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;//APP版本号
        } catch (PackageManager.NameNotFoundException e) {
            verName = e.getMessage();
        }
        return verName;
    }

    /**
     * IMEI号
     *
     * @return
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getImei() {
        return telephonyManager.getDeviceId();
    }

    /**
     * App包名
     *
     * @return
     */
    public String getPkName() {
        return mContext.getPackageName();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
}
