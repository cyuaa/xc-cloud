package com.chenyu.cloud;

import com.chenyu.cloud.common.properties.GlobalProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证启动类
 * Created by JackyChen on 2021/04/29.
 */
@EnableConfigurationProperties(GlobalProperties.class)
@MapperScan("com.chenyu.cloud.auth.mapper")
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
