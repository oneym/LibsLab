package com.oneym.demo.libslab.bean;

import com.oneym.libslab.utils.common.Utils;

/**
 * 提醒记录基类
 *
 * @author oneym oneym@sina.cn
 * @since 20151127161706
 */
public class AlertBean {
    private int id = -1;
    private String time = "";
    private int week = -1;
    private String isChecked = "false";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String isChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getText() {
        if (-1 == getWeek())
            return "null";

        String w = "";
        if ((getWeek() - 1) == 0)
            w = "日";
        else
            w = Utils.num2String(getWeek() - 1);

        return getTime() + "  星期" + w;
    }

    @Override
    public String toString() {
        return "[id=" + getId() + ",getText=" + getText() + ",isChecked=" + isChecked() + "]";
    }
}
