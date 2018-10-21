package mituo.plat.cpldemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import mituo.plat.Ads;
import mituo.plat.CompAds;
import mituo.plat.util.MituoUtil;
import mituo.plat.util.ResponseResult;

public class DemoAppListActivity extends Activity implements AdapterView.OnItemClickListener {

    private final static String TAG = "DemoAppListActivity";

    private ListView lvTasks;

    private DemoAppItemAdapter mAdapter;

    private LinearLayout empty;

    private ProgressBar mEmptyProgress;

    private TextView mEmptyText;


    private Activity mActivity;

    protected Handler handler;

    private AsyncTask<String, Void, ResponseResult> mAsyncTask;


    private AtomicBoolean isLoader = new AtomicBoolean(false);

    private ItemBroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        setContentView(R.layout.demo_app_list_layout);

        empty = (LinearLayout) findViewById(R.id.empty);
        mEmptyProgress = (ProgressBar) findViewById(R.id.emptyProgress);
        mEmptyText = (TextView) findViewById(R.id.emptyText);

        lvTasks = (ListView) findViewById(R.id.listViewTaskList);
        lvTasks.setOnItemClickListener(this);

        TextView  title = (TextView)findViewById(R.id.demo_action_bar_title);
        title.setText("应用列表");

        // back button
        ImageView btnBack = (ImageView)findViewById(R.id.demo_btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        handler = new Handler();

        //Activity 广播有可能内存紧张被结束掉 AndroidManifest.xml DSDKReceiver 不会
        mReceiver = new ItemBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Ads.INTENT_ACTION_ADS_VERIFY_SUCCESS); //app安装 加分 成功
        mActivity.registerReceiver(mReceiver, intentFilter);

        Log.i(TAG, "onCreate");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Ads entry = mAdapter.getItemById(id);

        boolean detail = true;
        if(detail) {
            //展示更多任务详情
            Intent intent = new Intent(mActivity, DemoDetailActivity.class);
            intent.putExtra("ads", entry);
            startActivity(intent);
        }else {
            //直接 开始做任务
            MituoUtil.getMituoConnect(mActivity).download(mActivity,entry);//UI线程
        }

    }


    public void load() {
        Log.i(TAG, "load");
        if (isLoader.compareAndSet(false, true)) {
            mAsyncTask = new LoadTask();
            mAsyncTask.execute();
        }
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        if (mAsyncTask != null) {
            if (mAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                isLoader.set(false);
                Log.i(TAG, "mAsyncTask cancel:" + mAsyncTask.cancel(true));
            } else {
                Log.i(TAG, "AsyncTask.Status.FINISHED");
            }
            mAsyncTask = null;
        }

        if (mReceiver != null) {
            Log.i(TAG, "unregisterReceiver");
            mActivity.unregisterReceiver(mReceiver);
            mReceiver = null;
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
        mEmptyText.setText(Html.fromHtml(text));
    }

    public void setLoadingView() {
        empty.setVisibility(ViewGroup.VISIBLE);
        mEmptyText.setText(R.string.loading);
    }


    private class LoadTask extends AsyncTask<String, Void, ResponseResult> {


        protected ResponseResult doInBackground(String... params) {
            ResponseResult response = MituoUtil.getMituoConnect(mActivity).fetch(); //后台线程
            if (isCancelled()) {
                isLoader.set(false);
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

            if (isFinishing()||response==null)  {
                isLoader.set(false);
                Log.i(TAG, "activity isFinishing");
                return;
            }

            if (response.isok()) {
                setEmptyView();
                List<Ads> tasks = response.getTasks();
                mAdapter = (DemoAppItemAdapter) lvTasks.getAdapter();
                if (mAdapter == null) {
                    mAdapter = new DemoAppItemAdapter(mActivity);
                }
                lvTasks.setAdapter(mAdapter);
                mAdapter.setList(tasks);
            } else {
                setEmptyViewError(response.getMsg());
            }
            isLoader.set(false);
        }
    }

    private class ItemBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            Log.i(TAG, "action:" + intent.getAction() + " package:" + intent.getPackage());
            //判断 包名 是因为 有一些定制机 并不能限制在APP内传播广播 而是全局在传 只接受 自己的广播
            if (context.getPackageName().equals(intent.getPackage())&& Ads.INTENT_ACTION_ADS_VERIFY_SUCCESS.equals(intent.getAction())) {

                if (mAdapter != null) {

                    Ads ads = intent.getParcelableExtra("ads"); //状态会有变 从安装变成可签到
                    double awarded = intent.getDoubleExtra("awarded", 0); //安装所得 奖励

                    //do something
                    List<Ads> items = mAdapter.getList();
                    for (int i = 0; i < items.size(); i++) {
                        Ads item = items.get(i);
                        if (item.getId() == ads.getId()) {
                            items.remove(item); //从安装列表中 删除
                            //排序
                            Collections.sort(mAdapter.getList(), new CompAds());
                            mAdapter.notifyDataSetChanged();
                            Log.i(TAG, "onReceive mNotifyOnChange");
                            break;
                        }
                    }


                }

            } else {
                Log.e(TAG, "not equals");
            }
        }

    }

}
