package com.olrtc.common.core.exception;


/**
 * 工具类异常
 *
 * @author njy
 * @since 14:57 2023/2/22
 */
public class UtilException extends RuntimeException {

    public UtilException(Throwable e) {
        super(e.getMessage(), e);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
