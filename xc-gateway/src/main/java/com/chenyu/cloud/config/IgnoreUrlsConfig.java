package com.chenyu.cloud.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于配置不需要保护的资源路径
 * Created by JackyChen on 2021/4/13.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "xc.ignore")
public class IgnoreUrlsConfig {

    private List<String> urls = new ArrayList<>();

}
