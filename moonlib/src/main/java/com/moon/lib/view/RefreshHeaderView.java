package com.moon.lib.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moon.lib.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 自定义定义SmartRefresh的Header
 * Created by superMoon on 2018/10/22.
 */

public class RefreshHeaderView extends LinearLayout implements RefreshHeader {

    private AnimationDrawable animationDrawable;
    private ImageView imageView;
    private LinearLayout headLayout;

    public RefreshHeaderView(Context context) {
        super(context);
        headLayout = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        headLayout.setLayoutParams(params);
        headLayout.setOrientation(LinearLayout.VERTICAL);
//        headLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_q));
        imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.img_refresh_anim);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        headLayout.addView(imageView);
    }

    @NonNull
    @Override
    public View getView() {
        return headLayout;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        Log.e("onMoving", "percent:" + percent + "_offset" + offset + "_height" + height + "_maxDragHeight" + maxDragHeight);
        float scale = 1.0f * offset / height;
        scale = Math.min(scale, 1.0f);
        Log.e("scare", scale + "");
        imageView.setPivotY(height);
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);//回到动画的初始位置
        }
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }
}
