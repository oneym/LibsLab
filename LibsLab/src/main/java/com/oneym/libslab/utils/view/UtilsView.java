package com.oneym.libslab.utils.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oneym.libslab.utils.common.Log;

/**
 * @author oneym
 * @since 20151211141542
 */
public class UtilsView {

    /**
     * 在图片上面绘制一个灰色矩形的框线
     *
     * @param context 上下文
     * @param bitmap  图片
     * @param colorId 颜色资源id
     * @return 图片
     */
    public static Bitmap drawFramLine(Context context, Bitmap bitmap, int colorId) {
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(context.getResources().getColor(colorId));
        p.setStrokeWidth(3f);
        canvas.drawRect(new RectF(45, 2, bitmap.getWidth() - 48, bitmap.getHeight() - 2), p);
        return bitmap;
    }

    /**
     * 根据点在父视图组中寻找对应的子视图<br/>
     * 这个方法主要解决两个问题<br/>
     * 1、where you from<br/>
     * 2、who are you<br/>
     * 使用递归来寻找原子view，再与坐标对比得出点击的视图
     *
     * @param v    父视图组
     * @param rawx x坐标
     * @param rawy y坐标
     * @return 返回子视图，null表示在父视图组中没有找到包含该点的子视图。
     */
    public static View getParentItemChildByPoint(View v, int rawx, int rawy, float delta) {
        View child = null;
        //for (int i = 0; i < v.getChildCount(); i++) {
        Log.out(v + " instanceof ViewGroup=" + (v instanceof ViewGroup));
        //进入layout、listview内部，找到textview等原子视图，各种layout、listview都是ViewGroup的直接子类，ViewGroup是View的直接子类
        if (View.VISIBLE == v.getVisibility() && v instanceof ViewGroup) {
            ViewGroup vs = (ViewGroup) v;
            for (int i = 0; i < vs.getChildCount(); i++) {
                View v_ = getParentItemChildByPoint(vs.getChildAt(i), rawx, rawy, delta);
                Log.out(v_ + " v_ instanceof View = " + (v_ instanceof View));
                if (!(v_ instanceof ViewGroup) && v_ instanceof View)
                    return v_;
            }
        }

        Rect rect = new Rect();
        Rect p_rect = new Rect();
        v.getHitRect(p_rect);
        Log.out("v = " + v + ",p_rect.bottom=" + p_rect.bottom);
        Log.out("v.getX()=" + v.getX() + ",v.getY=" + v.getY() + ",v.getHeight()=" + v.getHeight());
        v.getGlobalVisibleRect(rect);//获得子组件在窗口世界坐标系的坐标，窗口世界就是手机屏幕
        Log.out("rect:" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom);
        //rect.top = rect.top - v.getHeight() - (int) v.getY();
        //rect.bottom = rect.bottom - v.getHeight() - (int) v.getY();
        rect.top = rect.top - (int) delta;
        rect.bottom = rect.bottom - (int) delta;
        Log.out("rect:" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom);
        Log.out("xpos=" + rawx + ",ypos=" + rawy);
        if (rect.contains(rawx, rawy)) {
            child = (View) v;
            Log.out(child);
        }
        return child;
    }

    /**
     * 在非UI线程中设置图片
     *
     * @param imageView 需要刷新的View
     * @param uri       Uri资源
     */
    public static void setImageURI(final ImageView imageView, final String uri) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    imageView.setImageURI(Uri.parse(uri));
                    Log.out("setImageURIsetImageURIsetImageURI");
                    imageView.postInvalidate();
                } catch (Exception e) {
                    Log.out(e);
                }
            }
        });
    }

    /**
     * 在非UI线程中设置图片
     *
     * @param imageView 需要刷新的View
     * @param bitmap    图片
     */
    public static void setImageBitmap(final ImageView imageView, final Bitmap bitmap) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    imageView.setImageBitmap(bitmap);
                    Log.out("setImageBitmapsetImageBitmapsetImageBitmap");
                    imageView.postInvalidate();
                } catch (Exception e) {
                    Log.out(e);
                }
            }
        });
    }
}
