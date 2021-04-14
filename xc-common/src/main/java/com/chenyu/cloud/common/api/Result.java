package com.chenyu.cloud.common.api;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用返回对象
 * Created by JackyChen on 2021/04/13.
 */
@Getter
@Setter
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    protected Result() {
    }

    protected Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     * @param baseMsg 错误码
     */
    public static <T> Result<T> success(BaseMsg baseMsg) {
        return new Result<T>(baseMsg.getCode(), baseMsg.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(CommonMsg.SUCCESS.getCode(), CommonMsg.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<T>(CommonMsg.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 提示信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<T>(code, message, null);
    }

    /**
     * 失败返回结果
     * @param baseMsg 错误码
     */
    public static <T> Result<T> error(BaseMsg baseMsg) {
        return new Result<T>(baseMsg.getCode(), baseMsg.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param baseMsg 错误码
     * @param message 错误信息
     */
    public static <T> Result<T> error(BaseMsg baseMsg, String message) {
        return new Result<T>(baseMsg.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<T>(CommonMsg.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> error() {
        return error(CommonMsg.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> Result<T> validateError() {
        return error(CommonMsg.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> Result<T> validateError(String message) {
        return new Result<T>(CommonMsg.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> Result<T> unauthorized(T data) {
        return new Result<T>(CommonMsg.UNAUTHORIZED.getCode(), CommonMsg.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Result<T> forbidden(T data) {
        return new Result<T>(CommonMsg.FORBIDDEN.getCode(), CommonMsg.FORBIDDEN.getMessage(), data);
    }

}
