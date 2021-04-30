package com.chenyu.cloud.common.properties;

import com.chenyu.cloud.common.enums.LoginLimitRefuse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 全局配置文件
 * Created by JackyChen on 2021/4/29.
 **/
@Configuration
@ConfigurationProperties(prefix = GlobalProperties.PROP_PREFIX)
@Data
@EqualsAndHashCode(callSuper = false)
public class GlobalProperties {

    public static final String PROP_PREFIX = "xc-cloud";

    /** 系统名称 */
    private String systemName;

    /** 系统启动时间 为空则默认 真实当前系统启动时间 */
    private String systemStarterTime;

    /** 认证类 */
    private Auth auth;

    /** Web类 */
    private Web web;

    /** Excel类 */
    private Excel excel;

    public String getSystemName() {
        return systemName;
    }

    // ============== 内部类 =============

    /**
     * Web
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Web {

        /** 文件上传地址 */
        private String uploadPath;

    }

    /**
     * 认证类
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Auth {

        /** 超级管理员账号 */
        private String superAdmin;

        /** 默认密码 */
        private String defaultPass;

        /** Token */
        private Token token;

        /** Login */
        private Login login;


        /**
         * 认证类
         */
        @Data
        @EqualsAndHashCode(callSuper = false)
        public static class Token {

            private String tokenHeader;

            private String tokenHead;

            /** 盐值 */
            private String secret;

            /** 有效时间 （分钟）*/
            private Integer effectiveTime;

            /** 排除URL*/
            private Set<String> urlExclusion;

        }

        /**
         * 登录类
         */
        @Data
        @EqualsAndHashCode(callSuper = false)
        public static class Login {

            /** 限制登录数量 -1 为无限大 */
            private Integer limitCount;

            /** 限制登录拒绝策略 after为后者 before为前者 */
            private LoginLimitRefuse limitRefuse = LoginLimitRefuse.AFTER;

            /** 失败次数 */
            private Integer slipCount;

            /** 失败N次后弹出验证码 （超过验证码阈值 弹出验证码） */
            private Integer slipVerifyCount;

            /** 失败锁定时间(秒) */
            private Integer slipLockSpeed;


        }

    }

    /**
     * Excel
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Excel {

        /** 最大导出操作数 */
        private Integer exportMaxCount;

    }

}

