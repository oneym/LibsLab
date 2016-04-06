package com.oneym.demo.libslab;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.oneym.libslab.OActivity;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.common.Utils;
import com.oneym.libslab.utils.string.UtilsString;
import com.oneym.libslab.widget.OExpandableListView;
import com.oneym.libslab.widget.ONotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends OActivity implements OExpandableListView.OnGetViewListener, ExpandableListView.OnChildClickListener {

    private OExpandableListView main_listview;
    private List<String[]> groups = null;
    private List<List<String[]>> children = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_listview = (OExpandableListView) findViewById(R.id.main_listview);
        initData();
    }

    private void initData() {
        try {
            groups = new ArrayList<String[]>();
            children = new ArrayList<List<String[]>>();
            Resources res = getResources();
            groups = analysisStringArray2List(res, R.array.groups, "：");
            String packageName = getPackageName();
            PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
            for (int i = 0; i < groups.size(); i++) {
                int resId = res.getIdentifier("child_" + i, "array", packageName);
                if (resId <= 0) {
                    Log.out("id获取错误:" + resId);
                    continue;
                }

                List<String[]> lss = analysisStringArray2List(res, resId, "：");
                for (String[] s : lss)
                    if ("版本".equals(s[0]))
                        s[1] = "v" + info.versionName + "." + info.versionCode;

                children.add(lss);
            }

            main_listview.load(groups, children, this);
            main_listview.setOnChildClickListener(this);

        } catch (PackageManager.NameNotFoundException e) {
            Log.out(e);
        }
    }

    private List<String[]> analysisStringArray2List(Resources res, int R_array_id, String separator) {
        String arr_temp[] = res.getStringArray(R_array_id);
        List<String[]> ls = null;
        if (null != arr_temp && arr_temp.length > 0) {
            ls = new ArrayList<String[]>();
            for (int i = 0; i < arr_temp.length; i++) {
                Log.out("arr_temp=" + arr_temp[i]);
                String ss[];
                if (arr_temp[i].contains(separator)) {
                    ss = arr_temp[i].split(separator);
                } else {
                    ss = new String[1];
                    ss[0] = arr_temp[i];
                }
                ls.add(ss);
            }
        }

        if (null == ls || ls.size() <= 0)
            throw new SecurityException("返回值是null");

        return ls;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent, List<String[]> groups) {
        TextView textView;
        if (null == convertView) {
            convertView = LayoutInflater.from(this).inflate(R.layout.main_groups_item, parent, false);
            textView = (TextView) convertView.findViewById(R.id.group);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        textView.setText(groups.get(groupPosition)[0]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent, List<List<String[]>> children) {
        Child child;
        if (null == convertView) {
            child = new Child();
            convertView = LayoutInflater.from(this).inflate(R.layout.main_child_item, parent, false);
            child.child_title = (TextView) convertView.findViewById(R.id.child_title);
            child.child_content = (TextView) convertView.findViewById(R.id.child_content);
            convertView.setTag(child);
        } else {
            child = (Child) convertView.getTag();
        }

        child.child_title.setText(children.get(groupPosition).get(childPosition)[0]);
        if (children.get(groupPosition).get(childPosition).length > 1) {
            child.child_content.setVisibility(View.VISIBLE);
            child.child_content.setText(children.get(groupPosition).get(childPosition)[1]);
        } else {
            child.child_content.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        Log.out(v.toString());

        switch (children.get(groupPosition).get(childPosition)[0]) {//这是判断点击事件的依据条件
            case "1s后提醒(测试)":
                Utils.toast(this, "1s后提醒(测试)[启动]");
                ONotification.load(R.mipmap.ic_launcher, "测试标题", "1s后提醒(测试)");
                ONotification.getInstance().startAt(System.currentTimeMillis() + 1000);
                break;
        }

        return true;
    }

    private class Child {
        TextView child_title, child_content;
    }
}
