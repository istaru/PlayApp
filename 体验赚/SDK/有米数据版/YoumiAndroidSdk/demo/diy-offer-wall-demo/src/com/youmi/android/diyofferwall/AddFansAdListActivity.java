package com.youmi.android.diyofferwall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.youmi.android.diyofferwall.adapter.AddFansAdListViewAdapter;

import cde.ewd.adw.os.PointsChangeNotify;
import cde.ewd.adw.os.PointsManager;
import cde.ewd.adw.os.df.AddFansAdList;
import cde.ewd.adw.os.df.DiyOfferWallManager;
import cde.ewd.adw.os.df.OnRequestAddFansAdListListener;

import java.lang.ref.WeakReference;

/**
 * @author youmi
 * @since 2017-03-13 14:31
 */
public class AddFansAdListActivity extends BaseActivity implements PointsChangeNotify, OnItemClickListener {
	
	/**
	 * 更新列表数据
	 */
	private final static int UPDATE_LIST_DATA_SUCCESS = 1;
	
	/**
	 * 更新列表数据失败
	 */
	private final static int UPDATE_LIST_DATA_FAILED = 2;
	
	/**
	 * 记录最近一次请求的任务广告列表
	 */
	private AddFansAdList mAddFansAdList;
	
	private ListView mListView;
	
	private AddFansAdListViewAdapter mLvAdapter;
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private UiHandler handler;
	
	private static class UiHandler extends Handler {
		
		private WeakReference<AddFansAdListActivity> mOuter;
		
		UiHandler(AddFansAdListActivity activity) {
			mOuter = new WeakReference<>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			AddFansAdListActivity activity = mOuter.get();
			if (activity != null) {
				switch (msg.what) {
				// 更新广告数据
				case UPDATE_LIST_DATA_SUCCESS:
					activity.handleListView2Update(msg.arg1 == 1);
					break;
				
				// 更新广告失败
				case UPDATE_LIST_DATA_FAILED:
					activity.handleRequestFailed(msg.obj == null ? "请求失败" : msg.obj.toString());
					break;
				default:
					break;
				}
			}
			super.handleMessage(msg);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fans_ad_list);
		handler = new UiHandler(this);
		
		// 初始化View
		initView();
		
		// (可选)注册积分余额变动监听-随时随地获得积分的变动情况
		PointsManager.getInstance(this).registerNotify(this);
		
		// 发起列表请求
		requestList();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// (可选)注销积分余额监听-如果在onCreate中注册了，那这里必须得注销
		PointsManager.getInstance(this).unRegisterNotify(this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		updatePoints(PointsManager.getInstance(this).queryPoints());
		mLimitItem.setVisible(false);
		return true;
	}
	
