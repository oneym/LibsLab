package com.oneym.libslab.utils.time;

import com.oneym.libslab.exception.NotNaturalNumberException;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.string.UtilsString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author oneym
 * @since 20151211141427
 */
public class UtilsTime {
    /**
     * 时间格式
     */
    public enum FORMAT {
        yyyyMMdd, yyyyMMddHHmm, yyyyMMddHHmmss
    }

    /**
     * 把毫秒转化成{@code 分钟:秒 }的形式
     *
     * @param timeMillis 毫秒
     * @return 格式化后的时间
     * @throws NotNaturalNumberException 如果参数不是自然数
     */
    public static String getDigitalClockFormatTime(long timeMillis) {
        String ret = "";
        try {
            if (timeMillis < 0) {
                throw new NotNaturalNumberException();
            }
            int min = (int) (timeMillis / 1000 / 60);
            int sec = (int) (timeMillis / 1000 % 60);
            ret = min + ":" + sec;
        } catch (NotNaturalNumberException notNaturalNumber) {
            Log.out(notNaturalNumber);
        }
        return ret;
    }

    /**
     * 把电子表时间转换为今天的毫秒时间
     *
     * @param clockIn24 电子表时间(e.g. 13:25)
     * @param separator 小时与分钟之间的分隔符(e.g. :)
     * @return 毫秒
     */
    public static long stringTime2Millis_clockIn24(String clockIn24, String separator) {
        Calendar calendar = Calendar.getInstance();
        try {
            int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
            int month = Calendar.getInstance(Locale.CHINA).get(Calendar.MONTH) + 1;
            int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
            Log.out("_stringTime2Millis_clockIn24:" + clockIn24);
            Log.out("stringTime2Millis_clockIn24_:" + year + "-" + month + "-" + day + " " + clockIn24);
            String time = year + "";
            if (month < 10)
                time += "0" + month;
            else
                time += "" + month;
            if (day < 10)
                time += "0" + day;
            else
                time += "" + day;

            time += " " + clockIn24;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH" + separator + "mm");
            calendar.setTime(sdf.parse(time));
        } catch (ParseException e) {
            calendar.setTime(new Date(-1));
            Log.out(e);
        }
        return calendar.getTimeInMillis();
    }

    /**
     * 将字符串时间转化成毫秒时间
     *
     * @param time   待转换的时间
     * @param format 时间格式，使用{@link FORMAT}中的值
     * @return 毫秒时间
     */
    public static long stringTime2Millis(String time, FORMAT format) {
        if (!UtilsString.isEmptyString(time))
            if (time.length() != format.name().length())
                throw new IllegalArgumentException("参数格式错误");

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format.name());
            calendar.setTime(sdf.parse(time));
        } catch (ParseException e) {
            Log.out(e);
        }

        return calendar.getTimeInMillis();
    }

    /**
     * @return 当前手机所在时区
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        int offsetMinutes = tz.getRawOffset() / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        builder.append("GMT");
        builder.append(sign);
        UtilsString.appendNumber(builder, 2, offsetMinutes / 60);
        builder.append(':');
        UtilsString.appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    /**
     * 获得协调世界时与格林尼治区时的补偿时,就是两个不同时区间的时间差
     *
     * @param timeZone 时区（e.g. GMT+08:00,用Utils.getCurrentTimeZone()函数来获取本地当前时区）
     * @return utc到gmt的毫秒级时间补偿
     */
    public static long getGMTDelta(String timeZone) {
        String sign = timeZone.substring(3, 4);
        int zoneNum = Integer.parseInt(timeZone.substring(4, 6));
        Log.out("sign=" + sign + ",zoneNum=" + zoneNum);
        long GMTDELTA = zoneNum * 60L * 60L * 1000L;
        if (sign.equals("-"))
            GMTDELTA = -GMTDELTA;
        return GMTDELTA;
    }

    /**
     * 毫秒时间获得，周几、年、月、日、时、分、秒<br/>
     * 注意：月份已经修正，小时使用24小时，周几的修正需要配合{@link UtilsTime#getWeekWordChinese(int)}
     *
     * @param millis 带转化的毫秒
     * @return 0，周几、1，年、2，月、3，日、4，时、5，分、6，秒
     */
    public static String[] millis2EyMdHms(long millis) {
        Log.out("millis=" + millis);

        String[] time = new String[7];
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(millis);
        time[0] = UtilsString.toString(calendar.get(Calendar.DAY_OF_WEEK));
        time[1] = UtilsString.toString(calendar.get(Calendar.YEAR));
        time[2] = UtilsString.toString(calendar.get(Calendar.MONTH) + 1);//系统的月份是从0开始的，这里加1进行修正
        time[3] = UtilsString.toString(calendar.get(Calendar.DAY_OF_MONTH));
        time[4] = UtilsString.toString(calendar.get(Calendar.HOUR_OF_DAY));//24小时
        time[5] = UtilsString.toString(calendar.get(Calendar.MINUTE));
        time[6] = UtilsString.toString(calendar.get(Calendar.SECOND));
        for (int i = 2; i <= 6; i++)
            if (time[i].length() < "00".length())
                time[i] = "0" + time[i];
        return time;
    }

    public static String getWeekWordChinese(int week) {
        String w = "";

        if (week == Calendar.MONDAY)
            w = "一";
        if (week == Calendar.TUESDAY)
            w = "二";
        if (week == Calendar.WEDNESDAY)
            w = "三";
        if (week == Calendar.THURSDAY)
            w = "四";
        if (week == Calendar.FRIDAY)
            w = "五";
        if (week == Calendar.SATURDAY)
            w = "六";
        if (week == Calendar.SUNDAY)
            w = "日";
        return w;
    }

}
