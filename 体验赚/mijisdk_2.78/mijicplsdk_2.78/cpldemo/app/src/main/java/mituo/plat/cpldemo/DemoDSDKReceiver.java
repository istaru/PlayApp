package mituo.plat.cpldemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import mituo.plat.Ads;
import mituo.plat.util.MituoUtil;

public class DemoDSDKReceiver extends BroadcastReceiver {

    private final static String TAG = "DemoDSDKReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Log.i(TAG, "action:" + intent.getAction() + " package:" + intent.getPackage());

        if(context.getPackageName().equals(intent.getPackage())) {

            if (Ads.INTENT_ACTION_ADS_VERIFY_SUCCESS.equals(intent.getAction())) {

                Ads ads = intent.getParcelableExtra("ads"); //状态有变化后的 对象
                double awarded = intent.getDoubleExtra("awarded", 0); //安装所得 奖励

                //do something
                Toast.makeText(context,"获得安装奖励:"+awarded,Toast.LENGTH_LONG).show();

            }else if (Ads.INTENT_ACTION_ADS_CHECK_SUCCESS.equals(intent.getAction())){
                Ads ads = intent.getParcelableExtra("ads"); //状态有变化后的 对象
                double awarded = intent.getDoubleExtra("awarded", 0); //签到所得 奖励

                //do something
                Toast.makeText(context,"获得签到奖励:"+awarded,Toast.LENGTH_LONG).show();
            }else if (Ads.INTENT_ACTION_ADS_CPLGET_SUCCESS.equals(intent.getAction())){
                Ads ads = intent.getParcelableExtra("ads"); //状态有变化后的 对象
                String awarded = MituoUtil.miText2(ads.getEaward());

                //do something
                Toast.makeText(context,"已领取总奖励:"+awarded,Toast.LENGTH_LONG).show();
            }
        }


    }
}
