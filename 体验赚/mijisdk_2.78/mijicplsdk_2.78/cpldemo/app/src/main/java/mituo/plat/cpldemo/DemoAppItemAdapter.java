/**
 * 
 */

package mituo.plat.cpldemo;

import android.app.Activity;
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


public class DemoAppItemAdapter extends BaseAdapter {

    static class ViewHolder {
        public ImageView ivIcon;

        public TextView tvName;

        public TextView tvDownCount;

        public TextView tvSize;

        public TextView tvPromo;

        public Button tvStatus;

        public TextView tvWStatus;

    }

    private Activity mActivity;

    private LayoutInflater lif;

    private List<Ads> items;

    /** 构造方法 **/
    public DemoAppItemAdapter(Activity activity) {
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
            convertView = lif.inflate(R.layout.demo_app_item_new, null);
            views.ivIcon = (ImageView)convertView.findViewById(R.id.demo_imageViewIcon);
            views.tvName = (TextView)convertView.findViewById(R.id.demo_textViewName);
            views.tvDownCount =  (TextView)convertView.findViewById(R.id.demo_app_down);
            views.tvSize =  (TextView)convertView.findViewById(R.id.demo_app_size);
            views.tvPromo = (TextView)convertView.findViewById(R.id.demo_textViewPromo);
            views.tvStatus = (Button)convertView.findViewById(R.id.demo_status);
            convertView.setTag(views);
        } else {
            views = (ViewHolder)convertView.getTag();
        }

        final Ads item = items.get(position);
        views.tvName.setText(item.getName());
        views.tvPromo.setText(Html.fromHtml(item.getPromo()));

        if(!TextUtils.isEmpty(item.getIcon())) {
            Picasso.with(mActivity)
                    .load(item.getIcon())
                    .placeholder(R.drawable.demo_ic_vm_thumbnail_big_apps)
                    .error(R.drawable.demo_ic_vm_thumbnail_big_apps)
                    .into(views.ivIcon);
        }else {
            views.ivIcon.setImageResource(R.drawable.demo_ic_vm_thumbnail_big_apps);
        }

        views.tvDownCount.setText(Html.fromHtml(item.getDownloadCount()));
        views.tvSize.setText(Html.fromHtml(item.getSize()));

        views.tvStatus.setText(item.getAppAward());

        views.tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MituoUtil.getMituoConnect(mActivity).download(mActivity,item);
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
        for (Iterator<Ads> i = items.iterator(); i.hasNext();) {
            Ads t = (Ads)i.next();
            if (t.getId() == id)
                return t;
        }
        return null;
    }


}
