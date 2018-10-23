package com.experience.moon.play.params.request;

public class FirstReqDto extends BaseReqDao{

    /**
     * VPN : false
     * deviceVersion : iPhone5,2
     * WIFI_SSID : houbu3
     * httpProxy :
     * getEnv : true
     * batteryState : 1
     * loadAllApp : false
     * appName :
     * batteryLevel : 61
     * appVer : 1.0
     * Device_IDFA : A2FD396C-1B30-4025-8249-FAAC6FF0CC8E
     * WIFI_BSSID : e8:fc:af:a3:ee:fd
     * simType :
     * dns : 192.168.1.1
     * totalDiskSpace : 13610778624
     * hasJailBreakFile : true
     * screenBrightness : 45
     * freeDiskSpace : 10020610048
     * fromStartDuration : 4365
     * isJailBreak : true
     * canOpenCydiaUrl : true
     * sysVer : 8.1
     * idfaEnabled : true
     * deviceModel : iPhone
     * netStatus : 2
     * plid : 17
     * sfuid : 2222
     * uuid : 2222
     * bid : 123213
     */

    /**
     * 是否开启VPN
     */
    public boolean VPN;
    /**
     * 设备版本
     */
    public String deviceVersion;
    public String WIFI_SSID;
    public String httpProxy;
    public boolean getEnv;
    public int batteryState;
    public boolean loadAllApp;
    public String appName;
    public String batteryLevel;
    /**
     * app版本
     */
    public String appVer;//
    public String Device_IDFA;
    public String WIFI_BSSID;
    public int simType;
    public String dns;
    public long totalDiskSpace;
    public boolean hasJailBreakFile;
    public String screenBrightness;
    public long freeDiskSpace;
    public String fromStartDuration;
    public boolean isJailBreak;
    public boolean canOpenCydiaUrl;
    /**
     * 系统版本
     */
    public String sysVer;
    public boolean idfaEnabled;
    public String deviceModel = "android";
    public int netStatus;
    /**
     * IMEI
     */
    public String plid;
    /**
     * IMEI
     */
    public String imei;
    public String sfuid;
    public String uuid;
    /**
     * 包名
     */
    public String bid;
}
