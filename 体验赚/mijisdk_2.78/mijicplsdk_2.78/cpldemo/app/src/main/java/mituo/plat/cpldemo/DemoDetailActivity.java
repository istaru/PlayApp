package mituo.plat.cpldemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;

import mituo.plat.Ads;
import mituo.plat.util.MituoUtil;
import mituo.plat.util.ResponseResult;

public class DemoDetailActivity extends Activity {

    private final static String TAG = "DemoDetailActivity";

    private Ads ads;

    private LinearLayout empty;

    private ProgressBar mEmptyProgress;

    private TextView mEmptyText;

    private Activity mActivity;

    protected Handler handler;

    private AsyncTask<String, Void, ResponseResult> mAsyncTask;

    private boolean isLoader = false;

    private TextView detail;

    private BroadcastReceiver mReceiver =new  BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "action:" + intent.getAction() + " package:" + intent.getPackage());

            if(context.getPackageName().equals(intent.getPackage())) {

                if (Ads.INTENT_ACTION_ADS_DOWNLOAD_PROGRESS.equals(intent.getAction())) {
                    try {

                        long id = intent.getLongExtra("id",0);

                        if(ads.getId()==id) {

                            String name = intent.getStringExtra("name");
                            String packageName = intent.getStringExtra("packageName");
                            long totalBytes = intent.getLongExtra("totalBytes",0);
                            long currentBytes = intent.getLongExtra("currentBytes",0);

                            String percent = "0%";
                            int progress = 0;
                            if (totalBytes >0) {
                                if(totalBytes==currentBytes) {
                                    Log.d(TAG, "btnActionOne.setProgress(0)");
                                    Log.d(TAG, "btnActionOne.setText(立即安装)");
                                }else {
                                    progress = (int)((currentBytes * 100) / totalBytes);
                                    percent = NumberFormat.getPercentInstance().format((double) currentBytes / totalBytes);
                                    progress = MituoUtil.percentInt(percent, progress);
                                    Log.d(TAG, "progress:"+progress +" percent:"+percent);

                                    Log.d(TAG, "btnActionOne.setLoadingText("+percent+")");
                                    Log.d(TAG, "btnActionOne.setProgress("+progress+")");
                                }
                            }else {
                                Log.d(TAG, "btnActionOne.setLoadingText("+percent+")");
                                Log.d(TAG, "btnActionOne.setProgress("+progress+")");
                            }

                        }else {
                            Log.d(TAG,"packageName not equals");
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                }else if(Ads.INTENT_ACTION_ADS_DOWNLOAD_STATUS.equals(intent.getAction())) {
                    //只会下载结束发此广播
                    try {

                        long id = intent.getLongExtra("id",0);

                        Log.d(TAG, "btnActionOne.setText(立即安装)");

                        if(ads.getId()==id) {
                            //status 1 正常结束  -1 出现异常
                            int  status = intent.getIntExtra("status",0);
                            Log.d(TAG, "status:"+status);
                        }else {
                            Log.d(TAG,"packageName not equals");
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        setContentView(R.layout.demo_app_detail);

        empty = (LinearLayout) findViewById(R.id.empty);
        mEmptyProgress = (ProgressBar) findViewById(R.id.emptyProgress);
        mEmptyText = (TextView) findViewById(R.id.emptyText);

        detail =  (TextView) findViewById(R.id.demo_detail);
        TextView title = (TextView) findViewById(R.id.demo_action_bar_title);
        title.setText("应用详情");

        // back button
        ImageView btnBack = (ImageView) findViewById(R.id.demo_btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        handler = new Handler();

        ads = getIntent().getParcelableExtra("ads");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Ads.INTENT_ACTION_ADS_DOWNLOAD_PROGRESS);
        intentFilter.addAction(Ads.INTENT_ACTION_ADS_DOWNLOAD_STATUS);

        registerReceiver(mReceiver, intentFilter);

        Log.i(TAG, "onCreate");

    }

    @SuppressWarnings("ResourceType")
    private void init() {
        Log.i(TAG, "init");
        if (!ads.isLoader()) {
            Log.i(TAG, "init is not isLoader");
            return;
        }

        detail.setText("应用详情加载成功");

    }

    public void doAction(View view) {
        MituoUtil.getMituoConnect(mActivity).download(mActivity,ads);
    }

    public void load() {
        Log.i(TAG, "load");
        if (!isLoader && !ads.isLoader()) {
            isLoader = true;
            mAsyncTask = new LoadTask(ads);
            mAsyncTask.execute();
        }

    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        if (mAsyncTask != null) {
            if (mAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                isLoader = false;
                Log.i(TAG, "mAsyncTask cancel:" + mAsyncTask.cancel(true));
            } else {
                Log.i(TAG, "AsyncTask.Status.FINISHED");
            }
            mAsyncTask = null;
        }

        if(mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }

        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setEmptyView() {
        empty.setVisibility(ViewGroup.GONE);
        mEmptyText.setText("");
    }

    public void setEmptyViewError(String text) {
        mEmptyProgress.setVisibility(ViewGroup.GONE);
        mEmptyText.setText(text);
    }

    public void setLoadingView() {
        empty.setVisibility(ViewGroup.VISIBLE);
        mEmptyText.setText(R.string.loading);
    }


    private class LoadTask extends AsyncTask<String, Void, ResponseResult> {


        private Ads ads;

        public LoadTask(Ads a) {
            ads = a;
        }

        protected ResponseResult doInBackground(String... params) {
            ResponseResult response = MituoUtil.getMituoConnect(mActivity).appDetail(ads); //后台线程
            if (isCancelled()) {
                isLoader = false;
                Log.i(TAG, "isCancelled");
                return null;
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setLoadingView();
        }

        protected void onPostExecute(ResponseResult response) {

            if (isFinishing()||response==null) {
                isLoader = false;
                Log.i(TAG, "activity isFinishing");
                return;
            }

            if (response.isok()) {
                setEmptyView();
                ads = response.getAds();
                init();
            } else {
                setEmptyViewError(response.getMsg());
            }
            isLoader = false;
        }
    }

}
