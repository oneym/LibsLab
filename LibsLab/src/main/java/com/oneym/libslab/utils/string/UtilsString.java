package com.oneym.libslab.utils.string;

/**
 * @author oneym
 * @since 20151211141252
 */
public class UtilsString {

    /**
     * 在数字前加0
     *
     * @param builder StringBuilder
     * @param count   数字最后长度
     * @param value   需要添加0的数字
     */
    public static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    /**
     * 是空字符串
     *
     * @param str 字符串
     * @return true空的或者没有，false不是空的
     */
    public static boolean isEmptyString(String str) {
        boolean isEmpty = true;
        if (null != str && !str.equals("null"))
            isEmpty = str.isEmpty();
        return isEmpty;
    }

    /**
     * 重写对象的toString方法
     *
     * @param obj 对象
     * @return 字符串或空串
     */
    public static String toString(Object obj) {
        if (null == obj)
            return "";
        return obj.toString();
    }
}
