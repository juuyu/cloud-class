package com.olrtc.common.core.exception;

/**
 * @author njy
 * @since 2023/2/28 18:09
 */
public class BizException extends RuntimeException{

    public BizException() {
        super();
    }

    public BizException(String msg){
        super(msg);
    }
}
