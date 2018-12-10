package com.youmi.android.diyofferwall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("NewApi")
public abstract class BaseActivity extends AppCompatActivity {
	
	protected MenuItem mPointsItem, mLimitItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to theaction bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);// 获取menu目录下simple.xml的菜单文件
		mPointsItem = menu.findItem(R.id.action_points);
		mLimitItem = menu.findItem(R.id.action_limit);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_limit:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("可做任务数说明");
			builder.setMessage("1. 针对《未完成》状态的广告，每个用户每天可以做的任务有上限\n2. 获取这个上限必须在成功请求广告列表之后才可以获取\n3. 返回的结果中包含今天的安装上限以及已经安装过的数量");
			builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void updatePoints(float points) {
		if (mPointsItem != null) {
			mPointsItem.setTitle("积分：" + points);
		}
	}
	
	protected void updateLimitInfo(String msg) {
		if (mLimitItem != null) {
			mLimitItem.setTitle(msg);
		}
	}
	
}