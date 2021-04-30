package com.chenyu.cloud.security.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chenyu.cloud.common.constants.TokenConstants;
import com.chenyu.cloud.common.exception.JwtException;
import com.chenyu.cloud.common.response.JwtMsg;
import com.chenyu.cloud.common.properties.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.chenyu.cloud.common.constants.OrderConstants.UTIL_ORDER;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang", "tenant":1,"created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 * Created by JackyChen on 2021/4/28.
 */
@Slf4j
@Order(UTIL_ORDER)
@Lazy(false)
@Component
public class JwtUtil {

    /**
     * 过期时间改为从配置文件获取 120 分钟 两小时
     */
    public static long EXPIRE;


    /**
     * JWT认证加密私钥(Base64加密)
     */
    private static String ENCRYPT_JWT_INITIAL_SECRET;


    /**
     * 校验验证帐号加JWT私钥解密 是否正确
     * @param token Token
     * @return boolean 是否正确
     */
    public static boolean verify(String token) {
        String secret = getClaim(token, TokenConstants.USERNAME) + Base64.decodeStr(ENCRYPT_JWT_INITIAL_SECRET);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
        return true;
    }

    /**
     * 获得Token中的信息
     * @param token token
     * @param claim 字段
     * @return java.lang.String
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            // 解密异常
            String msg = StrUtil.format(JwtMsg.EXCEPTION_DECODE.getMessage(), e.getMessage());
            throw new JwtException(JwtMsg.EXCEPTION_DECODE.getCode(), msg);
        }
    }

    /**
     * 生成签名
     * @param account 帐号
     * @param userId 用户ID
     * @return java.lang.String 返回加密的Token
     */
    public static String sign(String account, Integer userId) {
        // 帐号加JWT私钥加密
        String secret = account + Base64.decodeStr(ENCRYPT_JWT_INITIAL_SECRET);
        // 此处过期时间是以毫秒为单位，所以乘以1000
        Date date = new Date(System.currentTimeMillis() + EXPIRE);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带account帐号信息
        return JWT.create()
                .withClaim(TokenConstants.USERNAME, account)
                .withClaim(TokenConstants.USER_ID, userId)
                .withClaim(TokenConstants.CREATE_TIME, String.valueOf(System.currentTimeMillis()))
                // token 过期时间
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 生成签名
     * @param account 帐号
     * @return java.lang.String 返回加密的Token
     */
    public static String sign(String account) {
        // 帐号加JWT私钥加密
        String secret = account + Base64.decodeStr(ENCRYPT_JWT_INITIAL_SECRET);
        // 此处过期时间是以毫秒为单位，所以乘以1000
        Date date = new Date(System.currentTimeMillis() + EXPIRE);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带account帐号信息
        return JWT.create()
                .withClaim(TokenConstants.USERNAME, account)
                .withClaim(TokenConstants.CREATE_TIME, String.valueOf(System.currentTimeMillis()))
                // token 过期时间
                .withExpiresAt(date)
                .sign(algorithm);
    }


    // ==================

    /**
     * 初始化
     * @param globalProperties 配置类
     */
    @Autowired
    public void init(GlobalProperties globalProperties){
        //
        if(globalProperties != null && globalProperties.getAuth() != null
                && globalProperties.getAuth().getToken() != null
        ){
            // 获得 Token失效时间
            JwtUtil.EXPIRE = globalProperties.getAuth()
                    .getToken().getEffectiveTime() * 60 * 1000;

            // 获得 Token初始盐值
            JwtUtil.ENCRYPT_JWT_INITIAL_SECRET = globalProperties.getAuth()
                    .getToken().getSecret();
        }
    }
}
