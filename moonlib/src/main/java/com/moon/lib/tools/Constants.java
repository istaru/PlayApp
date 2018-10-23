package com.moon.lib.tools;

import android.content.Context;
import android.provider.Settings;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Moon on 2018/7/5.
 */

public class Constants {
    public static String intentName = "object";
    public static String APPKEY = "ANDROID";

    public static String TOKEN = "token";
    public static String PHONE = "phoneNum";
    public static String USER_NAME = "userName";
    public static String USER_ICON = "userIcon";
    public static String USER_ID = "uid";
    public static String USER_PWD = "userPwd";
    public static String APR_NAME = "aprName";
    public static String NICK_NAME = "nickName";
    public static String IS_REAL_NAME = "is_real_name";
    /**
     * App文件夹名称
     */
    public static String FILE_NAME = "lingyongdai";
    public static String FILE_NET = "netCache";
    public static String FILE_IMAGES = "images";
    public static String FILE_WEB = "web";
    public static String FILE_DOWNLOAD = "download";
    public static String PHONE_INPUT = "phone_input";
    public static String PWD_INPUT = "pwd_input";
    public static String AUTO_BID_OBJECT = "auto_bid_";
    public static String DEFAULT_REAL_NAME = "340XXXX";
    public static String VERSION_CODE = "340XXXX";

    /**
     * Android权限
     */
    public static final int PERMISSIONS_CODE = 101;
    public static final int CODE_102 = 102;
    public static final int CODE_200 = 200;

    /**
     * 获取手机唯一标识
     */
    public static String phonId(Context context) {
        return 3 + "_" + Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }

    /**
     * 获取系统现在的时间
     */
    public static String nowTime() {
        return System.currentTimeMillis() + "";
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT＋8"));
        System.out.println("时间" + format.format(calendar.getTime()));
//        String str = "123木头人";
//        byte[] hash = new byte[0];
//        try {
//            hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        StringBuilder hex = new StringBuilder(hash.length * 2);
//        for (byte b : hash) {
//            if ((b & 0xFF) < 0x10){
//                //避免出现0x5这样的数字，应该是0x05
//                hex.append("0");
//            }
//            hex.append(Integer.toHexString(b & 0xFF));
//        }
//        String result = hex.toString();
//        System.out.println("MD5加密32位的值：" + result);
        String s = "";
        System.out.println("MD5加密截取16位的值：" + s);
    }

}
