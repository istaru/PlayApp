package com.youmi.android.diyofferwall.util;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.youmi.android.diyofferwall.R;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 * <p>
 * 重要：
 * <p>
 * 1. 下拉刷新的时候，滑动到底部的时候，不能触发上拉刷新
 * 2. 同理，上拉刷新的时候，滑动到顶部的时候，也不能触发下拉刷新
 */
public class RefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {

	/**
	 * 滑动到最下面时的上拉操作
	 */
	private int mTouchSlop;

	/**
	 * listview实例
	 */
	private ListView mListView;

	/**
	 * 上拉监听器, 到了最底部的上拉加载操作
	 */
	private OnPushRefreshListener mOnPushRefreshListener;

	/**
	 * ListView的加载中footer
	 */
	private View mListViewFooter;

	/**
	 * 按下时的y坐标
	 */
	private int mYDown;

	/**
	 * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
	 */
	private int mLastY;

	/**
	 * 是否在进行上拉加载中
	 */
	private boolean mIsPushRefreshLoading;

	public RefreshLayout(Context context) {
		this(context, null);
	}

	public RefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mListViewFooter = LayoutInflater.from(context).inflate(R.layout.lv_footer, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// 初始化ListView对象
		if (mListView == null) {
			int childs = getChildCount();
			if (childs > 0) {
				View childView = getChildAt(0);
				if (childView instanceof ListView) {
					mListView = (ListView) childView;
					// 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
					mListView.setOnScrollListener(this);
				}
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 按下
			mYDown = (int) event.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			// 移动
			mLastY = (int) event.getRawY();
			break;

		case MotionEvent.ACTION_UP:
			// 抬起的时候判断是否进行上拉刷新
			if (isTime2PushRefresh()) {
				setPushRefreshStart();
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 判断当前是否应该调用上拉刷新的回调
	 * <p>
	 * 1. 如果当前在进行下拉操作的话，那么这个时候不适合进行上拉刷新
	 * 2. 当前也没有在进行上拉加载ing的操作
	 * 3. 判断是否到达ListView底部：最后一个可见的item位置是否等于总数量
	 * 4. 抬手之前的操作是上拉的（避免在最后一个item还在显示的时候，下拉所触发的误判断）
	 *
	 * @return true：触发上拉刷新逻辑 false：不触发上拉刷新逻辑
	 */
	private boolean isTime2PushRefresh() {
		if (mListView == null || mListView.getAdapter() == null) {
			return false;
		}
		if (!isRefreshing()) {
			if (!isPushRefreshLoading()) {
				if (mListView.getAdapter().getCount() - 1 == mListView.getLastVisiblePosition()) {
					if (mYDown - mLastY >= mTouchSlop) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 设置上拉刷新开始
	 * <p>
	 * 1. 设置当前在进行上拉刷新中，避免上拉过程中又再次进入上拉刷新的逻辑中
	 * 1. 显示上拉刷新的view
	 * 2. 调用上啦刷新的逻辑
	 */
	private void setPushRefreshStart() {
		mIsPushRefreshLoading = true;

		if (mListView != null) {
			mListView.addFooterView(mListViewFooter);
		}

		if (mOnPushRefreshListener != null) {
			mOnPushRefreshListener.onPushRefresh();
		}
	}

	/**
	 * 设置上拉刷新结束
	 * <p>
	 * 1. 隐藏上啦刷新的view
	 * 2. 重置所有标记位
	 */
	public void setPushRefreshFinish() {
		mIsPushRefreshLoading = false;
		mYDown = 0;
		mLastY = 0;

		if (mListView == null) {
			return;
		}
		mListView.removeFooterView(mListViewFooter);
	}

	/**
	 * 获取当前是否在进行下拉操作中
	 *
	 * @return
	 */
	private boolean isPushRefreshLoading() {
		return mIsPushRefreshLoading;
	}

	/**
	 * 设置上拉刷新监听器
	 *
	 * @param onPushRefreshListener 上拉刷新监听器
	 */
	public void setOnPushRefreshListener(OnPushRefreshListener onPushRefreshListener) {
		mOnPushRefreshListener = onPushRefreshListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// 这里是为了实现如果listview在惯性上拉的过程中还能自动调用上拉刷新的逻辑
		if (isTime2PushRefresh()) {
			setPushRefreshStart();
		}

	}

	/**
	 * 重写设置下拉监听器的方法
	 * <p>
	 * 在回调下拉刷新的逻辑时，检查当前是否处于上拉刷新的状态中，如果是的话，那么是不应该回调下拉刷新的逻辑
	 *
	 * @param listener 下拉刷新监听器
	 */
	@Override
	public void setOnRefreshListener(OnRefreshListener listener) {
		super.setOnRefreshListener(new MyOnRefreshListener(this, listener));
	}

	class MyOnRefreshListener implements OnRefreshListener {

		OnRefreshListener mOnRefreshListener;

		SwipeRefreshLayout mSwipeRefreshLayout;

		MyOnRefreshListener(SwipeRefreshLayout swipeRefreshLayout, OnRefreshListener listener) {
			this.mSwipeRefreshLayout = swipeRefreshLayout;
			this.mOnRefreshListener = listener;
		}

		@Override
		public void onRefresh() {
			// 回调下拉刷新的逻辑之前检查是否处于上拉刷新状态中，是的话，就不执行本次下拉刷新的逻辑
			if (isPushRefreshLoading() && mSwipeRefreshLayout != null) {
				mSwipeRefreshLayout.getHandler().post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mSwipeRefreshLayout.getContext(), "当前在上拉刷新中，下拉刷新暂时不允许", Toast.LENGTH_LONG).show();
					}
				});
				return;
			}
			if (mOnRefreshListener != null) {
				mOnRefreshListener.onRefresh();
			}
		}
	}

	/**
	 * 加载更多的监听器
	 */
	public interface OnPushRefreshListener {

		/**
		 * 回调上拉监听
		 */
		void onPushRefresh();
	}
}