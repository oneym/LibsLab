package com.oneym.libslab.widget;

import android.app.Activity;
import android.content.Context;

/**
 * 初始化工具包中需要初始化的类</br>
 * 1、使用工具箱的开发者的Activity需要继承{@link com.oneym.libslab.OActivity}</br>
 * 2、工具箱在{@link com.oneym.libslab.OActivity}的onCreate方法中来初始化工具包</br>
 *
 * @author oneym
 * @since 20160323150756
 */
public class Init {
    /**
     * 初始化工具包
     *
     * @param activity 上下文
     */
    public static void init(Activity activity) {
        Context context = activity.getApplicationContext();
        //ONotivication
        ONotification.init(activity);
        //OPopupMenu
        OPopupMenu.getInstance().init(activity);

    }
}
