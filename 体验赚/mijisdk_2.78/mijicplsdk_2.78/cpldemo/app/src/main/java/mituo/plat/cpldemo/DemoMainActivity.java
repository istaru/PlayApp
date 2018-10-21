package mituo.plat.cpldemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import mituo.plat.MituoConnect;
import mituo.plat.MituoConstants;
import mituo.plat.MituoNotifier;
import mituo.plat.MituoSpendPointsNotifier;
import mituo.plat.util.CheckResult;
import mituo.plat.util.MituoUtil;


public class DemoMainActivity extends FragmentActivity implements MituoNotifier,MituoSpendPointsNotifier{

    private final static String TAG = "DemoMainActivity";

    private TextView pointsTextView;

    int point_total;

    String currency_name;

    String displayText = "";

    boolean update_text = false;

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_main);

        try {
            JSONObject j = new JSONObject();
            j.put("uidd", "1270368");
            //设置用户信息 如果服务器对接 会需要
            MituoConnect.setUdata(this,j.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //初始化sdk
        MituoUtil.getMituoConnect(this);

        pointsTextView  = (TextView)findViewById(R.id.points_textView);
        TextView version = (TextView)findViewById(R.id.sdk_version);
        version.setText("CPLSDK version: " + MituoConstants.MP_LIBRARY_VERSION_NUMBER);
    }

    public void doAction(View view) {

        int id = view.getId();
        if(R.id.cplsdk_app_list==id) {
            Intent intent = new Intent(this, DemoAppListActivity.class);
            startActivity(intent);
        }else if(R.id.cplsdk_check_list==id) {
            Intent intent = new Intent(this, DemoCheckListActivity.class);
            startActivity(intent);
        }else if(R.id.cplsdk_cpl_list==id) {
            Intent intent = new Intent(this, DemoCPLListActivity.class);
            startActivity(intent);
        }else if(R.id.cplsdk_my_cpl==id) {
            Intent intent = new Intent(this, DemoCPLMeActivity.class);
            startActivity(intent);
        }else if(R.id.cplsdk_open_cpl==id) {
            MituoUtil.getMituoConnect(this).showCPL();
        }else if (R.id.get_points==id) {
            MituoUtil.getMituoConnect(this).getPoints(this);
        } else if (R.id.spend_points==id) {
            MituoUtil.getMituoConnect(this).spendPoints(5, this);
        }else if (id == R.id.spam_check) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    CheckResult checkResult =  MituoUtil.getMituoConnect(DemoMainActivity.this).check();//后台线程

                    if(checkResult.isIsok()) {//检查成功
                        //检查结果 0 高危设备 1正常设备 2 设备存在异常 建议观察
                        int check = checkResult.getCheck();
                        if(check==0) {
                            displayText = "高危设备 "+checkResult.getMsg();
                        }else if(check==1) {
                            displayText = "正常设备";
                        }else if(check==2) {
                            displayText = "设备存在异常 建议观察";
                        }
                    }else {//出现异常 结果不可用
                        displayText = checkResult.getMsg();
                    }

                    update_text = true;
                    // We must use a handler since we cannot update UI elements from a
                    // different thread.
                    mHandler.post(mUpdateResults);
                }
            }).start();


        }

    }


    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    private void updateResultsInUi() {
        // Back in the UI thread -- update our UI elements based on the data in
        // mResults
        if (pointsTextView != null) {
            // Update the display text.
            if (update_text) {
                pointsTextView.setText(Html.fromHtml(displayText));
                update_text = false;
            }
        }
    }

    // ================================================================================
    // CALLBACK Methods
    // ================================================================================

    // This method must be implemented if using the MituoConnect.getPoints()
    // method.
    // It is the callback method which contains the currency and points data.
    @Override
    public void getUpdatePoints(String currencyName, int pointTotal) {
        Log.i(TAG, "currencyName: " + currencyName);
        Log.i(TAG, "pointTotal: " + pointTotal);

        currency_name = currencyName;
        point_total = pointTotal;

        update_text = true;

        displayText = currencyName + ": " + pointTotal;

        // We must use a handler since we cannot update UI elements from a
        // different thread.
        mHandler.post(mUpdateResults);
    }

    // This method must be implemented if using the MituoConnect.getPoints()
    // method.
    // It is the callback method which contains the currency and points data.
    @Override
    public void getUpdatePointsFailed(String error) {
        Log.i(TAG, "getPoints error: " + error);

        update_text = true;
        //displayText = "Unable to retrieve points from server.";
        displayText = error;
        // We must use a handler since we cannot update UI elements from a
        // different thread.
        mHandler.post(mUpdateResults);
    }

    // Notifier for when spending virtual currency succeeds.
    @Override
    public void getSpendPointsResponse(String currencyName, int pointTotal) {
        Log.i(TAG, "currencyName: " + currencyName);
        Log.i(TAG, "pointTotal: " + pointTotal);

        update_text = true;
        displayText = currencyName + ": " + pointTotal;

        // We must use a handler since we cannot update UI elements from a
        // different thread.
        mHandler.post(mUpdateResults);
    }

    // Notifier for when spending virtual currency fails.
    @Override
    public void getSpendPointsResponseFailed(String error) {
        Log.i(TAG, "spendPoints error: " + error);

        update_text = true;
        displayText = "Spend Points: " + error;

        // We must use a handler since we cannot update UI elements from a
        // different thread.
        mHandler.post(mUpdateResults);
    }

}
