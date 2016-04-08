package com.oneym.demo.libslab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.github.jjobes.slidedaytimepicker.SlideDayTimePicker;
import com.oneym.demo.libslab.Utils.SQLiteHelper;
import com.oneym.demo.libslab.Utils.SlideDayTimeListen;
import com.oneym.demo.libslab.Utils.Util;
import com.oneym.demo.libslab.adapter.AlertAdapter;
import com.oneym.demo.libslab.bean.AlertBean;
import com.oneym.libslab.utils.Constants;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.common.Utils;
import com.oneym.libslab.widget.ScrollListviewDelete;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 定时提醒界面
 */
public class AlertActivity extends FragmentActivity implements Constants, ScrollListviewDelete.ItemClickListener {

    private AlertAdapter alertAdapter = null;
    private ScrollListviewDelete listView = null;
    private List<AlertBean> lists = null;

    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.REFRESH:
                    Log.out("`````````````initData()");
                    initData();
                    break;
                case Constants.ADD:
                    SlideDayTimeListen.getInstance().setResource(AlertActivity.this, null, mHandler);
                    new SlideDayTimePicker.Builder(getSupportFragmentManager())
                            .setListener(SlideDayTimeListen.getInstance().slideDayTimeListener)
                            .setInitialDay(Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_WEEK))
                            .setInitialHour(Calendar.getInstance(Locale.CHINA).get(Calendar.HOUR_OF_DAY))
                            .setInitialMinute(Calendar.getInstance(Locale.CHINA).get(Calendar.MINUTE))
                            .setIs24HourTime(true)
                            .build()
                            .show();
                    break;
                case Constants.UPDATE:
                    AlertBean bean = (AlertBean) msg.obj;
                    SlideDayTimeListen.getInstance().setResource(AlertActivity.this, bean, mHandler);
                    new SlideDayTimePicker.Builder(getSupportFragmentManager())
                            .setListener(SlideDayTimeListen.getInstance().slideDayTimeListener)
                            .setInitialDay(bean.getWeek())
                            .setInitialHour(Integer.parseInt(bean.getTime().substring(0, bean.getTime().indexOf(":"))))
                            .setInitialMinute(Integer.parseInt(bean.getTime().substring(bean.getTime().indexOf(":") + 1)))
                            .setIs24HourTime(true)
                            .build()
                            .show();
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_alert);
        SQLiteHelper.init(this);

        findViewById(R.id.alert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.alert_btn:
                        Log.out("添加一条提醒记录。");
                        mHandler.sendEmptyMessage(Constants.ADD);
                        break;
                }
            }
        });

        listView = (ScrollListviewDelete) findViewById(R.id.alert_list);
        alertAdapter = new AlertAdapter(AlertActivity.this);
        listView.setAdapter(alertAdapter);
        listView.setOnItemClickListener(this);

        initData();
    }

    private void initData() {
        Log.out("______________________________initData()");
        List<Map> maps = SQLiteHelper.getInstance().selectAll_alert();
        if (null != maps) {
            lists = new ArrayList<AlertBean>();
            for (Map map : maps) {
                AlertBean bean = new AlertBean();
                bean.setId(Integer.parseInt(map.get("id").toString()));
                bean.setWeek(Integer.parseInt(map.get("week").toString()));
                bean.setTime(map.get("time").toString());
                bean.setIsChecked(map.get("isChecked").toString());
                lists.add(bean);
            }
            alertAdapter.setData(lists);
            alertAdapter.notifyDataSetChanged();

            SQLiteHelper.getInstance().show_alert();
            Util.getInstance().startAlert();
        } else {
            Log.out("_____________暂没有数据！");
        }
    }

    @Override
    public void onItemClick(int position, int viewId) {

        AlertBean bean = alertAdapter.getItem(position);
        Log.out("onItemClick+=viewId:" + viewId);

        switch (viewId) {
            case R.id.list_txt:
                Log.out("position=" + position + ":view=时间文本");
                Message msg = mHandler.obtainMessage();
                msg.what = Constants.UPDATE;
                msg.obj = alertAdapter.getItem(position);
                mHandler.sendMessage(msg);
                break;
            case R.id.list_toggle_btn:
                SQLiteHelper.getInstance().update_alert(bean.getId(),
                        bean.getTime(),
                        bean.getWeek(),
                        bean.isChecked().equals("true") ? "false" : "true");
                mHandler.sendEmptyMessage(Constants.REFRESH);
                break;
            case R.id.btnDelete:
                Log.out("position=" + position);
                SQLiteHelper.getInstance().deleteById_alert(bean.getId());
                mHandler.sendEmptyMessage(Constants.REFRESH);
                Utils.toast(this, "[" + bean.getText() + "] 的提醒已删除");
                break;
        }
    }
}
