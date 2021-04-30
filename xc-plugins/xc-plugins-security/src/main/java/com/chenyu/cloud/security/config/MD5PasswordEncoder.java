package com.chenyu.cloud.security.config;

import com.chenyu.cloud.common.properties.GlobalProperties;
import com.chenyu.cloud.security.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

/**
 * MD5密码加密
 * Created by JackyChen on 2021/04/29.
 */
public class MD5PasswordEncoder implements PasswordEncoder {

    private static String SECRET;

    @Autowired
    private GlobalProperties globalProperties;

    @PostConstruct
    public void init() {
        if (null != globalProperties) {
            SECRET = globalProperties.getAuth().getToken().getSecret();
        }
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return MD5Util.encode((String) rawPassword, SECRET);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5Util.encode((String)rawPassword, SECRET));
    }
}
