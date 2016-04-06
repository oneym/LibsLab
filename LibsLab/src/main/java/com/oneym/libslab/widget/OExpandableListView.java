package com.oneym.libslab.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.oneym.libslab.utils.common.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义可展开listview</br>
 * 使用方法：</br>
 * 1、使用本类在xml中布局</br>
 * 2、调用{@link OExpandableListView#load(List, List, OnGetViewListener)}方法把数据装载进来</br>
 * 3、实现{@link com.oneym.libslab.widget.OExpandableListView.OnGetViewListener}，这个listener就是adapter</br>
 * 4、监听子列表需要实现{@link android.widget.ExpandableListView.OnChildClickListener}</br>
 *
 * @author oneym
 * @since 20160127134527
 */
public class OExpandableListView extends ExpandableListView {

    //定义分组名称
    private List<String[]> groups;
    //定义子项名称
    private List<List<String[]>> children;
    private OnGetViewListener listener;

    public OExpandableListView(Context context) {
        this(context, null);
    }

    public OExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public OExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        groups = new ArrayList<String[]>();
        children = new ArrayList<List<String[]>>();
        setAdapter(new Adapter());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void load(@NonNull List<String[]> groups, @NonNull List<List<String[]>> children, @NonNull OnGetViewListener listener) {
        if (null == listener)
            throw new IllegalArgumentException("listener不可以为null");
        if (null == groups || null == children || groups.size() <= 0 || children.size() <= 0)
            throw new IllegalArgumentException("groups或者children参数为空");
        this.listener = listener;
        this.groups = groups;
        this.children = children;
    }

    /**
     * 获得自定视图的回调
     *
     * @author oneym
     */
    public interface OnGetViewListener {
        /**
         * 获得GroupView
         *
         * @return view
         */
        View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent, List<String[]> groups);

        /**
         * 获得ChildView
         *
         * @return view
         */
        View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent, List<List<String[]>> children);
    }

    private class Adapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Log.out("children.get(groupPosition).size()=" + children.get(groupPosition).size());
            return children.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            //返回的是一个String[]
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            Log.out("this is isChildSelectable");
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Log.out("this is getGroupView, isExpanded= " + isExpanded);
            if (null == listener)
                throw new IllegalArgumentException("listener不可以为null");
            return listener.getGroupView(groupPosition, isExpanded, convertView, parent, groups);
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Log.out("this is getChildView");
            if (null == listener)
                throw new IllegalArgumentException("listener不可以为null");
            return listener.getChildView(groupPosition, childPosition, isLastChild, convertView, parent, children);
        }
    }
}
