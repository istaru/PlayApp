package com.experience.moon.play.params.response;

import com.experience.moon.play.activity.BaseActivity;
import com.moon.lib.request.response.BaseResDto;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Retrofit2
 * Created by Moon on 2018/6/22.
 */

public abstract class ResSubscriber<T> implements Observer<T> {

    private RefreshLayout refreshLayout;
    private BaseActivity mContext;
    private Disposable mDisposable;

    public ResSubscriber() {
    }

    /**
     * 显示黑色半透明的小loading
     *
     * @param context
     */
    public ResSubscriber(BaseActivity context) {
        this.mContext = context;
    }

    /**
     * 开始请求
     *
     * @param d 可以做到切断的操作，让Observer观察者不再接收上游事件
     */
    @Override
    public void onSubscribe(Disposable d) {
        this.mDisposable = d;
    }

    /**
     * 请求处理循环
     *
     * @param t
     */
    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    /**
     * 请求出错
     *
     * @param e
     */
    @Override
    public void onError(final Throwable e) {
        if (null != refreshLayout) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        String msg = "";
        int code = 0;
        if (e instanceof HttpException) {
            try {
                HttpException httpException = (HttpException) e;
                msg = httpException.response().errorBody().string();
                code = httpException.response().code();
//                if (httpException.code() == 504) {
//                    msg = "网络异常";
//                } else {
//                    String str = httpException.response().errorBody().string();
//                    BaseEntity baseEntity = new Gson().fromJson(new Gson().toJson(str), BaseEntity.class);
//                    msg = baseEntity.getMessage();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
                msg = "数据解析异常";
            }
        } else {
            msg = "网络异常";
        }
        onError(code,msg);
    }

    /**
     * 请求处理循环结束
     */
    @Override
    public void onComplete() {
        if (null != refreshLayout) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
    }

    protected abstract void onSuccess(T t);

    protected abstract void onError(int code, String message);
}
