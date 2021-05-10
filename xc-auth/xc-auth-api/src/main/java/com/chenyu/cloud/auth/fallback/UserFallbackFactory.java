package com.chenyu.cloud.auth.fallback;

import com.chenyu.cloud.auth.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by JackyChen on 2021/05/08.
 */
@Component
@Slf4j
public class UserFallbackFactory implements FallbackFactory<UserApi> {
    @Override
    public UserApi create(Throwable cause) {
        log.error("User Feign Get Data Failed: [{}]", cause.getMessage());
        cause.printStackTrace();
        return null;
    }
}
