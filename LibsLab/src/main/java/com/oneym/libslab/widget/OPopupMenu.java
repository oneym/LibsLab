package com.oneym.libslab.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.oneym.libslab.exception.NotInitException;
import com.oneym.libslab.R;
import com.oneym.libslab.utils.common.Density;
import com.oneym.libslab.utils.common.Log;

import java.util.ArrayList;

/**
 * 底部弹出对话框，仿QQ样式</br>
 * 使用方法：</br>
 * 1、开发者的Activity需要继承{@link com.oneym.libslab.OActivity}</br>
 * 2、调用{@link OPopupMenu#getInstance()#load(ArrayList, AdapterView.OnItemClickListener)}来装载数据</br>
 * 3、调用{@link OPopupMenu#getInstance()#show(View)}来显示菜单</br>
 * 4、调用{@link OPopupMenu#getInstance()#dismiss()}来关闭菜单</br>
 *
 * @author oneym
 * @since 20151231140139
 */
public class OPopupMenu {
    private ArrayList<String> items = null;
    private PopupWindow menu = null;
    private View maskView;
    private WindowManager wm;

    private enum TYPE {
        HEADER,
        BODY,
        FOOT,
        TAIL,
        NULL
    }

    private Activity activity = null;
    private ListView listView = null;
    private boolean isLoaded = false;
    private static OPopupMenu instance = null;

    private OPopupMenu() {
    }

    public static OPopupMenu getInstance() {
        if (null == instance)
            instance = new OPopupMenu();
        return instance;
    }


    /**
     * 初始话上弹菜单</br>
     * 初始化，请调用{@link com.oneym.libslab.widget.Init#init(android.app.Activity)}来初始化
     *
     * @param activity Activity
     */
    void init(Activity activity) {
        if (null == activity)
            throw new NullPointerException("activity 不可为空");
        wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        activity.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        this.activity = activity;
    }

    /**
     * 数据加载
     *
     * @param items    按钮显示的文字，只传入按键名称，可以自动补上取消按钮的
     * @param listener 点击监听器
     */
    public OPopupMenu load(ArrayList<String> items, AdapterView.OnItemClickListener listener) {
        if (items.size() < 2)
            throw new IllegalArgumentException("items数量不得小于2");
        if (!items.get(items.size() - 1).equals("取消"))
            items.add("取消");
        this.items = items;
        listView = (ListView) LayoutInflater.from(activity).inflate(R.layout.opopupmenu_listview, null, false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        listView.setSelector(R.drawable.hide_listview_yellow_selector);
        isLoaded = true;
        return getInstance();
    }

    /**
     * 显示popupwindow
     *
     * @param v
     * @throws NotInitException
     */
    public void show(View v) throws NotInitException {
        if (!isLoaded)
            throw new NotInitException();

        menu = new PopupWindow(listView, Density.getScreenWidth(activity) - Density.dip2px(activity, 20), ViewGroup.LayoutParams.WRAP_CONTENT);
        menu.setBackgroundDrawable(new ColorDrawable());
        menu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });

        menu.setTouchable(true);
        menu.setOutsideTouchable(true);
        View v_ = v.getRootView();
        menu.setFocusable(true);
        addMaskView(v_.getContext(), v_.getWindowToken());
        menu.showAtLocation(v_, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 退出显示的窗口<br/>
     */
    public void dismiss() {
        if (null != menu)
            menu.dismiss();
        removeMaskView();
    }

    //蒙层方法参考自https://github.com/yzeaho/BottomPushPopWindow/blob/4e099bb92b6d52018dcd20e2299a5126acfeb9d0/library/BottomPushPopWindow/src/com/github/yzeaho/popupwindow/BottomPushPopupWindow.java
    private void addMaskView(Context context, IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = token;
        p.windowAnimations = android.R.style.Animation_Toast;
        maskView = new View(context);
        maskView.setBackgroundColor(0x7f000000);
        maskView.setFitsSystemWindows(false);
        maskView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        wm.addView(maskView, p);
    }

    private void removeMaskView() {
        if (maskView != null) {
            wm.removeViewImmediate(maskView);
            maskView = null;
        }
    }

    /**
     * 适配器类，不新建更多文件，新建太多的文件在未来我可能找不到了
     */
    private BaseAdapter adapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            TYPE type = TYPE.NULL;
            if (0 == position) {
                type = TYPE.HEADER;
            } else if (3 == getCount() && 1 == position || getCount() > 3 && position == getCount() - 2) {
                type = TYPE.FOOT;
            } else if (getCount() > 3 && position < getCount() - 2 && position > 0) {
                type = TYPE.BODY;
            } else if (getCount() - 1 == position) {
                type = TYPE.TAIL;
            }
            return type.ordinal();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            Holder holder = new Holder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.opopupmenu_listview_item, null, false);

            if (TYPE.HEADER.ordinal() == type) {
                holder.pop_header = (TextView) convertView.findViewById(R.id.pop_header);
                holder.pop_header.setVisibility(View.VISIBLE);
                holder.pop_header.setText(items.get(position));
            } else if (TYPE.BODY.ordinal() == type) {
                holder.pop_body = (TextView) convertView.findViewById(R.id.pop_body);
                holder.pop_body.setVisibility(View.VISIBLE);
                holder.pop_body.setText(items.get(position));
            } else if (TYPE.FOOT.ordinal() == type) {
                holder.pop_foot = (TextView) convertView.findViewById(R.id.pop_foot);
                holder.pop_foot.setVisibility(View.VISIBLE);
                holder.pop_foot.setText(items.get(position));
            } else if (TYPE.TAIL.ordinal() == type) {
                holder.pop_tail = (TextView) convertView.findViewById(R.id.pop_tail);
                holder.pop_tail.setVisibility(View.VISIBLE);
                holder.pop_tail.setText(items.get(position));
            }
            return convertView;
        }

        class Holder {
            TextView pop_header = null;
            TextView pop_body = null;
            TextView pop_foot = null;
            TextView pop_tail = null;
        }
    };

}
