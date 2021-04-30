package com.chenyu.cloud;

import com.chenyu.cloud.common.properties.GlobalProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 认证启动类
 * Created by JackyChen on 2021/04/29.
 */
@EnableConfigurationProperties(GlobalProperties.class)
@MapperScan
@SpringCloudApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}