package com.oneym.demo.libslab.Utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.github.jjobes.slidedaytimepicker.SlideDayTimeListener;
import com.oneym.demo.libslab.bean.AlertBean;
import com.oneym.libslab.utils.Constants;
import com.oneym.libslab.utils.common.Log;

/**
 * 时间选择器监听中间类
 *
 * @author oneym oneym@sina.cn
 * @since 20151128145343
 */
public class SlideDayTimeListen {
    private static SlideDayTimeListen instance = null;
    private AlertBean bean = null;
    private Context context = null;
    private Handler mHandler = null;
    private AlertBean _bean = null;
    public final SlideDayTimeListener slideDayTimeListener = new SlideDayTimeListener() {

        long id = -1;

        @Override
        public void onDayTimeSet(int day, int hour, int minute) {
            bean = new AlertBean();
            String time = "";
            if (hour < 10)
                time = "0" + Integer.toString(hour);
            else
                time = Integer.toString(hour);

            if (minute < 10)
                time += ":0" + Integer.toString(minute);
            else
                time += ":" + Integer.toString(minute);

            bean.setTime(time);
            bean.setWeek(day);
            bean.setIsChecked("true");
            Log.out(bean.toString());
            if (null == _bean) {
                id = SQLiteHelper.getInstance().insert_alert(bean.getWeek(), bean.getTime(), bean.isChecked());
                Toast.makeText(context, "添加完成", Toast.LENGTH_LONG).show();
                SQLiteHelper.getInstance().show_alert();
            } else {
                id = SQLiteHelper.getInstance().update_alert(_bean.getId(), bean.getTime(), bean.getWeek(), _bean.isChecked());
                Toast.makeText(context, "修改完成", Toast.LENGTH_LONG).show();
            }
            Log.out("_-----onDayTimeSet" + mHandler.sendEmptyMessage(Constants.REFRESH));
        }

        @Override
        public void onDayTimeCancel() {
            Log.out("星期未设置。此处当有飘窗！");
            bean = null;
            if (null == _bean) {
                Toast.makeText(context, "未添加", Toast.LENGTH_LONG).show();
                SQLiteHelper.getInstance().deleteById_alert((int) id);
            } else {
                Toast.makeText(context, "未修改", Toast.LENGTH_LONG).show();
                SQLiteHelper.getInstance().update_alert(_bean.getId(), _bean.getTime(), _bean.getWeek(), _bean.isChecked());
            }
            Log.out("_-----onDayTimeCancel" + mHandler.sendEmptyMessage(Constants.REFRESH));

        }
    };

    public static SlideDayTimeListen getInstance() {
        if (null == instance)
            instance = new SlideDayTimeListen();
        return instance;
    }

    public void setResource(Context context, AlertBean bean, Handler mHandler) {
        this.context = context;
        this.mHandler = mHandler;
        this._bean = bean;
    }
}
