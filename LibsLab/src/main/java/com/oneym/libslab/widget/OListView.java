package com.oneym.libslab.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oneym.libslab.R;
import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.string.UtilsString;
import com.oneym.libslab.utils.view.UtilsView;

/**
 * 自定义listview</br>
 * 使用方法：</br>
 * 1、使用本类在xml中布局</br>
 * 2、需要开发者自己写adapter</br>
 * 3、在刷新完成后需要调用{@link OListView#closeHeaderAndFoot()}来关闭头部或脚部的刷新loading(需要实现{@link com.oneym.libslab.widget.OListView.OnListViewItemRefresh})</br>
 * 4、点击事件实现{@link com.oneym.libslab.widget.OListView.OnListViewItemClickListener}方法（支持布局穿透），使用别的监听可能达不到效果</br>
 *
 * @author oneym oneym@sina.cn
 * @since 20151208135329
 */
public class OListView extends ListView implements AbsListView.OnScrollListener {

    public static final int REFRESH_NULL = 1;
    public static final int PREPARE_REFRESH = REFRESH_NULL + 1;
    public static final int RELEASE_REFRESH = PREPARE_REFRESH + 1;
    public static final int REFRESHING = RELEASE_REFRESH + 1;
    public static final int REFRESH_END = REFRESHING + 1;
    /**
     * 下拉刷新
     */
    public static final int UP_REFRESH = REFRESH_END + 1;
    /**
     * 上拉加载
     */
    public static final int DOWN_REFRESH = UP_REFRESH + 1;
    //1秒钟超过150像素即判定为非点击,VelocityTracker返回的是一个估值，估值与这个(SNAP_VELOCITY)值比较
    private static final int SNAP_VELOCITY = 150;
    //触摸的坐标
    private int xpos = -1;
    private int ypos = -1;
    //item对象
    private ViewGroup item = null;
    //当前选中的item
    private View touchedItem = null;
    //本文件自定义的监听器
    private OnListViewItemClickListener listener = null;
    //item位置
    private int position = -1;
    //发生移动的容忍度
    private int slop = -1;
    //触摸事件速率追踪
    private VelocityTracker velocityTracker = null;
    //刷新的扩展视图,就是头部和脚部
    private View headerView = null;
    private View footView = null;
    private ImageView headerArrorw = null;
    private ProgressBar headerProgressbar = null;
    private TextView headerText = null;
    private ProgressBar footProgressbar = null;
    private TextView footText = null;
    //刷新操作的箭头动画
    private Animation upAnimation = null;   //由下向上转动
    private Animation downAnimation = null; //由上向下转动
    //是头部
    private int isHeader = REFRESH_NULL;
    private int headerHeight = -1;
    private int footHeight = -1;
    private int refreshState = REFRESH_NULL;
    private boolean can_down_refresh = false;
    //自定义刷新回调方法
    private OnListViewItemRefresh refresh = null;


    public OListView(Context context) {
        this(context, null);
    }

    public OListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(this);
        slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initHeaderAndFoot();
        initAnimation();

        if ((getLastVisiblePosition() - getFirstVisiblePosition()) <= 0)
            refresh_header();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                xpos = (int) ev.getX();
                ypos = (int) ev.getY();
                position = pointToPosition(xpos, ypos);
                if (AdapterView.INVALID_POSITION == position)//位置是非法
                    return super.dispatchTouchEvent(ev);

