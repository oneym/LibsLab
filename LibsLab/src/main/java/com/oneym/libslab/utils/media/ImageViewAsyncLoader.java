package com.oneym.libslab.utils.media;

import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.io.UtilsIO;
import com.oneym.libslab.utils.net.Download;
import com.oneym.libslab.utils.string.UtilsString;
import com.oneym.libslab.utils.view.UtilsView;

/**
 * ImageView异步加载网络图片<br/>
 * 支持加载的图片设定圆角、倒影效果
 *
 * @author oneym
 * @since 20151211145050
 */
public class ImageViewAsyncLoader {
    /**
     * 腾讯QQ
     *
     * @deprecated
     */
    public static final int QQ = 1;
    /**
     * 腾讯微信
     *
     * @deprecated
     */
    public static final int WECHAT = QQ + 1;
    /**
     * 新浪微博
     *
     * @deprecated
     */
    public static final int WB_SINA = WECHAT + 1;
    //单例
    private static ImageViewAsyncLoader instance = null;
    /**
     * 允许永久使用已经设置的参数，默认不启用
     */
    private boolean enableParameters = false;
    /**
     * 启动设置图片圆角，默认不启用
     */
    private boolean enableRoundedCorner = false;

    /**
     * 启动设置图片倒影，默认不启用
     */
    private boolean enableReflection = false;


    //构造方法
    private ImageViewAsyncLoader() {
    }

    public static ImageViewAsyncLoader getInstance() {
        if (null == instance)
            instance = new ImageViewAsyncLoader();
        return instance;
    }

    /**
     * 设置图片
     *
     * @param imageView 图片对象
     * @param url       图片网络链接地址，url中如果没有文件扩展名
     * @param home      App在SD卡上的目录,这个方法需要缓存图片到本地
     */
    public void setData(final ImageView imageView, final String url, String home) {

        if (!url.startsWith("http://")) {
            Object obj = "发现奇葩地址一枚，没有以http开头，需要咨询后台开发者:url=" + url;
            Log.out(obj);
            return;
        }

        if (UtilsString.isEmptyString(url)) {
            Log.out("参数url是null");
            return;
        }

        String filename = url.substring(url.lastIndexOf("/") + 1);
        filename = UtilsString.toString(url.hashCode()) + filename;
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!root.endsWith("/") && !home.startsWith("/"))
            root += "/";
        if (!home.endsWith("/"))
            home += "/";
        String path = root + home + filename;

        Log.out("ImageVIewAsy->path=" + path);
        if (!(path = UtilsMedia.isImageExist(path)).equals("false")) {//返回值不是false就是图片path
            Log.out("图片文件存在，不下载");
            judgeParameters(imageView, path);

        } else {
            Download.getInstance().start(url, filename, home, new Download.OnDownloadingListener() {
                @Override
                public void onMessage(int type, String msg) {
                    Log.out("type=" + type + ",msg=" + msg);
                    if (Download.OnDownloadingListener.TYPE_SUCCESS == type) {
                        judgeParameters(imageView, msg);
                    }
                }

                @Override
                public void onSeekBarChange(int all, int now) {

                }
            });
        }
    }

    /**
     * 判断是否启用了圆角、倒影、永久参数
     *
     * @param imageView 图片对象
     * @param path      图片绝对路径
     */
    private void judgeParameters(ImageView imageView, String path) {
        if (!isEnableParameters() && !isEnableRoundedCorner() && !isEnableReflection()) {
            UtilsView.setImageURI(imageView, path);
            return;
        }

        Bitmap bitmap = UtilsIO.readImage2png(path);

        if (isEnableReflection())
            bitmap = UtilsMedia.getReflectionImageWithOrigin(bitmap);

        if (isEnableRoundedCorner())
            bitmap = UtilsMedia.getRoundedCornerBitmap(imageView.getContext(), bitmap);

        if (isEnableRoundedCorner() || isEnableReflection())
            UtilsView.setImageBitmap(imageView, bitmap);

        if (!isEnableParameters()) {
            setEnableReflection(false);
            setEnableRoundedCorner(false);
        }

    }

//    /**
//     * 设置图片(这个方法只是为了设置第三方登陆的头像)<br/>
//     * 真实设置图片与调用这个方法的次数无关，只与{@link OTencent#qq}装载的次数有关
//     *
//     * @param imageView 图片对象
//     * @param home      App在SD卡上的目录,这个方法需要缓存图片到本地
//     * @param type      常量为：{@link ImageViewAsyncLoader#QQ}、{@link ImageViewAsyncLoader#WECHAT}、{@link ImageViewAsyncLoader#WB_SINA}
//     * @deprecated
//     */
//    public void setData(final ImageView imageView, final String home, int type) {
//        if (UtilsString.isEmptyString(home))
//            throw new NullPointerException("home不可以为空");
//
//        switch (type) {
//            case QQ:
//                OTencent.getIntance().setLoaded(new OTencent.QQBeanLoaded() {
//                    @Override
//                    public void loaded() {
//                        setData(imageView, OTencent.getIntance().qq.getFigureurl_qq_2(), home);
//                    }
//                });
//                break;
//            case WECHAT:
//                break;
//            case WB_SINA:
//                break;
//            default:
//                //这行log的意思是，我无法理解type
//                Log.out("小丽来碗饭，这小子饿短片了！");
//                break;
//        }
//    }

    /**
     * 启用永久使用参数
     */
    private boolean isEnableParameters() {
        return enableParameters;
    }

    /**
     * 为接下来的图片启用已设置的参数
     *
     * @param enableParameters true启用，false不启用（默认值）
     */
    public void setEnableParameters(boolean enableParameters) {
        this.enableParameters = enableParameters;
    }

    /**
     * 启用圆角参数
     */
    private boolean isEnableRoundedCorner() {
        return enableRoundedCorner;
    }

    /**
     * 设置启用圆角，如果给{@link ImageViewAsyncLoader#setEnableParameters(boolean)}设置true值的话，只会默认执行一次
     *
     * @param enableRoundedCorner true启用，false不启用（默认值）
     */
    public void setEnableRoundedCorner(boolean enableRoundedCorner) {
        this.enableRoundedCorner = enableRoundedCorner;
    }

    /**
     * 启用倒影参数
     */
    private boolean isEnableReflection() {
        return enableReflection;
    }

    /**
     * 设置启用倒影，如果给{@link ImageViewAsyncLoader#setEnableParameters(boolean)}设置true值的话，只会默认执行一次
     *
     * @param enableReflection true启用，false不启用（默认值）
     */
    public void setEnableReflection(boolean enableReflection) {
        this.enableReflection = enableReflection;
    }

}