	/**
	 * 初始化View
	 */
	private void initView() {
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr_ad_list);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// 重新刷新页面
				requestList();
			}
		});
		mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff00ddff"),
				Color.parseColor("#ff99cc00"),
				Color.parseColor("#ffffbb33"),
				Color.parseColor("#ffff4444")
		);
		mSwipeRefreshLayout.setProgressViewOffset(false,
				0,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics())
		);
		mSwipeRefreshLayout.setRefreshing(true);
		
		mListView = (ListView) findViewById(R.id.lv_addata);
		mLvAdapter = new AddFansAdListViewAdapter(this, null); // 这里先让列表为空，待加载到数据再显示出来
		mListView.setAdapter(mLvAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	/**
	 * 监听积分变动，可以在这里进行更新界面显示等操作
	 */
	@Override
	public void onPointBalanceChange(float pointsBalance) {
		updatePoints(pointsBalance);
	}
	
	/**
	 * @param flag true : requesting adlist<br>
	 *             false: finish request adlist
	 */
	public void showRefreshingStatus(boolean flag) {
		if (flag) {
			mSwipeRefreshLayout.setRefreshing(true);
		} else {
			mSwipeRefreshLayout.setRefreshing(false);
			mLvAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 发起列表请求
	 */
	private void requestList() {
		
		showRefreshingStatus(true);
		
		// 下面展示了两种加载方式，开发者只需要采用一种即可
		// 请求20条广告列表
		
		// -------------------------------------------------------------------------------------------------------
		// 异步加载方式
		DiyOfferWallManager.getInstance(this)
		                            .requestAddFansAdList(1, 20, new OnRequestAddFansAdListListener() {
			
			                            /**
			                             * 当成功获取到微信加粉任务广告列表数据的时候，会回调这个方法（注意:本接口不在UI线程中执行， 所以请不要在本接口中进行UI线程方面的操作）
			                             * 注意：列表数据有可能为空（比如：没有广告的时候），开发者处理之前，请先判断列表是否为空，大小是否大与0
			                             */
			                            @Override
			                            public void onRequestAddFansAdListSuccess(
					                            AddFansAdList addFansAdList) {
				                            mAddFansAdList = addFansAdList;
				                            handleListView2Update();
			                            }
			
			                            /**
			                             * 请求成功，但是返回有米错误代码时候，会回调这个接口
			                             */
			                            @Override
			                            public void onRequestAddFansAdListFailedWithErrorCode(int errorCode) {
				                            notifyRequestFailed("请求错误，错误代码 ： %d， 请联系客服", errorCode);
				
			                            }
			
			                            /**
			                             * 因为网络问题而导致请求失败时，会回调这个接口（注意:本接口不在UI线程中执行， 所以请不要在本接口中进行UI线程方面的操作）
			                             */
			                            @Override
			                            public void onRequestAddFansAdListFailed() {
				                            notifyRequestFailed("请求失败，请检查网络");
				
			                            }
		                            });
		
		//		// 同步加载方式
		//		new Thread(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				try {
		//					mAddFansAdList = DiyOfferWallManager.getInstance(AddFansAdListActivity
		//							.this).requestAddFansAdList(1, 20);
		//					handleListView2Update();
		//				} catch (NetworkException e) {
		//					Log.e("YoumiSdk", "", e);
		//					notifyRequestFailed("请求失败，请检查网络");
		//				} catch (ErrorCodeException e) {
		//					Log.e("YoumiSdk", "", e);
		//					notifyRequestFailed("请求错误，错误代码 ： %d， 请联系客服", e.getErrCode());
		//				}
		//			}
		//		}).start();
		
	}
	
	/**
	 * 当点击列表项时，展示详细内容
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, AddFansAdDetailActivity.class);
		intent.putExtra("add_fans_ad", mLvAdapter.getItem(position));
		startActivity(intent);
	}
	
	/**
	 * 更新listview
	 */
	private void handleListView2Update() {
		if (mAddFansAdList != null && mAddFansAdList.size() > 0) {
			mLvAdapter.setData(mAddFansAdList);
			// 获取到数据之后向ui线程中handler发送更新view的信息（这里先显示文字信息，后续加载图片）
			notifyListView2Update(true);
		} else {
			mLvAdapter.setData(null);
			// 获取到数据之后向ui线程中handler发送更新view的信息
			notifyListView2Update(false);
		}
	}
	
	private void notifyListView2Update(boolean flag) {
		Message msg = handler.obtainMessage();
		msg.what = UPDATE_LIST_DATA_SUCCESS;
		if (!flag) {
			msg.arg1 = 1;
		}
		msg.sendToTarget();
	}
	
	private void notifyRequestFailed(String format, Object... args) {
		Message msg = handler.obtainMessage();
		msg.what = UPDATE_LIST_DATA_FAILED;
		msg.obj = String.format(format, args);
		msg.sendToTarget();
	}
	
	protected void handleListView2Update(boolean isListEmpty) {
		showRefreshingStatus(false);
		mLvAdapter.notifyDataSetChanged();
		if (isListEmpty) {
			Toast.makeText(this, "当前没有任务哦，晚点在来吧~", Toast.LENGTH_LONG).show();
		}
	}
	
	protected void handleRequestFailed(String message) {
		showRefreshingStatus(false);
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
}
