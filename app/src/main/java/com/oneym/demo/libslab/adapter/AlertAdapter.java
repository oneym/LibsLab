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
import com.oneym.demo.libslab.bean.AlertBean;
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
public class AlertAdapter extends BaseAdapter {

    private List<AlertBean> lists = null;
    private LayoutInflater mInflater = null;
    private Context context = null;

    public AlertAdapter(Context context) {
        this.context = context;
        lists = new ArrayList<AlertBean>();
        mInflater = LayoutInflater.from(this.context);
    }

    public void setData(List<AlertBean> lists) {
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public AlertBean getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            AlertBean bean = getItem(position);
            holder.tv.setText(bean.getText());
            holder.tbtn.setChecked(bean.isChecked().equals("true"));
        }
        return convertView;
    }

    private class Holder {
        TextView tv;
        ToggleButton tbtn;
        Button delbtn;
    }
}
