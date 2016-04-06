package com.oneym.libslab.utils;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.oneym.libslab.utils.common.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 淡入淡出动画效果</br>
 * 使用方法：</br>
 * 1、调用{@link AnimationUtil#getInstance()#AlphaAnimation(View, float, float, long, Handler)}方法，
 * 添加一个动画（每次执行需要重新添加），在Handler中需要处理动画的【见demo】</br>
 * 2、调用{@link AnimationUtil#getInstance()#exit()}方法对出所有动画</br>
 *
 * @author oneym oneym@sina.cn
 * @since 20151125101432
 */
public class AnimationUtil {
    private static AnimationUtil instance = null;
    private Timer timer;
    private TimerTask timerTask;
    private Map<STATUS, InnerPassBean> beans = new ConcurrentHashMap<STATUS, InnerPassBean>();

    private AnimationUtil() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.out("AnimationUtil->run():" + getStatu(STATUS.ALPHA_START) + ",beans.size()=" + beans.size());
                Iterator i = beans.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry) i.next();
                    STATUS key = (STATUS) entry.getKey();
                    final InnerPassBean bean = (InnerPassBean) entry.getValue();

                    if (null == bean)
                        throw new NullPointerException("bean is null.");

                    switch (bean.statu) {
                        case ALPHA_START:
                            final AlphaAnimation alphaAnimation = new AlphaAnimation(bean.getArg1(), bean.getArg2());
                            alphaAnimation.setDuration(bean.getDuration());
//                            Message msg = bean.getHandler().obtainMessage();
//                            msg.what = STATUS.ALPHA_START.ordinal();
//                            msg.obj = alphaAnimation;
//                            bean.getHandler().sendMessage(msg);
                            bean.getV().post(new Runnable() {
                                @Override
                                public void run() {
                                    bean.getV().setAnimation(alphaAnimation);
                                }
                            });
                            bean.statu = STATUS.ALPHA_STARTING;
                            break;
                        case ALPHA_STARTING:
                            if (bean.getV().getAlpha() != 1f)
                                return;
                            bean.statu = STATUS.ALPHA_END;
                            break;
                        case ALPHA_END:
                            bean.statu = STATUS.NULL;
                            //bean.getHandler().sendEmptyMessage(STATUS.ALPHA_END.ordinal());
                            Log.out("动画结束了。");
                            bean.getV().post(new Runnable() {
                                @Override
                                public void run() {
                                    bean.getV().setVisibility(View.INVISIBLE);
                                }
                            });
                            break;
                        case NULL:
                            if (beans.containsKey(key)) {
                                beans.remove(key);
                                Log.out("the key: \'" + key + "\' was removed.");
                            }
                            break;
                    }
                }
            }
        };
        //每秒检测一次
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    /**
     * 单例
     *
     * @return AnimationUtil对象
     */
    public static AnimationUtil getInstance() {
        if (null == instance)
            instance = new AnimationUtil();
        return instance;
    }

    /**
     * 添加一个淡入/淡出动画效果
     *
     * @param v         添加效果的视图对象
     * @param fromAlpha 初始透明度
     * @param toAlpha   最终透明度
     * @param duration  动画播放时长
     */
    //@param mHandler  Handler回调
    public void AlphaAnimation(View v, float fromAlpha, float toAlpha, long duration) {
        if (!(0f == toAlpha || 1f == toAlpha)) {
            Log.out("toAlpha value can only use float type 0 or 1.");
            return;
        }

        if (null == v)
            throw new NullPointerException("v is null.");

        InnerPassBean bean = new InnerPassBean();
        bean.setInnerPass(v, fromAlpha, toAlpha, duration, STATUS.ALPHA_START, null);
        beans.put(STATUS.ALPHA_START, bean);
    }

    /**
     * 根据key值获取对应动画的状态
     *
     * @param key 键
     * @return 当前动画的状态
     */
    public STATUS getStatu(STATUS key) {
        STATUS sta = STATUS.NULL;
        if (!beans.isEmpty())
            sta = beans.get(key).getStatu();
        return sta;
    }

    /**
     * 退出所有动画
     */
    public void exit() {
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        instance = null;
    }

    public enum STATUS {
        NULL,               //无状态
        ALPHA_START,        //开始渐变
        ALPHA_STARTING,     //渐变中
        ALPHA_END,          //渐变结束
    }

    //本类数据传输使用的bean
    private class InnerPassBean {
        private View v;
        private float arg1;
        private float arg2;
        private long duration;
        private STATUS statu;
        private Handler handler;


        private void clean() {
            v = null;
            arg1 = -1;
            arg2 = -1;
            statu = STATUS.NULL;
            handler = null;
        }

        public void setInnerPass(View v, float arg1, float arg2, long duration, STATUS statu, Handler handler) {
            clean();

            setV(v);
            setArg1(arg1);
            setArg2(arg2);
            setDuration(duration);
            setStatu(statu);
            setHandler(handler);
        }

        public View getV() {
            return v;
        }

        private void setV(View v) {
            this.v = v;
        }

        public float getArg1() {
            return arg1;
        }

        private void setArg1(float arg1) {
            this.arg1 = arg1;
        }

        public float getArg2() {
            return arg2;
        }

        private void setArg2(float arg2) {
            this.arg2 = arg2;
        }

        public STATUS getStatu() {
            return statu;
        }

        private void setStatu(STATUS statu) {
            this.statu = statu;
        }

        public Handler getHandler() {
            return handler;
        }

        private void setHandler(Handler handler) {
            this.handler = handler;
        }

        public long getDuration() {
            return duration;
        }

        private void setDuration(long duration) {
            this.duration = duration;
        }

        @Override
        public String toString() {
            String str = "[arg1=" + arg1 + ",arg2=" + arg2 + ",tag=" + statu.toString() + "]";
            return str;
        }
    }

}
