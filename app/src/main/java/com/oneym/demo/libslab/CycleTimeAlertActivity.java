package com.oneym.demo.libslab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.github.jjobes.slidedaytimepicker.SlideDayTimePicker;
import com.oneym.demo.libslab.Utils.SQLiteHelper;
import com.oneym.demo.libslab.Utils.SlideDayTimeListen;
import com.oneym.demo.libslab.Utils.Utils;
import com.oneym.demo.libslab.adapter.VideoAlertAdapter;
import com.oneym.demo.libslab.widget.ScrollListviewDelete;
import com.oneym.libslab.temp.VideoAlertBean;
import com.oneym.libslab.utils.Constants;
import com.oneym.libslab.utils.common.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 定时提醒界面
 */
public class CycleTimeAlertActivity extends FragmentActivity implements Constants {

    private VideoAlertAdapter alertAdapter = null;
    private ScrollListviewDelete listView = null;
    private List<VideoAlertBean> lists = null;

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
                    SlideDayTimeListen.getInstance().setResource(CycleTimeAlertActivity.this, null, mHandler);
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
                    VideoAlertBean bean = (VideoAlertBean) msg.obj;
                    SlideDayTimeListen.getInstance().setResource(CycleTimeAlertActivity.this, bean, mHandler);
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

        alertAdapter = new VideoAlertAdapter(CycleTimeAlertActivity.this, mHandler);
        listView.setAdapter(alertAdapter);

        listView.setOnItemClickListener(new ScrollListviewDelete.ItemClickListener() {
            @Override
            public void onItemClick(int position, int view) {
                if (view == R.id.list_txt) {
                    Log.out("position=" + position + ":view=时间文本");

                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.UPDATE;
                    msg.obj = alertAdapter.getItem(position);
                    mHandler.sendMessage(msg);
                }
            }
        });

        initData();
    }

    private void initData() {
        Log.out("______________________________initData()");
        List<Map> maps = SQLiteHelper.getInstance().selectAll_alert();
        if (null != maps) {
            lists = new ArrayList<VideoAlertBean>();
            for (Map map : maps) {
                VideoAlertBean bean = new VideoAlertBean();
                bean.setId(Integer.parseInt(map.get("id").toString()));
                bean.setWeek(Integer.parseInt(map.get("week").toString()));
                bean.setTime(map.get("time").toString());
                bean.setIsChecked(map.get("isChecked").toString());
                lists.add(bean);
            }
            alertAdapter.setData(lists);
            alertAdapter.notifyDataSetChanged();

            SQLiteHelper.getInstance().show_alert();
            Utils.getInstance().startAlert();
        } else {
            Log.out("_____________暂没有数据！");
        }
    }

}
