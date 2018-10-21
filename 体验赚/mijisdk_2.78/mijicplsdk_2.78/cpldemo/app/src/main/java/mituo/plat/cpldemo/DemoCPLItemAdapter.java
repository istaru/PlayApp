/**
 * 
 */

package mituo.plat.cpldemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mituo.plat.Ads;
import mituo.plat.util.MituoUtil;

public class DemoCPLItemAdapter extends BaseAdapter {

    static class ViewHolder {
        public ImageView ivIcon;

        public TextView tvName;

        public HtmlTextView tvDownCount;

        public HtmlTextView tvSize;

        public HtmlTextView tvPromo;

        public TextView tvStatus;

        public TextView tvTag;

    }

    private Activity context;

    private LayoutInflater lif;

    private List<Ads> items;

    /** 构造方法 **/
    public DemoCPLItemAdapter(Activity cont) {
        context = cont;
        lif = LayoutInflater.from(context);
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

    //static int tag = 0;

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder views;
        if (convertView == null) {
            views = new ViewHolder();
            convertView = lif.inflate(R.layout.demo_cpl_item_new, null);
            views.ivIcon = (ImageView)convertView.findViewById(R.id.demo_imageViewIcon);
            views.tvName = (TextView)convertView.findViewById(R.id.demo_textViewName);
            views.tvDownCount =  (HtmlTextView)convertView.findViewById(R.id.demo_app_down);
            views.tvSize =  (HtmlTextView)convertView.findViewById(R.id.demo_app_size);
            views.tvPromo = (HtmlTextView)convertView.findViewById(R.id.demo_textViewPromo);
            views.tvStatus = (TextView)convertView.findViewById(R.id.demo_status);
            views.tvTag = (TextView)convertView.findViewById(R.id.demo_tag);
            convertView.setTag(views);
        } else {
            views = (ViewHolder)convertView.getTag();
        }

        final Ads item = items.get(position);
        views.tvName.setText(item.getName());
        views.tvPromo.setHtml(item.getPromo(),new HtmlHttpImageGetter(views.tvPromo));

        if(!TextUtils.isEmpty(item.getIcon())) {
            Picasso.with(context)
                    .load(item.getIcon())
                    .fit()
                    .placeholder(R.drawable.demo_ic_vm_thumbnail_big_apps)
                    .error(R.drawable.demo_ic_vm_thumbnail_big_apps)
                    .into(views.ivIcon);
        }else {
            views.ivIcon.setImageResource(R.drawable.demo_ic_vm_thumbnail_big_apps);
        }

        views.tvDownCount.setHtml(item.getDownloadCount(),new HtmlHttpImageGetter(views.tvDownCount));
        views.tvSize.setHtml(item.getSize(),new HtmlHttpImageGetter(views.tvSize));

         // cpl 标签状态
         // 0 正常 1 参与中（用户正参与的游戏）
         // 2 已结束（活动结束奖励发放阶段） 3 新一期（游戏上线前五天）

        int tag = item.getTag();

        views.tvTag.setVisibility(View.VISIBLE);


        String colorString = "";

        if(tag==1) {
            views.tvTag.setText("参与中");
            colorString = "#FF4E8542";
        }else if(tag==2) {
            views.tvTag.setText("已结束");
            colorString = "#FF626262";
        }else if(tag==3) {
            views.tvTag.setText("新一期");
            colorString = "#FFff2c4c";
        }else {
            views.tvTag.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(colorString)) {
            int parse = Color.parseColor(colorString);
            GradientDrawable gly = new GradientDrawable();
            gly.setShape(GradientDrawable.RECTANGLE);
            gly.setColor(Color.parseColor("#FFFFFF"));
            gly.setStroke(MituoUtil.dipToPixels(context,1),parse);
            gly.setCornerRadius(MituoUtil.dipToPixels(context,5));
            views.tvTag.setTextColor(parse);
            MituoUtil.setBackground(views.tvTag,gly);
        }

        String ready = "+" + MituoUtil.miText3(item.getTotalAward());
        views.tvStatus.setText(ready);

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
