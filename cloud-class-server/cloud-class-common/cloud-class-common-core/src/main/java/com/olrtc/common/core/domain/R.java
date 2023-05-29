package com.olrtc.common.core.domain;

import com.olrtc.common.core.constants.HttpStatusConst;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author njy
 * @since 2023/2/2 09:24
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    /**
     * 成功
     */
    public static final int SUCCESS = HttpStatusConst.SUCCESS;

    /**
     * 失败
     */
    public static final int FAIL = HttpStatusConst.BAD_REQUEST;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> R<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, "操作失败");
    }

    public static <T> R<T> fail(String msg, T data) {
        return restResult(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> R<T> warn(String msg) {
        return restResult(null, HttpStatusConst.WARN, msg);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> R<T> warn(String msg, T data) {
        return restResult(data, HttpStatusConst.WARN, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

    public static <T> Boolean isError(R<T> ret) {
        return !isSuccess(ret);
    }

    public static <T> Boolean isSuccess(R<T> ret) {
        return R.SUCCESS == ret.getCode();
    }

}
