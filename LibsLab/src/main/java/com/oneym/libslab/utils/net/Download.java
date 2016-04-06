package com.oneym.libslab.utils.net;

import android.content.Context;
import android.os.Environment;

import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.common.Utils;
import com.oneym.libslab.utils.io.UtilsIO;
import com.oneym.libslab.utils.media.UtilsMedia;
import com.oneym.libslab.utils.string.UtilsString;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程同步下载方法，文件存储部分有同步锁
 *
 * @author oneym oneym@sina.cn
 * @since 20151210115512
 */
public class Download {

    //存储类型常量
    private final static int STORAGER_FOREVER = 1;
    private final static int STORAGER_TEMPORARY = 2;
    //出现异常的后缀名，这些后缀名是在开发过程中，自动解析出来的，在此纠正。包含在这些异常扩展名里面，系统一律不给新的扩展名，意味着在开发的时候发现这种情况就在文件名后面直接加上扩展名
    //                          服务器动态返回的json文件会得到text/html格式
    private final static String error_suffix[] = {".html; charset=utf-8"};

    //单例
    private static Download instance = null;
    //线程池   20151215155142
    private ExecutorService pool = null;

    //构造方法
    private Download() {
    }

    /**
     * @return 单例
     */
    public static Download getInstance() {
        if (null == instance)
            instance = new Download();
        return instance;
    }

