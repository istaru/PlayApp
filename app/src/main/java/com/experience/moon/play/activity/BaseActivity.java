package com.experience.moon.play.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.moon.lib.view.BlackStatusBar;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public AppCompatActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getLayoutId());
        ButterKnife.bind(mContext);
        initView();
        setTransparentStatusBar(0, null);
        setStatusBarImgColor(false);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();


    /**
     * 设置状态栏的透明度
     *
     * @param statusBarAlpha 0-250
     */
    protected void setTransparentStatusBar(int statusBarAlpha, View view) {
        StatusBarUtil.setTranslucentForImageView(mContext, statusBarAlpha, view);
    }

    /**
     * 设置状态栏图标的颜色
     *
     * @param b true为黑色，false为白色
     */
    protected void setStatusBarImgColor(boolean b) {
        if (b) {
            BlackStatusBar.setStatusBarDarkMode(mContext);
        } else {
//            BlackStatusBar.clearStatusBarDarkMode(context)
        }
    }
}
