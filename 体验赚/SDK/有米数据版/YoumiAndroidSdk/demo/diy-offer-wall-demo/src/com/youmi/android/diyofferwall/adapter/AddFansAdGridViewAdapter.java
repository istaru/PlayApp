package com.youmi.android.diyofferwall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youmi.android.diyofferwall.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情页的截图显示
 *
 * @author youmi
 * @date 2015-4-24 上午9:27:10
 */
public class AddFansAdGridViewAdapter extends BaseAdapter {
	
	private Context mContext;
	
	private List<Object> mTaskFlowChartList;
	
	public AddFansAdGridViewAdapter(Context context, ArrayList<Object> taskFlowChartList) {
		mContext = context;
		mTaskFlowChartList = taskFlowChartList;
	}
	
	public void setData(List<Object> taskFlowChartList) {
		mTaskFlowChartList = taskFlowChartList;
	}
	
	@Override
	public int getCount() {
		return mTaskFlowChartList == null ? 0 : mTaskFlowChartList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mTaskFlowChartList == null ? null : mTaskFlowChartList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.gv_item_add_fans, null);
			viewHolder.screenShot = convertView.findViewById(R.id.gv_item_add_fans_steps);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Glide.with(mContext).load(getItem(position)).into(viewHolder.screenShot);
		
		return convertView;
	}
	
	private static class ViewHolder {
		
		ImageView screenShot;
	}
}
