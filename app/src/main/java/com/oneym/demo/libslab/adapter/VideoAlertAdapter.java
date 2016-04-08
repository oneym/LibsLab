package com.oneym.demo.libslab.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.oneym.demo.libslab.R;
import com.oneym.demo.libslab.Utils.SQLiteHelper;
import com.oneym.demo.libslab.widget.ListItemDelete;
import com.oneym.libslab.temp.VideoAlertBean;
import com.oneym.libslab.utils.Constants;
import com.oneym.libslab.utils.common.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 右滑删除适配类
 *
 * @author oneym oneym@sina.cn
 * @since 20151127160701
 */
public class VideoAlertAdapter extends BaseAdapter {

    private List<VideoAlertBean> lists = null;
    private LayoutInflater mInflater = null;
    private Handler mHandler = null;
    private Context context = null;

    public VideoAlertAdapter(Context context, Handler mHandler) {
        this.context = context;
        lists = new ArrayList<VideoAlertBean>();
        mInflater = LayoutInflater.from(this.context);
        this.mHandler = mHandler;
    }

    public void setData(List<VideoAlertBean> lists) {
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_content_alert, null, false);
            holder = new Holder();
            holder.tv = (TextView) convertView.findViewById(R.id.list_txt);
            holder.tbtn = (ToggleButton) convertView.findViewById(R.id.list_toggle_btn);
            holder.delbtn = (Button) convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (!lists.isEmpty()) {
            final VideoAlertBean bean = lists.get(position);
            holder.tv.setText(bean.getText());
            holder.tbtn.setChecked(bean.isChecked().equals("true"));

            holder.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.out("position=" + position);

                    VideoAlertBean bean = lists.get(position);
                    SQLiteHelper.getInstance().deleteById_alert(bean.getId());
                    mHandler.sendEmptyMessage(Constants.REFRESH);
                    Toast.makeText(context, "[" + bean.getText() + "] 的提醒已删除", Toast.LENGTH_LONG).show();
                }
            });

            holder.tbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteHelper.getInstance().update_alert(bean.getId(),
                            bean.getTime(),
                            bean.getWeek(),
                            bean.isChecked().equals("true") ? "false" : "true");
                    mHandler.sendEmptyMessage(Constants.REFRESH);
                }
            });

        }
        return convertView;
    }

    private class Holder {
        TextView tv;
        ToggleButton tbtn;
        Button delbtn;
    }
}