                item = (ViewGroup) getChildAt(position - getFirstVisiblePosition());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.out("MotionEvent.ACTION_DOWN");
                if (null != item) {
                    xpos = (int) ev.getRawX();
                    ypos = (int) ev.getRawY();
                    touchedItem = UtilsView.getParentItemChildByPoint(item, xpos, ypos);
                    return null != touchedItem;//返回true后面的事件不经过这个方法
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        addVelocityTracker(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //在按下和抬起之间发生了移动就取消这次点击事件
                int velocity[] = getScrollVelocity();
                if (Math.abs(velocity[0]) > SNAP_VELOCITY
                        || Math.abs(velocity[1]) > SNAP_VELOCITY
                        || Math.abs(ev.getRawX() - xpos) > slop
                        || Math.abs(ev.getRawY() - ypos) > slop) { //超出了触摸容忍度，判定不是触摸，在此判定为不是点击事件

                    item = null;
                    touchedItem = null; //判定是点击事件的重要条件
                }

                if (null != refresh) {

                    Log.out("isHeader=" + isHeader + ",headerHeight=" + headerHeight + ",footHeight=" + footHeight);
                    //下拉上推刷新的距离计算
                    // 移动中的y - 按下的y = 间距.
                    int delta = (int) (ev.getRawY() - ypos) / 2;
                    // 头布局的高度 + 间距 = paddingTop
                    int paddingTop = headerHeight + delta;
                    //  脚布局的高度 - 间距 = paddingBottom
                    //int paddingBottom = footHeight - delta;//这个间距和下拉的方向相反，所以取负数

                    // 当拉开的距离大零时
                    if (UP_REFRESH == isHeader && headerHeight < paddingTop) {//是头部
                        if (paddingTop > 0 && refreshState == PREPARE_REFRESH) { // 完全显示了.
                            Log.out("松开刷新");
                            headerText.setText("松开刷新");
                            headerArrorw.clearAnimation();
                            headerArrorw.setAnimation(upAnimation);
                            refreshState = RELEASE_REFRESH;
                            //在抬起事件中联网刷新数据了
                        } else if (paddingTop < 0 && refreshState == REFRESH_NULL) { // 没有显示完全
                            Log.out("下拉刷新");
                            headerText.setText("下拉刷新");
                            headerArrorw.clearAnimation();
                            headerArrorw.setAnimation(downAnimation);
                            refreshState = PREPARE_REFRESH;
                        }
                        // 下拉头布局
                        headerView.setPadding(0, paddingTop, 0, 0);
                        return true;
                    }
//                    else if (DOWN_REFRESH == isHeader && footHeight < paddingBottom) {//是尾部
//                        if (paddingBottom > 0 && refreshState == PREPARE_REFRESH) { //完全显示了
//                            Log.out("松开刷新");
//                            footText.setText("松开刷新");
//                            refreshState = REFRESHING;
//                            //footView.setPadding(0, 0, 0, 0);
//                            //需要联网刷新数据了
//                        } else if (paddingBottom < 0 && refreshState == REFRESH_NULL) { //没有完全显示
//                            Log.out("上拉刷新");
//                            footText.setText("上拉刷新");
//                            refreshState = PREPARE_REFRESH;
//                        }
//                        footView.setPadding(0, paddingBottom, 0, 0);
//                        return true;
//                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (null != touchedItem) {

                    if (null == listener)
                        throw new NullPointerException("请先使用setOnListViewItemClickListener方法设置监听器。");

                    //视图没有显示
                    if (View.INVISIBLE == touchedItem.getVisibility() || View.GONE == touchedItem.getVisibility())
                        return super.onTouchEvent(ev);

                    //监听事件触发
                    listener.onClick(touchedItem, position);

                    Object obj = "触发点击事件。" + UtilsString.toString(touchedItem);
                    Log.out(obj);

                    //事件执行完成后，清空事件判断依据
                    item = null;
                    touchedItem = null;
                }
                recycleVelocityTracker();

                if (null != refresh) {

                    if (RELEASE_REFRESH == refreshState) {
                        refresh_header();
                    } else if (REFRESH_NULL == refreshState) {
                        headerView.setPadding(0, headerHeight, 0, 0);
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refresh_header() {

        headerView.setPadding(0, 0, 0, 0);
        headerText.setText("正在刷新...");
        headerArrorw.clearAnimation();
        headerArrorw.setVisibility(GONE);
        headerProgressbar.setVisibility(VISIBLE);

        Log.out("正在刷新...");

        if (null != refresh)
            //开始加载网络数据
            refresh.onHeader();
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setOnListViewItemClickListener(OnListViewItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置刷新回调
     *
     * @param refresh 回调函数
     */
    public void setOnListViewItemRefresh(OnListViewItemRefresh refresh) {
        this.refresh = refresh;
    }

    //添加触摸事件的速率追踪
    private void addVelocityTracker(MotionEvent ev) {
        if (null == velocityTracker)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(ev);
    }

    //回收速率追踪
    private void recycleVelocityTracker() {
        if (null != velocityTracker) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    //获得一秒钟的速率信息
    private int[] getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity[] = new int[2];
        velocity[0] = (int) velocityTracker.getXVelocity();
        velocity[1] = (int) velocityTracker.getYVelocity();
        return velocity;
    }

    //初始化头部和尾部刷新的对象
    private void initHeaderAndFoot() {
        headerView = View.inflate(getContext(), R.layout.olistview_refresh_headerview, null);
        headerArrorw = (ImageView) headerView.findViewById(R.id.headerarrow);
        headerProgressbar = (ProgressBar) headerView.findViewById(R.id.headerbar);
        headerText = (TextView) headerView.findViewById(R.id.headertv);
        //让系统测量头布局的高度
        headerView.measure(0, 0);//设置成0，系统回自动设置当前的真实尺度的
        headerHeight = -headerView.getMeasuredHeight();
        headerView.setPadding(0, headerHeight, 0, 0);
        //添加头布局到头部
        addHeaderView(headerView);

        footView = View.inflate(getContext(), R.layout.olistview_refresh_footview, null);
        footText = (TextView) footView.findViewById(R.id.foottv);
        footProgressbar = (ProgressBar) footView.findViewById(R.id.footbar);
        //让系统测量脚布局的高度
        footView.measure(0, 0);//设置成0，系统回自动设置当前的真实尺度的
        footHeight = -footView.getMeasuredHeight();
        footView.setPadding(0, footHeight, 0, 0);
        //添加脚布局到脚部
        addFooterView(footView);
    }

    //初始化箭头动画
    private void initAnimation() {
        upAnimation = new RotateAnimation(0f, -180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);//动画完成后定在最后的位置上

        downAnimation = new RotateAnimation(-180f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);//动画完成后定在最后的位置上

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (null != refresh) {
            if (SCROLL_STATE_FLING == scrollState || SCROLL_STATE_IDLE == scrollState) {
                Log.out("isHeader=" + isHeader + "refreshState=" + refreshState);
                can_down_refresh = true;
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != refresh) {

            if (can_down_refresh && DOWN_REFRESH == isHeader && REFRESH_NULL == refreshState) {
                can_down_refresh = false;
                isHeader = REFRESH_NULL;
                refreshState = REFRESH_END;
                footView.setPadding(0, 0, 0, 0);
                footText.setText("加载更多数据...");
                setSelection(getCount());
                Log.out("加载更多数据");
                refresh.onFoot();
            }

            if (visibleItemCount > 0 && refreshState == REFRESH_NULL) {
                if (totalItemCount - 1 == getLastVisiblePosition()) {
                    isHeader = DOWN_REFRESH;
                } else if (0 == getFirstVisiblePosition()) {
                    isHeader = UP_REFRESH;
                } else {
                    isHeader = REFRESH_END;
                }
            }
        }
    }

    /**
     * 关闭头部和脚部
     */
    public void closeHeaderAndFoot() {
        isHeader = REFRESH_NULL;
        refreshState = REFRESH_NULL;

        headerView.measure(0, 0);
        headerProgressbar.setVisibility(GONE);
        headerArrorw.setVisibility(VISIBLE);
        headerView.setPadding(0, headerHeight, 0, 0);

        footView.measure(0, 0);
        footView.setPadding(0, footHeight, 0, 0);

        Object obj = "closeHeaderAndFoot->isHeader=" + isHeader + ",headerHeight=" + headerHeight + ",footHeight=" + footHeight;
        Log.out(obj);
    }

    /**
     * listView item的点击监听接口
     *
     * @author oneym oneym@sina.cn
     */
    public interface OnListViewItemClickListener {
        void onClick(View v, int position);
    }


    /**
     * listView 刷新监听接口
     *
     * @author oneym oneym@sina.cn
     */
    public interface OnListViewItemRefresh {
        /**
         * 刷新完成后需要调用{@link OListView#closeHeaderAndFoot()}
         */
        void onHeader();

        /**
         * 刷新完成后需要调用{@link OListView#closeHeaderAndFoot()}
         */
        void onFoot();
    }

}
