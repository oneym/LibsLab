package com.oneym.libslab.widget;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.string.UtilsString;
import com.oneym.libslab.utils.time.UtilsTime;

/**
 * Notification</br>
 * 使用方法：</br>
 * 1、开发者的Activity需要继承{@link com.oneym.libslab.OActivity}
 * 2、调用{@link ONotification#load(int, String, String)}来装载数据</br>
 * 3、调用{@link ONotification#startAt(long)}来启动定时提醒</br>
 *
 * @author oneym oneym@sina.cn
 * @since 20151130155050
 */
public class ONotification extends Notification {//妈的智障，这行报错不用管

    private static final String BC_NOTIFICATION_ACTION = "com.oneym.libslab.widget.BC_NOTIFICATION_ACTION";
    private static ONotification instance = null;
    private static Context p_context = null;
    private static int p_icon = -1;
    private static PendingIntent mpIntent = null;
    //utc到gmt区时补偿
    //private static final long GMTDELTA = UtilsTime.getGMTDelta(UtilsTime.getCurrentTimeZone());
    PendingIntent bpIntent = null;
    private Notification notification = null;
    private static String p_tickerText = "chinese oneym";
    private static String p_tickerTitle = "author";
    private NotificationManager mNotificationManager = null;
    private AlarmManager mAlarmManager = null;
    private Intent bIntent = null;
    //Notification执行的时间点
    long target = 0L;
    //计算时间间隔并且发送广播
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            List<Map> maps = SQLiteHelper.getInstance().selectAll_alert();
//            Log.out("runnable staring");
//            for (Map m : maps) {
//                int nowWeek = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_WEEK);
//                int id = Integer.parseInt(m.get("id").toString());
//                int week = Integer.parseInt(m.get("week").toString());
//                String time = m.get("time").toString();
//                String isChecked = m.get("isChecked").toString();
//                Log.out("week=" + week + ",nowWeek=" + nowWeek);
//                if (isChecked.equals("true")) {
//                    if (nowWeek == week) {
//                        long now = System.currentTimeMillis();
//                        long target = UtilsTime.stringTime2Millis_clockIn24(time, ":");
//                        Log.out("now=" + now);
//                        Log.out("target=" + target);
//                        if (now <= target) {
//                            Log.out("time=" + time + ",week=" + week);
//                            // 在TRIGGERATTIME毫秒时，执行
//                            mAlarmManager.set(AlarmManager.RTC_WAKEUP, target, bpIntent);
//                        }
//                    }
//                }
//            }

            long now = System.currentTimeMillis();
            Log.out("now=" + now);
            Log.out("target=" + target);
            if (now <= target) {
                // 在TRIGGERATTIME毫秒时，执行
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, target, bpIntent);
            }
        }
    };

    private ONotification() {
        if (null == p_context || -1 == p_icon || null == mpIntent)
            throw new NullPointerException("参数为空，没有setInfo参数。");

        mNotificationManager = (NotificationManager) p_context.getSystemService(p_context.NOTIFICATION_SERVICE);

        mAlarmManager = (AlarmManager) p_context.getSystemService(p_context.ALARM_SERVICE);
        //东八区区时<timezone id="Asia/Shanghai">中国标准时间 (北京)</timezone>
        //mAlarmManager.setTimeZone(UtilsTime.getCurrentTimeZone());//不设置时区

        if (null == bIntent)
            bIntent = new Intent();
    }

    /**
     * 初始化，请调用{@link com.oneym.libslab.widget.Init#init(android.app.Activity)}来初始化
     *
     * @param context 上下文
     * @hide
     */
    static void init(Context context) {
        p_context = context;
        mpIntent = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()), 0);
    }

    /**
     * @param icon    图片资源id
     * @param title   提示标题
     * @param content 提示内容
     * @return ONotification对象
     */
    public static ONotification load(int icon, String title, String content) {
        p_icon = icon;
        p_tickerTitle = title;
        p_tickerText = content;
        return getInstance();
    }

    public static ONotification getInstance() {
        if (null == instance)
            instance = new ONotification();
        return instance;
    }

    /**
     * 让notification处于准备状态
     *
     * @param targetTime 在这个时间点显示提示
     */
    public void startAt(long targetTime) {
        target = targetTime;
        bIntent.setAction(BC_NOTIFICATION_ACTION);
        bIntent.putExtra(OBroadcastReceiver.TAG, OBroadcastReceiver.VIDEOALERT_INDEX);
        bIntent.putExtra(OBroadcastReceiver.TITLE, p_tickerTitle);
        bIntent.putExtra(OBroadcastReceiver.CONTENT, p_tickerText);
        bpIntent = PendingIntent.getBroadcast(p_context, 0, bIntent, 0);
        new Thread(runnable).start();
    }

    /**
     * 真实触发notification
     *
     * @hide
     */
    void show(String tickerTitle, String tickerText) {
        if (UtilsString.isEmptyString(tickerTitle))
            tickerText = p_tickerText;
        if (UtilsString.isEmptyString(tickerTitle))
            tickerTitle = p_tickerTitle;
        //notification = new Notification(p_icon, tickerText, System.currentTimeMillis() + GMTDELTA);
        notification = new Notification(p_icon, tickerText, System.currentTimeMillis());
        notification.flags = Notification.FLAG_INSISTENT;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.setLatestEventInfo(p_context, tickerTitle, tickerText, mpIntent);
        mNotificationManager.notify(1, notification);
        Log.out("shown_____________");
    }

}
