package com.oneym.libslab.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.oneym.libslab.utils.common.Log;

/**
 * ScrollListviewDelete的适配器辅助类<br/>
 * 使用方法：</br>
 * 1、使用本类在xml中布局</br>
 * 2、在Adapter中加载这个类布局所在的xml文件</br>
 */
public class OListItemDelete extends LinearLayout {

    private float mLastMotionX;// 记住上次触摸屏的位置
    private int deltaX;
    private int back_width;//滑动显示组件的宽度
    private float downX;
    private int itemClickMin = 5;//判断onItemClick的最大距离

    public OListItemDelete(Context context) {
        this(context, null);
    }

    public OListItemDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
            if (i == 1) {
                back_width = getChildAt(i).getMeasuredWidth();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int margeLeft = 0;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                int childWidth = view.getMeasuredWidth();
                // 将内部子孩子横排排列
                view.layout(margeLeft, 0, margeLeft + childWidth,
                        view.getMeasuredHeight());
                margeLeft += childWidth;
            }
        }
    }

    private void reset() {
        scrollTo(0, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.out("item  ACTION_DOWN");
                mLastMotionX = x;
                downX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.out(back_width + "  item  ACTION_MOVE  " + getScrollX());
                deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                int scrollx = getScrollX() + deltaX;
                if (scrollx > 0 && scrollx < back_width) {
                    scrollBy(deltaX, 0);
                } else if (scrollx > back_width) {
                    scrollTo(back_width, 0);
                } else if (scrollx < 0) {
                    reset();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.out("item  ACTION_UP");
                int scroll = getScrollX();
                if (Math.abs(x - downX) < itemClickMin) {// 这里根据点击距离来判断是否是itemClick
                    reset();
                    return false;
                }
                if (deltaX > 0) {
                    if (scroll > back_width / 4) {
                        scrollTo(back_width, 0);
                    } else {
                        reset();
                    }
                } else {
                    if (scroll > back_width * 3 / 4) {
                        scrollTo(back_width, 0);
                    } else {
                        reset();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                reset();
                break;
        }
        return true;
    }
}
