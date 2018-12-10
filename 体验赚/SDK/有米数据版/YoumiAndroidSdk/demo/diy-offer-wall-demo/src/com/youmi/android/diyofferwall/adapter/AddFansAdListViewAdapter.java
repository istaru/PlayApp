package com.youmi.android.diyofferwall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmi.android.diyofferwall.R;

import cde.ewd.adw.os.df.AddFansAd;
import cde.ewd.adw.os.df.AddFansAdList;

import java.util.Locale;

/**
 * @author youmi
 * @since 2017-03-13 14:48
 */
public class AddFansAdListViewAdapter extends BaseAdapter {
	
	private Context mContext;
	
	private AddFansAdList mAddFansAdList;
	
	public AddFansAdListViewAdapter(Context context, AddFansAdList addFansAdList) {
		this.mContext = context;
		this.mAddFansAdList = addFansAdList;
	}
	
	public void setData(AddFansAdList addFansAdList) {
		mAddFansAdList = addFansAdList;
	}
	
	public AddFansAdList getData() {
		return mAddFansAdList;
	}
	
	@Override
	public int getCount() {
		return mAddFansAdList == null ? 0 : mAddFansAdList.size();
	}
	
	@Override
	public AddFansAd getItem(int position) {
		return mAddFansAdList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_item_add_fans_ad_task, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.lvitem_iv_wechat_add_fens_icon);
			holder.name = (TextView) convertView.findViewById(R.id.lvitem_tv_wechat_add_fens_name);
			holder.adText = (TextView) convertView.findViewById(R.id.lvitem_tv_wechat_add_fens_ad_text);
			holder.rewardCounts = (TextView) convertView.findViewById(R.id.lvitem_tv_wechat_add_fens_reward_counts);
			holder.status = (TextView) convertView.findViewById(R.id.lvitem_tv_wechat_add_fens_status);
			holder.points = (TextView) convertView.findViewById(R.id.lvitem_tv_wechat_add_fens_points);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		AddFansAd addFansAd = getItem(position);
		
		// 设置广告图标
		if (addFansAd.getIconUrl() == null) {
			holder.icon.setVisibility(View.INVISIBLE);
		} else {
			Glide.with(mContext).load(addFansAd.getIconUrl()).into(holder.icon);
			holder.icon.setVisibility(View.VISIBLE);
		}
		
		// 设置广告名字
		holder.name.setText(addFansAd.getName());
		
		// 设置广告语
		holder.adText.setText(addFansAd.getDescription());
		
		// 设置今天获取到用户奖励的人数
		holder.rewardCounts.setText(String.format(Locale.getDefault(), "今天已有 %d 人获奖", addFansAd.getRewardsCount()));
		
		// 设置任务状态
		String status;
		if (addFansAd.isFollow()) {
			status = "已关注";
		} else {
			if (addFansAd.isInProgress()) {
				status = "任务在进行中";
			} else {
				status = "任务未开始";
			}
		}
		holder.status.setText(status);
		
		// 设置任务积分
		String points = String.format(Locale.getDefault(),
				"+ %.1f %s",
				addFansAd.getPoints(),
				addFansAd.getPointsUnit()
		);
		holder.points.setText(points);
		
		return convertView;
	}
	
	private static class ViewHolder {
		
		ImageView icon;
		
		TextView name;
		
		TextView adText;
		
		TextView rewardCounts;
		
		TextView status;
		
		TextView points;
		
	}
}
