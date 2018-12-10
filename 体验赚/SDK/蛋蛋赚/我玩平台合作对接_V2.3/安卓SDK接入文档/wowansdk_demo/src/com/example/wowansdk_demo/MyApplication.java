package com.example.wowansdk_demo;

import com.liulishuo.filedownloader.FileDownloader;
import com.lz.playmy.WoWanSdk;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		WoWanSdk.init("1001", "zw210", "867855022550858", "fdsmkfshdik423432");//渠道号，用户id，devideid，key
		
//		WoWanSdk.setTitltStyle(R.drawable.ic_launcher, "列表。。。", "#ffffff", 15, "", 0, "详情、、", "0000ff", 20, "#ff0000");
		// 下载初始化
		FileDownloader.setup(this);
	}

}
