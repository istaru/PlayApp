/**
 *
 */

package mituo.plat.cpldemo;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mituo.plat.Ads;
import mituo.plat.util.MituoUtil;


public class DemoCheckItemAdapter extends BaseAdapter {

    static class ViewHolder {
        public ImageView ivIcon;

        public TextView tvName;

        public TextView tvPromo;

        public Button tvStatus;

        public TextView tvRemian;
    }

    private Activity mActivity;

    private LayoutInflater lif;

    private List<Ads> items;

    /**
     * 构造方法
     **/
    public DemoCheckItemAdapter(Activity activity) {
        mActivity = activity;
        lif = LayoutInflater.from(mActivity);
        items = new ArrayList<Ads>();
    }


    public void add(Ads p) {
        if (p == null)
            return;
        items.add(p);
        notifyDataSetChanged();
    }


    public void setList(List<Ads> l) {
        items = l;
        notifyDataSetChanged();
    }

    public List<Ads> getList() {
        return items;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder views;
        if (convertView == null) {
            views = new ViewHolder();
            convertView = lif.inflate(R.layout.demo_app_item_check, null);
            views.ivIcon = (ImageView) convertView.findViewById(R.id.demo_imageViewIcon);
            views.tvName = (TextView) convertView.findViewById(R.id.demo_textViewName);
            views.tvPromo = (TextView) convertView.findViewById(R.id.demo_textViewPromo);
            views.tvStatus = (Button) convertView.findViewById(R.id.demo_status);
            views.tvRemian = (TextView) convertView.findViewById(R.id.demo_remain_number);
            convertView.setTag(views);
        } else {
            views = (ViewHolder) convertView.getTag();
        }

        final Ads item = items.get(position);
        views.tvName.setText(item.getName());
        views.tvPromo.setText(Html.fromHtml(item.getPromo()));

        if (!TextUtils.isEmpty(item.getIcon())) {
            Picasso.with(mActivity)
                    .load(item.getIcon())
                    .placeholder(R.drawable.demo_ic_vm_thumbnail_big_apps)
                    .error(R.drawable.demo_ic_vm_thumbnail_big_apps)
                    .into(views.ivIcon);
        } else {
            views.ivIcon.setImageResource(R.drawable.demo_ic_vm_thumbnail_big_apps);
        }

        String ready = "+" + MituoUtil.miText2(item.getAward());
        views.tvStatus.setText(ready);

        // A_STATUS_READY = 0 可安装
        // A_STATUS_WAIT_CD = 10 可体验
        // A_STATUS_READY_C = 20 可签到
        // A_STATUS_READY_CD = 25 已抢完(可签到名额)
        // A_STATUS_READY_D = 30 可深度
        // A_STATUS_READY_DR = 35 审核中
        // A_STATUS_DONE = 99 完成
        switch (item.getStatus()) {
            case Ads.A_STATUS_READY:
                views.tvRemian.setVisibility(View.GONE);

                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_dotted_line));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_grey_selector);

                views.tvStatus.setText("可安装");
                break;
            case Ads.A_STATUS_WAIT_CD:
                views.tvRemian.setVisibility(View.GONE);

                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_dotted_line));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_grey_selector);
                views.tvStatus.setText("未到时间");

                break;
            case Ads.A_STATUS_READY_C:
                views.tvRemian.setVisibility(View.VISIBLE);
                views.tvRemian.setText(item.getEcount());

                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_main_color));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_main_selector);

                views.tvStatus.setText("+" + MituoUtil.miText2(item.getAward()));
                break;
            case Ads.A_STATUS_READY_CD:
                views.tvRemian.setVisibility(View.GONE);

                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_dotted_line));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_grey_selector);
                views.tvStatus.setText("已抢完");
                break;
            case Ads.A_STATUS_READY_D:
                views.tvRemian.setVisibility(View.GONE);

                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_main_color));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_main_selector);
                views.tvStatus.setText("+" + MituoUtil.miText2(item.getAward()));

                break;
            case Ads.A_STATUS_READY_DR:
                views.tvRemian.setVisibility(View.GONE);
                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_dotted_line));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_grey_selector);
                views.tvStatus.setText("审核中");
                break;
            case Ads.A_STATUS_DONE: {
                views.tvRemian.setVisibility(View.GONE);

                views.tvStatus.setTextColor(ContextCompat.getColor(mActivity,R.color.demo_dotted_line));
                views.tvStatus.setBackgroundResource(R.drawable.demo_checkitem_grey_selector);
                views.tvStatus.setText("已完成");
                break;
            }

        }

        views.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MituoUtil.getMituoConnect(mActivity).download(mActivity, item);
            }
        });

        return convertView;
    }

    public Ads getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        Ads i = items.get(position);
        return i.getId(); // not threadsafe
    }

    public Ads getItemById(long id) {
        for (Iterator<Ads> i = items.iterator(); i.hasNext(); ) {
            Ads t = (Ads) i.next();
            if (t.getId() == id)
                return t;
        }
        return null;
    }


}
