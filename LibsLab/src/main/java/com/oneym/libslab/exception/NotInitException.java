package com.oneym.libslab.exception;

/**
 * NotInitException，没有对方法初始化造成的错误
 * @author oneym oneym@sina.cn
 * @since 20151126142347
 *
 * @see Exception
 */
public class NotInitException extends Exception{
    public NotInitException() {
        super("没有init，请先初始化。");
    }
}
