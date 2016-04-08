package com.oneym.demo.libslab.Utils;

import com.oneym.demo.libslab.R;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.time.UtilsTime;
import com.oneym.libslab.widget.ONotification;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by oneym on 16-4-6.
 */
public class Utils {
    private static Utils instance = null;

    public static Utils getInstance() {
        if (null == instance)
            instance = new Utils();
        return instance;
    }


    /**
     * 启动所有定时器
     */
    public void startAlert() {
        List<Map> maps = SQLiteHelper.getInstance().selectAll_alert();
        ONotification.load(R.mipmap.ic_launcher, "LibsLab", "亲，时间到了～");
        Log.out("alert staring");
        for (Map m : maps) {
            int nowWeek = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_WEEK);
            int week = Integer.parseInt(m.get("week").toString());
            String time = m.get("time").toString();
            String isChecked = m.get("isChecked").toString();
            Log.out("week=" + week + ",nowWeek=" + nowWeek);
            if (isChecked.equals("true")) {
                if (nowWeek == week) {
                    long now = System.currentTimeMillis();
                    long target = UtilsTime.stringTime2Millis_clockIn24(time, ":");
                    Log.out("now=" + now);
                    Log.out("target=" + target);
                    if (now <= target) {
                        Log.out("time=" + time + ",week=" + week);
                        // 在target毫秒时，执行
                        ONotification.getInstance().startAt(target);
                    }
                }
            }
        }
    }
}
