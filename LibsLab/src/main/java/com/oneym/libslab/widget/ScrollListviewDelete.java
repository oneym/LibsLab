package com.oneym.libslab.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.view.UtilsView;

/**
 * 左滑删除,需要配合{@link OListItemDelete}使用才会有效果</br>
 * 使用方法：</br>
 * 1、使用本类在xml中布局</br>
 * 2、设置自定义Adapter</br>
 * 3、设置监听{@link ScrollListviewDelete#setOnItemClickListener(ItemClickListener)}
 *
 * @author oneym
 */
public class ScrollListviewDelete extends ListView implements OnScrollListener {

    private float minDis = 10;
    private float mLastItemMotionX;// 记住上次X触摸屏的位置
    private float mLastItemMotionY;// 记住上次Y触摸屏的位置
    private float slopX;// 记住上次X触摸屏的位置
    private float slopY;// 记住上次Y触摸屏的位置
    private int mTouchSlop;// 发生触摸的容忍度
    private boolean isSlop = false;
    private boolean isLock = false;
    private ItemClickListener onItemClickListener;

    public ScrollListviewDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setOnScrollListener(this);
    }

    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 如果一个ViewGroup的onInterceptTouchEvent()方法返回true，说明Touch事件被截获，
     * 子View不再接收到Touch事件，而是转向本ViewGroup的
     * onTouchEvent()方法处理。，之后的Move从Down开始，Up都会直接在onTouchEvent()方法中处理。
     * 先前还在处理touch event的child view将会接收到一个 ACTION_CANCEL。
     * 如果onInterceptTouchEvent()返回false，则事件会交给child view处理。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isIntercept(ev)) {
            scrollTo(0, 0);
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean dte = super.dispatchTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                slopX = event.getX();
                slopY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float tempX = event.getX() - slopX;
                float tempY = event.getY() - slopY;

                if (tempX > mTouchSlop || tempY > mTouchSlop || tempX > minDis * 3 / 4 || tempY > minDis * 3 / 4)
                    isSlop = true;
                Log.out("MotionEvent.ACTION_MOVE " + isSlop);
                break;
            case MotionEvent.ACTION_UP:
                Log.out("MotionEvent.ACTION_UP " + isSlop + ",onItemClickListener = " + onItemClickListener.toString());
                if (onItemClickListener != null && !isSlop) {

                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    Log.out("x=" + x + ",y=" + y);
                    Log.out("event.getX()=" + event.getX() + ",event.getY()=" + event.getY());
                    int position = pointToPosition(x, y);
                    Log.out("position=" + position + ",getChildCount()=" + getChildCount());
                    if (AdapterView.INVALID_POSITION == position)//位置是非法
                        return dte;
                    View item = getChildAt(position - getFirstVisiblePosition());
                    View v = UtilsView.getParentItemChildByPoint(item, x, y, event.getRawY() - y);
                    if (null != v) {
                        dte = true;
                        scrollTo(0, 0);//在用户发生点击事件后，将item复位
                        onItemClickListener.onItemClick(position, v.getId());
                    }
                }

                isSlop = false;//这个参数用完了要复位的
                break;
        }
        return dte;
    }

    /**
     * 检测是ListView滑动还是item滑动 isLock 一旦判读是item滑动，则在up之前都是返回false
     */
    private boolean isIntercept(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.out("isIntercept  ACTION_DOWN  " + isLock);
                mLastItemMotionX = x;
                mLastItemMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.out("isIntercept  ACTION_MOVE  " + isLock);
                if (!isLock) {
                    float deltaX = Math.abs(mLastItemMotionX - x);
                    float deltay = Math.abs(mLastItemMotionY - y);
                    mLastItemMotionX = x;
                    mLastItemMotionY = y;
                    if (deltaX > deltay && deltaX > minDis) {
                        isLock = true;
                        return false;
                    }
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.out("isIntercept  ACTION_UP  " + isLock);
                isLock = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.out("isIntercept  ACTION_CANCEL  " + isLock);
                isLock = false;
                break;
        }
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {//认为是滚动，重置
            scrollTo(0, 0);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    public interface ItemClickListener {
        void onItemClick(int position, int viewId);
    }

}
