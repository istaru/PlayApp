package com.youmi.android.diyofferwall;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmi.android.diyofferwall.adapter.AddFansAdGridViewAdapter;

import cde.ewd.adw.os.PointsChangeNotify;
import cde.ewd.adw.os.PointsManager;
import cde.ewd.adw.os.df.AddFansAd;
import cde.ewd.adw.os.df.DiyOfferWallManager;
import cde.ewd.adw.os.df.OnStartAddFansListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author zhitao
 * @since 2017-03-13 16:05
 */
public class AddFansAdDetailActivity extends BaseActivity implements View.OnClickListener, PointsChangeNotify {
	
	private AddFansAd mAddFansAd;
	
	private GridView mGvTaskFlowChart;
	
	private AddFansAdGridViewAdapter mAddFansAdGridViewAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fans_ad_detail);
		
		// 检查传入的Intent是否合法，不合法就直接finish
		Object obj = getIntent().getSerializableExtra("add_fans_ad");
		if (obj == null || !(obj instanceof AddFansAd)) {
			this.finish();
			return;
		}
		mAddFansAd = (AddFansAd) obj;
		
		// 初始化View
		initView();
		
		// （可选）注册积分余额变动监听-随时随地获得积分的变动情况
		PointsManager.getInstance(this).registerNotify(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// （可选）注销积分余额监听-如果在onCreate中注册了，那这里必须得注销
		PointsManager.getInstance(this).unRegisterNotify(this);
	}
	
	private void initView() {
		ImageView ivIcon = findViewById(R.id.iv_add_fans_detail_page_icon);
		Glide.with(AddFansAdDetailActivity.this).load(mAddFansAd.getIconUrl()).into(ivIcon);
		
		TextView tvName = findViewById(R.id.tv_add_fans_detail_page_name);
		tvName.setText(mAddFansAd.getName());
		
		TextView tvPoints = findViewById(R.id.tv_add_fans_detail_page_points);
		String points = String.format(Locale.getDefault(),
				"+ %.1f %s",
				mAddFansAd.getPoints(),
				mAddFansAd.getPointsUnit()
		);
		tvPoints.setText(points);
		
		TextView tvStatus = findViewById(R.id.tv_add_fans_detail_page_status);
		String status;
		if (mAddFansAd.isFollow()) {
			status = "已关注";
		} else {
			if (mAddFansAd.isInProgress()) {
				status = "任务在进行中";
			} else {
				status = "任务未开始";
			}
		}
		tvStatus.setText(status);
		
		Button btnStartTask = findViewById(R.id.btn_add_fans_detail_page_start_task);
		btnStartTask.setOnClickListener(this);
		btnStartTask.setText("开始任务");
		
		TextView tvTargetId = findViewById(R.id.tv_add_fans_detail_page_target_id);
		tvTargetId.setText(mAddFansAd.getTargetId());
		
		TextView tvReplyContent = findViewById(R.id.tv_add_fans_detail_page_reply_content);
		tvReplyContent.setText(mAddFansAd.getReplyContent());
		
		mGvTaskFlowChart = findViewById(R.id.add_fans_detail_page_gridView);
		mAddFansAdGridViewAdapter = new AddFansAdGridViewAdapter(AddFansAdDetailActivity.this, null);
		mGvTaskFlowChart.setAdapter(mAddFansAdGridViewAdapter);
		
		TextView tvStep2Tips = findViewById(R.id.tv_add_fans_ad_tips_step_2);
		TextView tvStep3Tips = findViewById(R.id.tv_add_fans_ad_tips_step_3);
		TextView tvStep4Tips = findViewById(R.id.tv_add_fans_ad_tips_step_4);
		
		List<Object> taskFlowChartObjectList = new ArrayList<>();
		
		// 重要：必须根据广告类型设置不同的任务流程提示语与任务流程图，另外，不得硬编码 getTargetId() 以及 getReplyContent() 方法返回的内容到应用中，否则会影响广告的转化。
		tvStep4Tips.setText(mAddFansAd.getTaskStepTips());
		
		// 获取任务流程图列表，如果任务流程图列表为空，就使用本地的图片，否则加载线上的图片。
		List<AddFansAd.TaskFlowChart> taskFlowChartList = mAddFansAd.getTaskFlowChartList();
		
		switch (mAddFansAd.getType()) {
		// 订阅微信公众号类型。
		case AddFansAd.TYPE_SUBSCRIBE_WE_CHAT_PUBLIC_NUMBER:
			// 设置任务流程提示语。
			tvStep2Tips.setText("长按复制以下微信公众号，粘贴到微信公众号添加界面，搜索并关注");
			tvStep3Tips.setText("再次长按复制以下关键字，发送给关注的微信公众号");
			// 设置本地任务流程图。
			taskFlowChartObjectList.add(R.drawable.subscribe_we_chat_public_number_step_1);
			taskFlowChartObjectList.add(R.drawable.subscribe_we_chat_public_number_step_2);
			taskFlowChartObjectList.add(R.drawable.subscribe_we_chat_public_number_step_3);
			taskFlowChartObjectList.add(R.drawable.subscribe_we_chat_public_number_step_4);
			taskFlowChartObjectList.add(R.drawable.subscribe_we_chat_public_number_step_5);
			break;
		// 打开微信小程序类型。
		case AddFansAd.TYPE_OPEN_WE_CHAT_MINI_PROGRAM:
			// 设置任务流程提示语。
			tvStep2Tips.setText("长按复制以下小程序名称，粘贴到微信小程序添加界面，搜索并打开");
			tvStep3Tips.setText("再次长按复制以下关键字，发送给打开的小程序的兑奖入口或客服入口");
			// 设置本地任务流程图。
			taskFlowChartObjectList.add(R.drawable.open_we_chat_mini_program_step_1);
			taskFlowChartObjectList.add(R.drawable.open_we_chat_mini_program_step_2);
			taskFlowChartObjectList.add(R.drawable.open_we_chat_mini_program_step_3);
			taskFlowChartObjectList.add(R.drawable.open_we_chat_mini_program_step_4);
			taskFlowChartObjectList.add(R.drawable.open_we_chat_mini_program_step_5);
			break;
		// 添加微信个人号类型。
		case AddFansAd.TYPE_ADD_WE_CHAT_PERSONAL_ACCOUNT:
			// 设置任务流程提示语。
			tvStep2Tips.setText("长按复制以下微信号，粘贴到好友添加界面，搜索并添加");
			tvStep3Tips.setText("再次长按复制以下关键字，发送给添加的好友");
			// 设置本地任务流程图。
			taskFlowChartObjectList.add(R.drawable.add_we_chat_personal_account_step_1);
			taskFlowChartObjectList.add(R.drawable.add_we_chat_personal_account_step_2);
			taskFlowChartObjectList.add(R.drawable.add_we_chat_personal_account_step_3);
			taskFlowChartObjectList.add(R.drawable.add_we_chat_personal_account_step_4);
			taskFlowChartObjectList.add(R.drawable.add_we_chat_personal_account_step_5);
			break;
		default:
			break;
		}
		
		// 加载线上的任务流程图。
		if (taskFlowChartList != null) {
			for (AddFansAd.TaskFlowChart taskFlowChart : taskFlowChartList) {
				if (taskFlowChart == null) {
					continue;
				}
				// 流程图展示的位置，从 0 开始。
				int position = taskFlowChart.getPosition();
				String url = taskFlowChart.getUrl();
				
				// 先移除本地的流程图，再添加线上的流程图。
				taskFlowChartObjectList.remove(position);
				taskFlowChartObjectList.add(position, url);
			}
		}
		
		showTaskFlowCharts(taskFlowChartObjectList);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		updatePoints(PointsManager.getInstance(this).queryPoints());
		mLimitItem.setVisible(false);
		return true;
	}
	
	@Override
	public void onPointBalanceChange(float points) {
		updatePoints(points);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_fans_detail_page_start_task:
			
			// 方式一：不带监听
			// DiyOfferWallManager.getInstance(this).startAddFans(mAddFansAd);
			
			// 方式二：带监听
			DiyOfferWallManager.getInstance(this)
			                            .startAddFans(mAddFansAd, new OnStartAddFansListener() {
				
				                            @Override
				                            public void onStartAddFansSuccess() {
					
				                            }
				
				                            @Override
				                            public void onStartAddFansFailed(int errorCode) {
					                            String message = "";
					                            switch (errorCode) {
					                            case OnStartAddFansListener.CODE_DATA_INVALID:
						                            message = "传入的数据无效";
						                            break;
					                            case OnStartAddFansListener.CODE_WE_CHAT_APPLICATION_NOT_INSTALLED:
						                            message = "微信还没有安装，建议引导用户去安装";
						                            break;
					                            case OnStartAddFansListener.CODE_CAN_NOT_GET_WE_CHAT_INTENT:
						                            message = "获取打开微信的 Intent 失败";
						                            break;
					                            case OnStartAddFansListener.CODE_CAN_NOT_START_WE_CHAT_APPLICATION:
						                            message = "打开微信失败";
						                            break;
					                            default:
						                            break;
					                            }
					                            Log.i("youmi",
							                            String.format(Locale.getDefault(),
									                            "错误代码: %d 错误描述: %s",
									                            errorCode,
									                            message
							                            )
					                            );
				                            }
			                            });
			
			break;
		default:
			break;
		}
	}
	
	/**
	 * 显示任务流程图。
	 */
	private void showTaskFlowCharts(List<Object> taskFlowChartObjectList) {
		if (taskFlowChartObjectList != null && !taskFlowChartObjectList.isEmpty()) {
			int width = getResources().getDimensionPixelSize(R.dimen.iv_task_flow_chart_width);
			LinearLayout.LayoutParams params =
					new LinearLayout.LayoutParams(taskFlowChartObjectList.size() * width, LinearLayout.LayoutParams
							.WRAP_CONTENT);
			
			mGvTaskFlowChart.setLayoutParams(params);
			mGvTaskFlowChart.setColumnWidth(width);
			mGvTaskFlowChart.setHorizontalSpacing(6);
			mGvTaskFlowChart.setStretchMode(GridView.NO_STRETCH);
			mGvTaskFlowChart.setNumColumns(taskFlowChartObjectList.size());
			mGvTaskFlowChart.setVisibility(View.VISIBLE);
			
			mAddFansAdGridViewAdapter.setData(taskFlowChartObjectList);
			mAddFansAdGridViewAdapter.notifyDataSetChanged();
		} else {
			mGvTaskFlowChart.setVisibility(View.GONE);
		}
	}
	
}
