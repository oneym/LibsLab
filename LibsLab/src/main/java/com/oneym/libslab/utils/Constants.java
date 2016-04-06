package com.oneym.libslab.utils;

/**
 * 常量类
 *
 * @author oneym oneym@sina.cn
 * @since 20151130141044
 */
public interface Constants {
    int REFRESH = 1;
    int ADD = 2;
    int UPDATE = 3;

    /**
     * 图片的content-type和suffix对照表<br/>
     * IMG_CONTENT_TYPE[0][]->content-type<br/>
     * IMG_CONTENT_TYPE[1][]->suffix<br/>
     */
    String[][] IMG_CONTENT_TYPE = {{"image/jpeg", "image/png", "image/gif", "image/x-icon", "image/fax", "image/tiff", "image/pnetvue", "image/vnd.rn-realpix", "image/vnd.wap.wbmp"},
                                   {".jpg",       ".png",      ".gif",      ".ico",         ".fax",      ".tif ",      ".net",          ".rp",                  ".wbmp"}};


    //-------------------------友盟sdk->QQ------------------
    String UID = "uid";
    String NICK = "screen_name";
    String USER_FACE = "profile_image_url";
    String SEX = "gender";
    String PROVINCE = "province";
    String CITY = "city";
    //-------------------------友盟sdk->QQ------------------


    /**
     * 第三方登陆的平台名称的键值或者是文件名（SharedPreferences键）
     */
    String PLATFORM = "platform";
    /**
     * 键名为platform的SharedPreferences值
     */
    String QQ = "qq";
    /**
     * 键名为platform的SharedPreferences值
     */
    String WECHAT = "wechat";
    /**
     * 键名为platform的SharedPreferences值
     */
    String SINA = "weibo";

}