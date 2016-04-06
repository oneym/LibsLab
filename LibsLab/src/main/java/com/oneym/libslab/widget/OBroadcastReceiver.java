package com.oneym.libslab.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oneym.libslab.utils.common.Log;

/**
 * 这是一个神奇的广播接收器，开发者不要调用
 *
 * @author oneym oneym@sina.cn
 * @since 20151201114655
 */
public class OBroadcastReceiver extends BroadcastReceiver {

    private static final int INDEX = 1;
    public static final int VIDEOALERT_INDEX = INDEX + 1;
    public static final String TAG = "TAG_ALERTBROADCASTRECEIVER";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    @Override
    public void onReceive(Context context, Intent intent) {
        int index = -1;
        String title = "";
        String content = "";
        if (intent.hasExtra(TAG))
            index = intent.getIntExtra(TAG, -1);
        if (intent.hasExtra(TITLE))
            title = intent.getStringExtra(TITLE);
        if (intent.hasExtra(CONTENT))
            content = intent.getStringExtra(CONTENT);
        switch (index) {
            case VIDEOALERT_INDEX:
                Log.out("______________________VIDEOALERT_INDEX");
                ONotification.getInstance().show(title, content);
                Log.landmark();
                break;
            default:
                Log.out("OBroadcastReceiver maybe receive a anonymous boradcast,be careful!");
                break;
        }
    }
}
