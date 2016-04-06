package com.oneym.libslab.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.ProgressBar;

import com.oneym.libslab.utils.common.Log;

/**
 * 一个小的等待loading对话框</br>
 * 使用方法：</br>
 * 1、调用{@link OLoadingDialog#getInstance()#show(Context)}来显示对话框</br>
 * 2、调用{@link OLoadingDialog#getInstance()#dismiss()}来关闭对话框</br>
 *
 * @author oneym
 *         Created by oneym on 16-3-19.
 */
public class OLoadingDialog {
    private static OLoadingDialog instance = null;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private ProgressBar progressBar;

    private OLoadingDialog() {
    }

    public static OLoadingDialog getInstance() {
        if (null == instance)
            instance = new OLoadingDialog();
        return instance;
    }

    public void show(Activity activity) {
        if (null == activity)
            return;
        builder = new AlertDialog.Builder(activity);
        progressBar = new ProgressBar(activity);
        if (null == dialog || !dialog.isShowing()) {
            builder.setView(progressBar);
            dialog = builder.create();
            dialog.show();
            Log.out("dialog.show");
        }
    }

    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
            Log.out("dialog.dismiss");
        }
    }
}
