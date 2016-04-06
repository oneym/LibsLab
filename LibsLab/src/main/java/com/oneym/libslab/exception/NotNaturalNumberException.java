package com.oneym.libslab.exception;

/**
 * 非自然数异常，一个数不是自然数就会抛出异常。<br/>
 * NotNaturalNumberException,if the number is not a natural number,throw this exception.
 * @author oneym oneym@sina.cn
 * @since 20151117103828
 *
 * @see Exception
 */
public class NotNaturalNumberException extends Exception {

    public NotNaturalNumberException() {
        super("数字为非自然数。The number is not a natural number.");
    }
}
