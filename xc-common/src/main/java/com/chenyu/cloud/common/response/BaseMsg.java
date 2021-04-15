package com.chenyu.cloud.common.response;
/**
 * 统一存放消息
 * Created by JackyChen on 2021/04/14.
 */
public interface BaseMsg {

    /**
     * 获取消息的状态码
     */
    Integer getCode();

    /**
     * 获取消息提示信息
     */
    String getMessage();

}
