package com.oneym.libslab.utils.common;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author oneym oneym@sina.cn
 * @since 20151117102556
 */
public class Utils {
    //线程池，当前只为toast方法存在
    private static ExecutorService pool = null;

    /**
     * 阿拉伯数字转化成中文数字（最高十万位）
     *
     * @param num 阿拉伯数字
     * @return 中文数字字符串
     */
    public static String num2String(int num) {
        String str = "";
        String numstr = Integer.toString(num);
        int len = numstr.length();
        if (len > 6) {
            Log.out("数字不要超过六位。");
            return "数字不要超过六位。";
        }

        for (int i = 0; i < len; i++) {
            switch (Integer.parseInt(numstr.substring(i, i + 1))) {
                case 1:
                    str += "一";
                    break;
                case 2:
                    str += "二";
                    break;
                case 3:
                    str += "三";
                    break;
                case 4:
                    str += "四";
                    break;
                case 5:
                    str += "五";
                    break;
                case 6:
                    str += "六";
                    break;
                case 7:
                    str += "七";
                    break;
                case 8:
                    str += "八";
                    break;
                case 9:
                    str += "九";
                    break;
                case 0:
                    str += "零";
                    break;
            }
            switch (len - i) {
                case 2:
                    str += "十";
                    break;
                case 3:
                    str += "百";
                    break;
                case 4:
                    str += "千";
                    break;
                case 5:
                    str += "万";
                    break;
                case 6:
                    str += "十万";
                    break;
            }
        }

        boolean flag = true;
        while (flag) {
            str = str.replace("零零", "零");
            str = str.replace("零十", "零");
            str = str.replace("零百", "零");
            str = str.replace("零千", "零");
            str = str.replace("零万", "零");
            str = str.replace("零十万", "零");
            if (str.contains("零零"))
                flag = true;
            else
                flag = false;
        }
        str = str.replace("一十万", "十万");
        if (!str.contains("十万零"))
            str = str.replace("十万", "十");
        if (str.substring(str.length() - 1).equals("零") && str.length() > 1)
            str = str.substring(0, str.length() - 1);

        return str;
    }

    /**
     * 获得当前正运行的线程数量
     */
    public static void countActiveThread() {
        Object obj = "Thread.activeCount()=" + Thread.activeCount();
        Log.out(obj);
    }

    /**
     * 显示toast
     *
     * @param context 上下文
     * @param msg     消息
     */
    public static void toast(final Context context, final String msg) {
        if (null == pool)
            pool = Executors.newCachedThreadPool();

        pool.execute(new Runnable() {
            @Override
            public void run() {
                context.getMainLooper().prepare();
                Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                context.getMainLooper().loop();
            }
        });
    }

}
