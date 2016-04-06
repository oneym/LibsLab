package com.oneym.libslab.utils.common;


import com.oneym.libslab.utils.config.GlobalConfig;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 打印日志
 *
 * @author oneym
 * @since 20151103161316
 */
public class Log implements GlobalConfig {

    private static final String TAG = "LOGTAG";

    /**
     * 打印信息。
     * 只在调试模式下有效。<br/>
     *
     * @param log 需要输出的文本信息
     */
    public static void out(String log) {
        if (isDebug)
            android.util.Log.i(TAG + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), log);
    }

    /**
     * 打印信息。
     * 只在调试模式下有效。<br/>
     *
     * @param tag 标记
     * @param log 需要输出的文本信息
     * @deprecated 20160323135813
     */
    public static void out(String tag, String log) {
        if (isDebug)
            android.util.Log.i(TAG + "->" + tag, log);
    }


    /**
     * 可能出错的地方打印信息。其他的地方不要使用。
     * 只在调试模式下有效。<br/>
     *
     * @param obj 需要检索的对象
     */
    public static void out(Object obj) {
        if (isDebug) {
            android.util.Log.w(TAG + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), obj.getClass().toString());
            android.util.Log.w(TAG + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), obj.toString());
        }
    }

    /**
     * 可能出错的地方打印信息。其他的地方不要使用。
     * 只在调试模式下有效。<br/>
     *
     * @param obj 需要检索的对象
     * @deprecated 20160323135813
     */
    public static void out(String tag, Object obj) {
        if (isDebug) {
            android.util.Log.w(TAG + "->" + tag, obj.getClass().toString());
            android.util.Log.w(TAG + "->" + tag, obj.toString());
        }
    }

    /**
     * 输出异常时的堆栈信息
     *
     * @param e Exception子类对象
     */
    public static void out(Exception e) {
        StackTraceElement[] elements = e.getStackTrace();
        android.util.Log.e(TAG + "#STACKTRACE" + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), e.getClass().toString() + ": " + e.getMessage());
        for (StackTraceElement element : elements) {
            android.util.Log.e(TAG + "#STACKTRACE" + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), element.toString());
        }
        //System.exit(0);//亚麻跌，某都某都，刚巴爹，这行代码注释掉是不让应用退出
        //bugly
        CrashReport.postCatchedException(e);
    }

    /**
     * 输出异常时的堆栈信息
     *
     * @param e Exception子类对象
     * @deprecated 20160323135813
     */
    public static void out(String tag, Exception e) {
        StackTraceElement[] elements = e.getStackTrace();
        android.util.Log.e(TAG + "#STACKTRACE->" + tag, e.getClass().toString() + ": " + e.getMessage());
        for (StackTraceElement element : elements) {
            android.util.Log.e(TAG + "#STACKTRACE->" + tag, element.toString());
        }
        //System.exit(0);//亚麻跌，某都某都，刚巴爹，这行代码注释掉是不让应用退出
        //bugly
        CrashReport.postCatchedException(e);
    }

    /**
     * 热爱生命版分割线。<br/><br/>
     * <p>汪国真，1956-2015(中国)，当代诗人、书画家。1982年毕业于暨南大学中文系。</p>
     *
     * @see <a href="http://baike.baidu.com/link?url=sQNCOXlTzralNWOWIzuLFV2k3JeudhWoq_VmjPIdAt2PEL4ETGr5SNhp7S5Q_E-IA_2DnlYMWwNVnW9rfXTryK">More...</a>
     */
    public static void separator() {
        android.util.Log.d(TAG + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), "我不去想，身后会不会袭来寒风冷雨，既然目标是地平线，留给世界的只能是背影。");
    }

    /**
     * 陋室铭版分割线。<br/><br/>
     * <p>刘禹锡，772-842(中国)，唐代文学家、哲学家。唐●贞元九年（793年），进士及第。</p>
     *
     * @see <a href="http://baike.baidu.com/link?url=5haZ5RbJVNLuWIQ3iB8X4cZ8TfYsqeNERjwEuX_xTlnfD0MrkuIZYmc-or-rSoMkJndQExBUj3Tiy9Egd-Kksa">More...</a>
     */
    public static void landmark() {
        android.util.Log.v(TAG + "->" + new Throwable().getStackTrace()[1].getClassName() + "." + new Throwable().getStackTrace()[1].getMethodName(), "山不在高，有仙则名。水不在深，有龙则灵。斯是陋室，惟吾德馨。");
    }
}
