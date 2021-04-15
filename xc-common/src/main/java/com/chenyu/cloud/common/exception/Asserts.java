package com.chenyu.cloud.common.exception;


import com.chenyu.cloud.common.response.CommonMsg;

/**
 * 断言处理类，用于抛出各种API异常
 * Created by JackyChen on 2021/04/13.
 */
public class Asserts {
    public static void error(String message) {
        throw new ServiceException(message);
    }

    public static void error(CommonMsg resultCode) {
        throw new ServiceException(resultCode);
    }
}
