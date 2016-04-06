package com.oneym.libslab;

import android.app.Activity;
import android.os.Bundle;

import com.oneym.libslab.widget.Init;
import com.oneym.libslab.widget.OPopupMenu;

/**
 * 本类继承自{@link Activity}</br>
 * 用来初始化工具箱
 *
 * @author oneym
 * @since 20160323203841
 */
public class OActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化工具箱
        Init.init(OActivity.this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onUserLeaveHint() {
        OPopupMenu.getInstance().dismiss();//解决用户点击home键再返回应用弹窗出现在遮罩下面（在点下home键后退出前关闭弹窗）
        super.onUserLeaveHint();
    }
}
