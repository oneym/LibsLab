package com.oneym.libslab.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.Toast;

import com.oneym.libslab.utils.common.Log;
import com.oneym.libslab.utils.common.Utils;
import com.oneym.libslab.utils.string.UtilsString;

import org.apache.http.HttpResponse;
import org.apache.http.impl.io.ChunkedInputStreamHC4;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author oneym
 * @since 20151212113706
 */
public class UtilsNet {
    /**
     * 获得ChunkedInputStream,用来解决header中包含Transfer-Encoding: chunked
     *
     * @param response HttpResponse对象
     * @return InputStream
     * @deprecated
     */
    public static InputStream getHttpResponseContent(final HttpResponse response) {
        SessionInputBufferImpl sibi = null;
        try {
            HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
            sibi = new SessionInputBufferImpl(metrics, 1024);
            sibi.bind(response.getEntity().getContent());
            ChunkedInputStreamHC4 hc4 = new ChunkedInputStreamHC4(sibi);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ChunkedInputStreamHC4(sibi);
    }

    /**
     * 拼接url的json参数<br/>
     * 参数后面加"&"符号
     *
     * @param json JSON数据
     * @param url  包含"?"号的url，"?"号之后的action已经加上了，只需要添加参数;<br/>
     *             如:http://ip:port/api.php?action=dosomething&（有action的），或者http://ip:port/api.php?(没有action的)<br/>
     * @param maps K-V对，K是url中添加参数的参数名，V是需要映射json值的参数名，K和V都是String类型的
     * @return {@link String} url
     */
    public static String linkUrl(final JSONObject json, String url, final Map maps) {
        try {
            if (null != maps && maps.size() > 0) {
                boolean flag = true;
                Collection collection = maps.values();
                Iterator i = collection.iterator();
                while (i.hasNext()) {
                    //不包含给定参数
                    if (!json.has(UtilsString.toString(i.next()))) {
                        flag = false;
                        break;
                    }
                }

                Log.out("UtilsNet->linkUrl->url(orgin): " + url);

                //这里只判断了一些基本信息，json中包含这些信息就表示是用户的详细信息
                if (flag) {
                    for (Object key : maps.keySet()) {
                        String value = UtilsString.toString(json.get(UtilsString.toString(maps.get(key))));
                        url += UtilsString.toString(key) + "=" + value + "&";
                    }
                    Log.out("UtilsNet->linkUrl->url: " + url);
                    if (url.endsWith("&"))
                        url = url.substring(0, url.length() - 1);
                } else {
                    throw new IllegalArgumentException("\"JSON中不完全包含所需的目标参数，请检查传入的参数\"");
                }
            }
        } catch (JSONException e) {
            Log.out(e);
        }
        return url;
    }

    /**
     * 判断字符串是url
     *
     * @param str
     * @return true是url，false不是url
     */
    public static boolean judgeIsUrl(Context context, String str) {
        if (null == context || UtilsString.isEmptyString(str)) {
            Log.out("参数为空(context||str)");
            return false;
        }
        TextView text = new TextView(context);
        text.setText(str);
        return judgeIsUrl(text);
    }

    /**
     * 判断TextView中有url
     *
     * @param textView TextView
     * @return true有url，false没有url
     */
    public static boolean judgeIsUrl(TextView textView) {
        if (null == textView) {
            Log.out("参数为空(textView)");
            return false;
        }
        return Linkify.addLinks(textView, Linkify.WEB_URLS);
    }

    /**
     * 判断网络是可用
     *
     * @param context 上下文
     * @return true可用，false不可用
     */
    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                context.getMainLooper().prepare();
//                Toast.makeText(context.getApplicationContext(), "网络不可用", Toast.LENGTH_LONG).show();
//                context.getMainLooper().loop();
//            }
//        }).start();

        Utils.toast(context, "网络不可用");


        Log.out("网络不可用");
        return false;
    }

    /**
     * 联网方式是数据流量联网
     *
     * @param context 上下文
     * @return true是流量，false不是流量
     */
    public static boolean isGPRS(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 联网方式是无线网联网
     *
     * @param context 上下文
     * @return true是无线网，false不是无线网
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


}
