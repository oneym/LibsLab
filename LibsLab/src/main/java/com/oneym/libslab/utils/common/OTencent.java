//package com.oneym.libslab.utils.common;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.widget.ImageView;
//
//import com.oneym.libslab.utils.io.UtilsIO;
//import com.oneym.libslab.utils.media.ImageViewAsyncLoader;
//import com.oneym.libslab.utils.net.Download;
//import com.oneym.libslab.utils.net.UtilsNet;
//import com.oneym.libslab.utils.string.UtilsString;
//import com.oneym.libslab.bean.QQBean;
//import com.tencent.connect.UserInfo;
//import com.tencent.connect.auth.QQToken;
//import com.tencent.connect.common.Constants;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.Tencent;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * 腾讯管理类
// *
// * @author oneym oneym@sina.cn
// * @since 20151222131522
// * @deprecated
// */
//public class OTencent {
//
//    public static final String OPENID = "OPENID";
//    public static final String EXPIRES_IN = "EXPIRES_IN";
//    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
//    /** QQBean对象装填完成*/
//    public static final int LOADED = 1;
//    private static OTencent instance = null;
//    /**
//     * 临时存储QQ用户信息json,用户退出就没有了
//     */
//    public QQBean qq = null;
//    private Tencent mTencent = null;
//    private SharedPreferences p = null;
//    //这里是init传递过来的context
//    private Context context = null;
//    private QQToken token = null;
//    //线程池（只有一个线程）
//    private ExecutorService pool = null;
//    private QQBeanLoaded loaded = null;
//
//    private OTencent() {
//    }
//
//    public static OTencent getIntance() {
//        if (null == instance)
//            instance = new OTencent();
//        return instance;
//    }
//
//    /**
//     * 初始化
//     *
//     * @param APP_ID  第三方id
//     * @param context 上下文
//     */
//    public void init(final String APP_ID, final Context context) {
//        this.context = context;
//        pool = Executors.newSingleThreadExecutor();//创建一个只有一个线程的线程池
//
//        p = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
//        mTencent = Tencent.createInstance(APP_ID, context);
//
//        String openid = p.getString(OPENID, "openid");
//        String access_token = p.getString(ACCESS_TOKEN, "access_token");
//        long expires_in = (p.getLong(EXPIRES_IN, 0L) - System.currentTimeMillis()) / 1000;
//
//        //不是默认值就表示不是第一次登陆，其他的问题交给腾讯来处理
//        if (!openid.equals("openid") && !access_token.equals("access_token") && expires_in > 0L) {
//            mTencent.setOpenId(openid);
//            mTencent.setAccessToken(access_token, expires_in + "");
//        }
//        token = mTencent.getQQToken();
//    }
//
//    /**
//     * QQ登陆
//     *
//     * @param activity Activity
//     * @param listener 监听回调
//     */
//    public void doLogin_qq(final Activity activity, final IUiListener listener) {
//        if (null == mTencent)
//            throw new NullPointerException("请使先调用init初始化OTencent类");
//        mTencent.login(activity, "all", listener);
//
//    }
//
//    /**
//     * 低端机回调补救方法，这个方法要在重写(override)onActivityResult中调用，放在第一行，【注意】如果有super，super放在这个方法后面
//     *
//     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
//     * @param resultCode  The integer result code returned by the child activity through its setResult().
//     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
//     * @param listener    回调
//     * @see <a href="http://wiki.open.qq.com/wiki/mobile/Android_SDK%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E"> 【官网】参考文档一<a/>、
//     * <a href="http://wiki.open.qq.com/wiki/%E5%88%9B%E5%BB%BA%E5%AE%9E%E4%BE%8B%E5%B9%B6%E5%AE%9E%E7%8E%B0%E5%9B%9E%E8%B0%83"> 【官网】参考文档二<a/>
//     */
//    public void doLogin_qq_low(final int requestCode, final int resultCode, final Intent data, final IUiListener listener) {
//        if (requestCode == Constants.REQUEST_LOGIN)
//            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
//    }
//
//    /**
//     * 获取用户的详细信息，如用户名、性别、头像图片等等
//     *
//     * @param listener 监听器
//     */
//    public void getUserInfo(final IUiListener listener) {
//        pool.execute(new Runnable() {
//            @Override
//            public void run() {//网络网络耗时操作，放在线程中
//                UserInfo info = new UserInfo(context, token);
//                info.getUserInfo(listener);
//            }
//        });
//    }
//
//    /**
//     * 存储用户的令牌信息，在登陆的回调函数onComplete方法中调用<br/>
//     * 用户的详细信需要发送到自己的服务器来存储
//     *
//     * @param o 成功后的JSON数据
//     */
//    public void saveTokenInfo(final JSONObject o) {
//        try {
//            //在同一个监听器里面存在返回的json数据不同，故在此判断json中的元素是存在
//            if (o.has("openid") && o.has("expires_in") && o.has("access_token")) {
//                String openid = o.getString("openid");
//                long expires_in = o.getLong("expires_in");
//                String access_token = o.getString("access_token");
//
//                String _openid = p.getString(OPENID, "openid");
//                String _access_token = p.getString(ACCESS_TOKEN, "access_token");
//
//                if (!UtilsString.isEmptyString(openid) && !UtilsString.isEmptyString(access_token) && expires_in > 0) {
//                    if (!_openid.equals(openid) || !_access_token.equals(access_token)) {
//                        SharedPreferences.Editor editor = p.edit();
//                        editor.putString(OPENID, openid);
//                        editor.putLong(EXPIRES_IN, System.currentTimeMillis() + expires_in * 1000);
//                        editor.putString(ACCESS_TOKEN, access_token);
//                        editor.commit();
//
//                        mTencent.setOpenId(openid);
//                        mTencent.setAccessToken(access_token, ((p.getLong(EXPIRES_IN, 0L) - System.currentTimeMillis()) / 1000) + "");
//                    }
//                } else {
//                    Log.out("QQ登陆失败，数据加载未成功");
//                }
//            }
//        } catch (JSONException e) {
//            Log.out(e);
//        }
//    }
//
//    /**
//     * 保存参数到服务器<br/>
//     * 有错误才会打印日志，要不然就不打印日志。控制台日志<br/>
//     * [注意]如果需要设置第三方的登陆头像，需要在次方法之前调用{@link ImageViewAsyncLoader#setData(ImageView, String, int)}
//     *
//     * @param o     成功后的JSON数据
//     * @param url   包含"?"号的url，"?"号之后的action已经加上了，只需要添加参数;不对这个参数作判断<br/>
//     *              如:http://ip:port/api.php?action=dosomething&（有action的），或者http://ip:port/api.php?(没有action的)<br/>
//     * @param maps  K-V对，K是url中添加参数的参数名，V是需要映射json值的参数名，K和V都是String类型的
//     * @param s_ret 我们服务器返回校验的json和传入到本函数的json相同的参数值的key映射；就是两个不同的json，值相同，不同键值；此map中key是
//     *              服务器返回的json的键值，value是传入json的键值
//     */
//    //@return {@link String} true表示上传成功,当ret是null时返回从服务器返回输出的json数据
//    public void saveUserInfo(final JSONObject o, String url, final Map maps, final Map s_ret) {
//        String ret = "true";//本函数返回的内容
//        //这里只判断了一些基本信息，json中包含这些信息就表示是用户的详细信息
//        if (o.has("figureurl_qq_1") && o.has("nickname") && o.has("city") && o.has("province") && o.has("gender")) {
//            analysisUserDetailsJson(o);
//            url = UtilsNet.linkUrl(o, url, maps);
//            Download.getInstance().start(url, "saveInfoData.json", "/Cache/data", new Download.OnDownloadingListener() {
//
//                @Override
//                public void onMessage(int type, String msg) {
//                    try {
//                        if (Download.OnDownloadingListener.TYPE_SUCCESS == type) {
//                            JSONObject ret = UtilsIO.getJsonObjectFromFile(msg, null);
//                            JSONObject ret_data = null;
//                            if (0 == ret.getInt("type"))
//                                ret_data = ret.getJSONObject("data");
//                            Set key = s_ret.keySet();
//                            Iterator i = key.iterator();
//                            while (i.hasNext()) {
//                                String k = UtilsString.toString(i.next());
//                                String v = UtilsString.toString(s_ret.get(k));
//                                if (ret_data.has(k) && o.has(v)) {
//                                    if (!ret_data.getString(k).equals(o.getString(v))) {
//                                        Object Object = "服务器返回输出的json数据中与传入的json不符";
//                                        Log.out(Object);
//                                        Log.out("返回的json: " + ret);
//                                        break;
//                                    }
//                                } else {
//                                    Log.out("服务器返回的json可能没有:" + k + "键,或者传入的json可能没有:" + v + "键");
//                                    break;
//                                }
//                            }
//
//                            Log.out("OTencent->saveUserInfo：成功！");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onSeekBarChange(int all, int now) {
//
//                }
//            });
//        }
//    }
//
//    private void analysisUserDetailsJson(final JSONObject obj) {
//        pool.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    qq = new QQBean();
//                    qq.setIs_yellow_year_vip(obj.getInt("is_yellow_year_vip"));
//                    qq.setRet(obj.getInt("ret"));
//                    qq.setFigureurl_qq_1(obj.getString("figureurl_qq_1"));
//                    qq.setFigureurl_qq_2(obj.getString("figureurl_qq_2"));
//                    qq.setNickname(obj.getString("nickname"));
//                    qq.setYellow_vip_level(obj.getInt("yellow_vip_level"));
//                    qq.setIs_lost(obj.getInt("is_lost"));
//                    qq.setMsg(obj.getString("msg"));
//                    qq.setCity(obj.getString("city"));
//                    qq.setFigureurl_1(obj.getString("figureurl_1"));
//                    qq.setVip(obj.getInt("vip"));
//                    qq.setLevel(obj.getInt("level"));
//                    qq.setFigureurl_2(obj.getString("figureurl_2"));
//                    qq.setProvince(obj.getString("province"));
//                    qq.setIs_yellow_vip(obj.getInt("is_yellow_vip"));
//                    qq.setGender(obj.getString("gender"));
//                    qq.setFigureurl(obj.getString("figureurl"));
//                    if (0 != qq.getRet()) {
//                        Log.out("OTencent: " + qq.getMsg());
//                    } else {
//                        if (null != loaded)
//                            loaded.loaded();//可以加载头像icon图片了
//                    }
//                } catch (JSONException e) {
//                    Log.out(e);
//                }
//            }
//        });
//    }
//
//    /**
//     * 这个接口其他地方不要去实现,这是用来发送给异步加载图片的，表示可以加载头像了<br/>
//     * 只在{@link ImageViewAsyncLoader}中实现
//     * @author oneym
//     */
//    public interface QQBeanLoaded {
//        void loaded();
//    }
//    public void setLoaded(QQBeanLoaded loaded) {
//        this.loaded = loaded;
//    }
//}
