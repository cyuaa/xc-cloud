package com.chenyu.cloud.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static com.chenyu.cloud.common.constants.CoreConstants.URL_IGNORE;

/**
 * 用于配置不需要保护的资源路径
 * Created by JackyChen on 2021/4/13.
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = URL_IGNORE)
public class IgnoreUrlsProperties {

    private List<String> urls = new ArrayList<>();

}
