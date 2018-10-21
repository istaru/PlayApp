package mituo.plat.cpldemo;

import android.app.Activity;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import mituo.plat.Ads;
import mituo.plat.util.MituoUtil;
import mituo.plat.util.ResponseResult;

public class DemoCPLListActivity extends Activity implements AdapterView.OnItemClickListener {

    private final static String TAG = "DemoCPLListActivity";

    private ListView lvTasks;

    private DemoCPLItemAdapter mAdapter;

    private LinearLayout empty;

    private ProgressBar mEmptyProgress;

    private TextView mEmptyText;


    private Activity mActivity;

    protected Handler handler;

    private AsyncTask<String, Void, ResponseResult> mAsyncTask;


    private AtomicBoolean isLoader = new AtomicBoolean(false);

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
        title.setText("CPL列表");

        // back button
        ImageView btnBack = (ImageView)findViewById(R.id.demo_btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        handler = new Handler();

        Log.i(TAG, "onCreate");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Ads entry = mAdapter.getItemById(id);
        //打开 cpl详情页面
        MituoUtil.getMituoConnect(mActivity).showCPLDetail(entry);
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
            ResponseResult response = MituoUtil.getMituoConnect(mActivity).fetchCPL(); //后台线程
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
                mAdapter = (DemoCPLItemAdapter) lvTasks.getAdapter();
                if (mAdapter == null) {
                    mAdapter = new DemoCPLItemAdapter(mActivity);
                }
                lvTasks.setAdapter(mAdapter);
                mAdapter.setList(tasks);
            } else {
                setEmptyViewError(response.getMsg());
            }
            isLoader.set(false);
        }
    }

}
