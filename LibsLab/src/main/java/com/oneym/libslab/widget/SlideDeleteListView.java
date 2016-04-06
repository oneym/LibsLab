package com.oneym.libslab.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 自定义侧滑删除listview中的item</br>
 * 使用方法：</br>
 * 1、使用本类在xml中布局</br>
 * 2、开发者自定义adapter
 * 3、需要实现{@link SlideDeleteListView#setRemoveListener(RemoveListener)}
 * 4、当设置{@link SlideDeleteListView#setIncludeHeader(boolean)}为true时，第一个不可删除
 *
 * @author oneym
 * @see <a href="https://github.com/Gracker/SlideDelete-ListView/blob/master/SlideDeleteListViewDemo/slidedeletelistview/src/main/java/com/performance/slidedeletelistview/SlideDeleteListView.java" />由该文件修改而成</a>
 * @since 20151203160323
 */
public class SlideDeleteListView extends ListView {
    private static final int SNAP_VELOCITY = 600;
    /**
     * ListView item位置
     */
    private int slidePosition;
    /**
     * 触摸时的y坐标
     */
    private int downY;
    /**
     * 触摸时的x坐标
     */
    private int downX;
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * ListView item
     */
    private View itemView;
    /**
     * Scroller
     */
    private Scroller scroller;
    /**
     * VelocityTracker
     */
    private VelocityTracker velocityTracker;
    /**
     * 滑动状态值,默认是false
     */
    private boolean isSlide = false;
    /**
     * 发生触摸的容忍度
     */
    private int mTouchSlop;
    /**
     * RemoveListener
     */
    private RemoveListener mRemoveListener;
    /**
     * 删除方向
     */
    private RemoveDirection removeDirection;

    /**
     * 列表包含列表头，默认是false不包含
     */
    private boolean isIncludeHeader = false;

    public SlideDeleteListView(Context context) {
        this(context, null);
    }


    public SlideDeleteListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDeleteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 设置列表包含列表头，默认是false不包含
     *
     * @param isIncludeHeader
     */
    public void setIncludeHeader(boolean isIncludeHeader) {
        this.isIncludeHeader = isIncludeHeader;
    }

    /**
     * 判断当前item位置是否是列表头
     *
     * @param slidePosition now touch position
     * @return false不是表头，true是表头
     */
    private boolean getIncludeHeader(int slidePosition) {
        if (isIncludeHeader)
            return 0 == slidePosition;
        return false;
    }

    /**
     * 设置移除监听
     *
     * @param removeListener RemoveListener对象
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    //派遣触摸事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);

                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();

                slidePosition = pointToPosition(downX, downY);

                if (slidePosition == AdapterView.INVALID_POSITION
                        || getIncludeHeader(slidePosition)) {//with header
                    isSlide = false;
                    return super.dispatchTouchEvent(event);
                }

                // get touch item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                        || (Math.abs(event.getX() - downX) > mTouchSlop && Math
                        .abs(event.getY() - downY) < mTouchSlop)) {
                    isSlide = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSlide) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    /**
     * 从右向左卷动（隐藏）
     */
    private void scrollRight() {
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (screenWidth + itemView.getScrollX());
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate();
    }

    /**
     * 从左向右卷动（隐藏）
     */
    private void scrollLeft() {
        removeDirection = RemoveDirection.LEFT;
        final int delta = (screenWidth - itemView.getScrollX());
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                Math.abs(delta));
        postInvalidate();
    }

    /**
     * 根据x方向上的距离给定卷动方向
     */
    private void scrollByDistanceX() {
        if (itemView.getScrollX() >= screenWidth / 3) {
            scrollLeft();
        } else if (itemView.getScrollX() <= -screenWidth / 3) {
            scrollRight();
        } else {
            itemView.scrollTo(0, 0);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide && ((slidePosition != AdapterView.INVALID_POSITION
                && !getIncludeHeader(slidePosition)))) {
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getX();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int deltaX = downX - x;
                    downX = x;

                    itemView.scrollBy(deltaX, 0);
                    break;
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }

                    recycleVelocityTracker();
                    isSlide = false;
                    break;
            }

            return true;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();

            if (scroller.isFinished()) {
                if (mRemoveListener == null) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }

                mRemoveListener.removeItem(removeDirection, slidePosition);

                //reset the item position
                itemView.scrollTo(0, scroller.getCurrY());
            }
        }
    }

    /**
     * 增加VelocityTracker
     *
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * 移除VelocityTracker
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 获得ScrollVelocity
     *
     * @return
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return velocity;
    }

    // direction
    public enum RemoveDirection {
        RIGHT, LEFT
    }

    /**
     * item移除监听器
     */
    public interface RemoveListener {
        void removeItem(RemoveDirection direction, int position);
    }

}

