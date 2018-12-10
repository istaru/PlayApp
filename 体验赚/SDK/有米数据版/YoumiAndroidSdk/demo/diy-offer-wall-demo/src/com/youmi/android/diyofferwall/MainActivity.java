package com.youmi.android.diyofferwall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import cde.ewd.adw.AdManager;
import cde.ewd.adw.os.EarnPointsOrderInfo;
import cde.ewd.adw.os.EarnPointsOrderList;
import cde.ewd.adw.os.PointsChangeNotify;
import cde.ewd.adw.os.PointsEarnNotify;
import cde.ewd.adw.os.PointsManager;
import cde.ewd.adw.os.df.DiyOfferWallManager;

public class MainActivity extends BaseActivity
		implements OnClickListener, PointsChangeNotify, PointsEarnNotify {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 初始化UI
		initView();
		
		// (可选)关闭有米log输出，建议开发者在嵌入有米过程中不要关闭，以方便随时捕捉输出信息，出问题时可以快速定位问题
		// AdManager.getInstance(Context context).setEnableDebugLog(false);
		
		// 初始化接口，应用启动的时候调用，参数：appId, appSecret, isEnableYoumiLog
		AdManager.getInstance(this).init("0ea38b9e22ce5ad7", "628cf20c72c11050", true);
		
		// (可选) 检查积分墙源数据配置
		DiyOfferWallManager.getInstance(this).checkDiyOffersAdConfig();
		
		// ----------------------------------------------------------------
		// (可选) 服务器回调积分配置
		// OffersManager.getInstance(this).setCustomUserId("test");
		// OffersManager.getInstance(this).setUsingServerCallBack(true);
		// ----------------------------------------------------------------
		
		// 请务必调用以下代码，告诉积分墙源数据SDK应用启动，可以让SDK进行一些初始化操作。该接口务必在SDK的初始化接口之后调用。
		DiyOfferWallManager.getInstance(this).onAppLaunch();
		
		// （可选）注册积分监听-随时随地获得积分的变动情况
		PointsManager.getInstance(this).registerNotify(this);

		// （可选）注册积分订单赚取监听（sdk v4.10版本新增功能）
		PointsManager.getInstance(this).registerPointsEarnNotify(this);

		// (可选)设置是否在通知栏显示下载相关提示。默认为true，标识开启；设置为false则关闭。（sdk v4.10版本新增功能）
		// AdManager.getInstance(this).setIsDownloadTipsDisplayOnNotification(false);
		
		// (可选)设置安装完成后是否在通知栏显示已安装成功的通知。默认为true，标识开启；设置为false则关闭。（sdk v4.10版本新增功能）
		// AdManager.getInstance(this).setIsInstallationSuccessTipsDisplayOnNotification(false);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// （可选）注销积分余额监听-如果在onCreate中注册了，那这里必须得注销
		PointsManager.getInstance(this).unRegisterNotify(this);

		// （可选）注销积分订单赚取监听-如果在onCreate中注册了，那这里必须得注销（sdk v4.10版本新增功能）
		PointsManager.getInstance(this).unRegisterPointsEarnNotify(this);

		// 请务必在应用退出的时候调用以下代码，告诉SDK应用已经关闭，可以让SDK进行一些资源的释放和清理。
		DiyOfferWallManager.getInstance(this).onAppExit();
	}
	
	private void initView() {
		findViewById(R.id.btn_award_points).setOnClickListener(this);
		findViewById(R.id.btn_spend_points).setOnClickListener(this);
		findViewById(R.id.btn_get_add_fans_ad).setOnClickListener(this);
		findViewById(R.id.btn_get_all_app_opt).setOnClickListener(this);
		findViewById(R.id.btn_get_all_game_opt).setOnClickListener(this);
		findViewById(R.id.btn_get_game).setOnClickListener(this);
		findViewById(R.id.btn_get_app).setOnClickListener(this);
		findViewById(R.id.btn_get_extra_task_list).setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		updatePoints(PointsManager.getInstance(this).queryPoints());
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 奖励20积分
		case R.id.btn_award_points:
			PointsManager.getInstance(this).awardPoints(20);
			break;
		
		// 消耗10积分
		case R.id.btn_spend_points:
			PointsManager.getInstance(this).spendPoints(10);
			break;
		
		// 请求加粉任务广告
		case R.id.btn_get_add_fans_ad:
			startActivity(new Intent(MainActivity.this, AddFansAdListActivity.class));
			break;
		
		// 请求所有类型广告 （游戏先于应用展示）
		case R.id.btn_get_all_app_opt:
			showAdListActivity(DiyOfferWallManager.ym_param_REQUEST_ALL);
			break;
		
		// 请求所有类型广告 （应用先于游戏展示）
		case R.id.btn_get_all_game_opt:
			showAdListActivity(DiyOfferWallManager.ym_param_REQUEST_SPECIAL_SORT);
			break;
		
		// 请求游戏广告
		case R.id.btn_get_game:
			showAdListActivity(DiyOfferWallManager.ym_param_REQUEST_GAME);
			break;
		
		// 请求应用广告
		case R.id.btn_get_app:
			showAdListActivity(DiyOfferWallManager.ym_param_REQUEST_APP);
			break;
		
		// 请求追加任务列表
		case R.id.btn_get_extra_task_list:
			showAdListActivity(DiyOfferWallManager.ym_param_REQUEST_EXTRA_TASK);
			break;
		
		default:
			break;
		}
	}
	
	/**
	 * 打开app列表activity
	 *
	 * @param requestType
	 */
	private void showAdListActivity(int requestType) {
		Intent intent = new Intent(MainActivity.this, OfferWallAdListActivity.class);
		intent.putExtra("requestType", requestType);
		startActivity(intent);
	}
	
	/**
	 * 积分余额发生变动时，就会回调本方法（本回调方法执行在UI线程中）
	 */
	@Override
	public void onPointBalanceChange(float pointsBalance) {
		updatePoints(pointsBalance);
	}
	
	/**
	 * 积分订单赚取时会回调本方法（本方法执行在UI线程中）
	 */
	@Override
	public void onPointEarn(Context context, EarnPointsOrderList list) {
		for (int i = 0; i < list.size(); ++i) {
			EarnPointsOrderInfo info = list.get(i);
			Log.i("test", info.getMessage());
		}
	}
}