    /**
     * 开始下载<br/>
     * 文件存在，会自动删除然后下载
     *
     * @param distinct 这是用来与重载的公共方法做区分的区分，没有其他意义
     * @param url      资源下载链接
     * @param savepath 文件存储目录
     * @param filename 指定保存时的文件名，不要以“/”斜杠开头；最好从url中截取或者文件名能够被记录，然后传给这个参数;
     * @param listener 监听接口
     */
    private void start(final int distinct, final String url, final String savepath, final String filename, final OnDownloadingListener listener) {
        try {
            //初始化线程池为可缓存的线程池，智能添加和删除，这个初始化是多线程的关键部分
            if (null == pool)
                pool = Executors.newCachedThreadPool();
            //线程池维护单条线程
//            if (null == pool)
//                pool = Executors.newSingleThreadExecutor();

            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.out("startAt:Thread.currentThread().getId()=" + Thread.currentThread().getId());
                    //开始下载
                    _download(url, savepath, filename, listener);
                }
            });

        } catch (Exception e) {
            Log.out(e);
        } finally {
            Utils.countActiveThread();
            Log.out("_____________________");
        }
    }

    /**
     * 下载好的【永久】文件保存在系统APP缓存目录下
     *
     * @param context  上下文;不可为null
     * @param url      资源下载链接
     * @param filename 指定保存时的文件名，不要以“/”斜杠开头；最好从url中截取或者文件名能够被记录，然后传给这个参数;
     * @param listener 监听接口
     */
    public void start(Context context, String url, String filename, OnDownloadingListener listener) {
        Map map = getData(context, url, filename, listener);
        start(0, map.get("url").toString(), map.get("savepath").toString(), map.get("filename").toString(), listener);
    }

    /**
     * 下载好的【临时】文件保存在{@code /SDHome/} 目录下
     *
     * @param url      资源下载链接
     * @param filename 指定保存时的文件名，不要以“/”斜杠开头；最好从url中截取或者文件名能够被记录，然后传给这个参数;
     * @param SDHome   APP在SD上建立的Home目录，默认值为oneym，不可为null;不要以“/”斜杠结尾,可以是多级目录（e.g: oneym/img）
     * @param listener 监听接口
     */
    public void start(String url, String filename, String SDHome, OnDownloadingListener listener) {
        Map map = getData(url, filename, SDHome, listener);
        start(0, map.get("url").toString(), map.get("savepath").toString(), map.get("filename").toString(), listener);
    }

    //核心下载方法(网络链接部分【共两部分】)
    private void _download(final String url, final String savepath, final String filename, final OnDownloadingListener listener) {
        //数据输入流
        DataInputStream dis = null;

        try {
            if (!UtilsIO.isDiskCanReadAndWrite(savepath)) {
                Log.out("SD卡不可用");
                if (null != listener)
                    listener.onMessage(OnDownloadingListener.TYPE_OTHER, "SD卡不可用");
                return; //当SD不可用是就直接停止下载操作
            }

            Log.out("下载开始" + Thread.currentThread().getId());
            if (null != listener)
                listener.onMessage(OnDownloadingListener.TYPE_START, "下载开始");

            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
            HttpGetHC4 get = new HttpGetHC4(url);
            Log.out("Download->url=" + url);
            //get.getHttpConnectionManager().getParams().setConnectionTimeout(60000);

            HttpResponse response = client.execute(get);
            Log.out("response.getStatusLine().getStatusCode()=" + response.getStatusLine().getStatusCode());
            if (HttpURLConnection.HTTP_OK == response.getStatusLine().getStatusCode()) {
                String path = savepath + filename;
                String typeValue = response.getEntity().getContentType().getValue();
                String suffix = "." + typeValue.substring(typeValue.lastIndexOf("/") + 1).toLowerCase();
                String temp_suffix = "";
                if (!(temp_suffix = UtilsMedia.isImage(typeValue)).equals("false"))//判断文件是图片类型
                    suffix = temp_suffix.toLowerCase();

                Log.out("suffix=" + suffix);

                if (!UtilsMedia.isContainsSuffix(filename, suffix)) {
                    for (int i = 0; i < error_suffix.length; i++)
                        if (suffix.equals(error_suffix[i]))
                            suffix = "false";

                    if (!suffix.equals("false"))
                        path = path + suffix;
                }

                Header[] header = response.getAllHeaders();
                for (int k = 0; k < header.length; k++) {
                    Log.out(header[k].getName() + ":" + header[k].getValue());
                }

                int lenAll = (int) response.getEntity().getContentLength();
                Log.out("lenAll=" + lenAll);
                Log.out("url=" + url);
                Log.out("isChunked()=" + response.getEntity().isChunked());

                dis = new DataInputStream(response.getEntity().getContent());

                //进入第二部分
                saveInputStream2File(listener, dis, response.getEntity().isChunked(), path, lenAll);

            } else {
                Log.out("response.getStatusLine().getStatusCode():" + response.getStatusLine().getStatusCode());
                if (null != listener)
                    listener.onMessage(OnDownloadingListener.TYPE_OTHER, "服务器错误，code=" + response.getStatusLine().getStatusCode());
            }
        } catch (HttpHostConnectException e) {
            if (null != listener)
                listener.onMessage(OnDownloadingListener.TYPE_ERROR, "与服务器链接超时,请检查你的网络");
            Log.out(e);
        } catch (IOException e) {
            if (null != listener)
                listener.onMessage(OnDownloadingListener.TYPE_ERROR, "下载出错,请检查你的网络");
            Log.out(e);
        } finally {
            try {
                if (dis != null)
                    dis.close();
                Object obj = "现在我做了关闭流的操作";
                Log.out(obj);

                Utils.countActiveThread();
            } catch (IOException e) {
                Log.out("下载类-->流关闭异常");
                Log.out(e);
            }
        }
    }

    //核心下载方法(文件存储部分【共两部分】)
    private synchronized void saveInputStream2File(OnDownloadingListener listener, DataInputStream dis, boolean isChunked, String path, int lenAll) throws IOException {
        File file = new File(path);
        file.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(file);

        byte[] data = new byte[1024];
        int lenTemp = -1;
        int len = 0;

        while ((lenTemp = dis.read(data)) > 0) {
            len += lenTemp;
            fos.write(data, 0, lenTemp);

            if (null != listener)
                listener.onSeekBarChange(lenAll, len);
        }

        if (lenAll == len || isChunked) {
            Log.out("~~~~~Downloaded: " + file.getName());
            Log.out("下载完成" + Thread.currentThread().getId());
            if (null != listener)
                listener.onMessage(OnDownloadingListener.TYPE_SUCCESS, file.getAbsolutePath());
        } else {
            Log.landmark();
            Log.out(lenAll + ":下载错误，大小不一致:" + len);
            Log.landmark();
            if (null != listener)
                listener.onMessage(OnDownloadingListener.TYPE_ERROR, "下载出错");
        }
    }


    /**
     * 设置数据<br/>
     * 下载好的文件保存在{@code /SDHome/} 目录下
     *
     * @param type     资源存储类型,可以设置为{@link Download#STORAGER_FOREVER}，{@link Download#STORAGER_TEMPORARY}
     * @param context  上下文;在type为{@link Download#STORAGER_FOREVER}时，不可为null,其他类型允许为null
     * @param url      资源下载链接
     * @param filename 指定保存时的文件名，不要以“/”斜杠开头；最好从url中截取或者文件名能够被记录，然后传给这个参数;
     * @param SDHome   APP在SD上建立的Home目录，默认值为oneym，在type为{@link Download#STORAGER_TEMPORARY}时，
     *                 不可为null,其他类型允许为null;不要以“/”斜杠结尾,可以是多级目录（e.g: oneym/img）
     * @param listener 监听接口
     * @return 返回一个Map集合，也可能是null，键是url、savepath、filename只有这三个
     */
    private Map getData(final int type, final Context context, final String url, final String filename, String SDHome, final OnDownloadingListener listener) {
        if (UtilsString.isEmptyString(url) || UtilsString.isEmptyString(filename)) {
            Object obj = "参数为空";
            Log.out(obj);
            return null;
        }

        if (filename.startsWith("/")) {
            Log.out("filename 不要以“/”斜杠开头");
            return null;
        }

        Map map = null;

        //这个switch中的方法只是用来拼接url和savePathWithFileFullName的字符串值
        switch (type) {
            case STORAGER_FOREVER:
                if (null == context) {
                    Object obj = "context为空，请检查";
                    Log.out(obj);
                    return null;
                }

                String path = context.getExternalCacheDir().getAbsolutePath();
                if (UtilsString.isEmptyString(path)) {
                    Log.out("SD卡不可用");
                    if (null != listener)
                        listener.onMessage(OnDownloadingListener.TYPE_OTHER, "SD卡不可用");
                    return null; //当SD不可用是就直接停止下载操作
                }

                if (!path.endsWith("/"))
                    path = path + "/";

                map = new HashMap();
                map.put("url", url);
                map.put("savepath", path);
                map.put("filename", filename);

                break;
            case STORAGER_TEMPORARY:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//挂载了SD卡
                    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                    if (!UtilsString.isEmptyString(root)) {

                        if (!root.endsWith("/") && !SDHome.startsWith("/"))
                            root = root + "/";

                        if (UtilsString.isEmptyString(SDHome))
                            SDHome = "/oneym";

                        if (!SDHome.endsWith("/"))
                            SDHome = SDHome + "/";

                        root = root + SDHome;

                        map = new HashMap();
                        map.put("url", url);
                        map.put("savepath", root);
                        map.put("filename", filename);

                    } else {
                        Object obj = "在挂载了SD卡后出现获取到的目录为空";
                        Log.out(obj);
                        if (null == listener)
                            listener.onMessage(OnDownloadingListener.TYPE_OTHER, "在挂载了SD卡后出现获取到的目录为空");
                    }
                } else {
                    Object obj = "SD卡处于未挂载状态，可能处于共享或者移除状态";
                    Log.out(obj);
                    if (null == listener)
                        listener.onMessage(OnDownloadingListener.TYPE_OTHER, "SD卡处于未挂载状态，可能处于共享或者移除状态");
                }
                break;
            default:
                Log.out("非法的type类型");
                break;
        }
        return map;
    }

    /**
     * 设置数据<br/>
     * 下载好的【永久】文件保存在系统APP缓存目录下
     *
     * @param context  上下文;不可为null
     * @param url      资源下载链接
     * @param filename 指定保存时的文件名，不要以“/”斜杠开头；最好从url中截取或者文件名能够被记录，然后传给这个参数;
     * @param listener 监听接口
     * @return 返回一个Map集合，也可能是null，键是url、savepath、filename只有这三个
     */
    private Map getData(Context context, final String url, final String filename, final OnDownloadingListener listener) {
        return getData(STORAGER_FOREVER, context, url, filename, null, listener);
    }

    /**
     * 设置数据<br/>
     * 下载好的【临时】文件保存在{@code /SDHome/} 目录下
     *
     * @param url      资源下载链接
     * @param filename 指定保存时的文件名，不要以“/”斜杠开头；最好从url中截取或者文件名能够被记录，然后传给这个参数;
     * @param SDHome   APP在SD上建立的Home目录，默认值为oneym，不可为null;不要以“/”斜杠结尾,可以是多级目录（e.g: oneym/img）
     * @param listener 监听接口
     * @return 返回一个Map集合，也可能是null，键是url、savepath、filename只有这三个
     */
    private Map getData(final String url, final String filename, String SDHome, final OnDownloadingListener listener) {
        return getData(STORAGER_TEMPORARY, null, url, filename, SDHome, listener);
    }

    /**
     * 关闭线程池
     */
    public void closePool() {
        if (null != pool)
            pool.shutdown();
    }

    /**
     * 下载监听接口<br/>
     * 当且仅当type == {@link Download.OnDownloadingListener#TYPE_SUCCESS}
     * 时为下载完成,此时msg为完整的下载路径
     *
     * @author oneym
     */
    public interface OnDownloadingListener {
        /**
         * 下载开始
         */
        int TYPE_START = 1;
        /**
         * 下载完成
         */
        int TYPE_SUCCESS = 2;
        /**
         * 下载异常
         */
        int TYPE_ERROR = 3;
        /**
         * 其他问题
         */
        int TYPE_OTHER = 4;

        /**
         * 消息监听
         *
         * @param type 消息类型,该类型仅有：{@link Download.OnDownloadingListener#TYPE_START}
         *             、{@link Download.OnDownloadingListener#TYPE_SUCCESS}
         *             、{@link Download.OnDownloadingListener#TYPE_ERROR}
         *             、{@link Download.OnDownloadingListener#TYPE_OTHER}
         * @param msg  消息
         */
        void onMessage(final int type, final String msg);

        /**
         * 下载进度条<br/>
         * 【注意】当参数all为-1（负一）时,下载的是chunked数据大小未知。本下载方式暂时不支持chunked下载
         *
         * @param all 总长度
         * @param now 当前已下载的大小
         */
        void onSeekBarChange(final int all, final int now);
    }

    /**
     * 没有进度条的下载监听接口<br/>
     * 当且仅当type == {@link Download.OnDownloadingListener#TYPE_SUCCESS}
     * 时为下载完成,此时msg为完整的下载路径
     */
    public abstract class OnDownloadedListenner implements OnDownloadingListener {
        @Override//在这个抽象方法中实现了onSeekBarChange方法，那么在实现类中就不会出现这个方法了，但是这个方法依然可以被重写
        public void onSeekBarChange(int all, int now) {

        }
    }
}
