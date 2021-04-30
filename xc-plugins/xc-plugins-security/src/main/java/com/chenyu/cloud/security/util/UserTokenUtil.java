package com.chenyu.cloud.security.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.chenyu.cloud.auth.model.UserModel;
import com.chenyu.cloud.common.constants.TokenConstants;
import com.chenyu.cloud.common.enums.LoginLimitRefuse;
import com.chenyu.cloud.common.exception.TokenException;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.common.response.TokenMsg;
import com.chenyu.cloud.common.properties.GlobalProperties;
import com.chenyu.cloud.core.utils.CacheUtil;
import com.chenyu.cloud.redis.RedisPlugin;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.chenyu.cloud.common.constants.OrderConstants.UTIL_ORDER;

/**
 * UserTokenUtil
 * Created by JackyChen on 2021/04/29.
 */
@Slf4j
@Order(UTIL_ORDER)
@Component
@Lazy(false)
public class UserTokenUtil {

    /** token header */
    public static String TOKEN_NAME;

    @Autowired
    private GlobalProperties globalProperties;

    @PostConstruct
    public void init() {
        if (globalProperties != null) {
            TOKEN_NAME = globalProperties.getAuth().getToken().getTokenHeader();
        }
    }

    /** 缓存前缀 */
    private static final String TICKET_PREFIX = "ticket:";
    /** 账号失败次数 */
    public static final String ACCOUNT_SLIP_COUNT_PREFIX = "account:slip:count:";
    /** 账号失败锁定KEY */
    public static final String ACCOUNT_SLIP_LOCK_PREFIX = "account:slip:lock:";
    /** 限制登录数量 -1 为无限大 */
    public static final int ACCOUNT_LIMIT_INFINITE = -1;
    /** 登录配置信息 */
    public static GlobalProperties.Auth.Login LOGIN_PROPERTIES;
    /** Redis插件 */
    private static RedisPlugin redisPlugin;


    /**
     * 根据 user 创建Token
     * @param user 用户
     * @return UserTokenUtil.TokenRet
     */
    public static Result<TokenRet> createToken(UserModel user) {
        if (user == null) {
            // 生成Token失败
            throw new TokenException(TokenMsg.EXCEPTION_TOKEN_CREATE_ERROR);
        }

        try {

            // 如果当前登录开启 数量限制
            if(LOGIN_PROPERTIES.getLimitCount() > ACCOUNT_LIMIT_INFINITE){
                // 当前用户已存在 Token数量
                Long ticketLen = redisPlugin.sSize(CacheUtil.getPrefixName() + TICKET_PREFIX + user.getUsername());
                if(ticketLen !=null && ticketLen >= LOGIN_PROPERTIES.getLimitCount()){
                    // 如果是拒绝后者 则直接抛出异常
                    if(LoginLimitRefuse.AFTER == LOGIN_PROPERTIES.getLimitRefuse()){
                        // 生成Token失败 您的账号已在其他设备登录
                        throw new TokenException(TokenMsg.EXCEPTION_TOKEN_CREATE_LIMIT_ERROR);
                    }
                    // 如果是拒绝前者 则弹出前者
                    else {
                        redisPlugin.sPop(CacheUtil.getPrefixName() + TICKET_PREFIX + user.getUsername());
                    }
                }
            }

            // 生效时间
            int expire = Integer.parseInt(
                    String.valueOf(JwtUtil.EXPIRE)
            );

            // 生成 Token 包含 username userId timestamp
            String signToken = JwtUtil.sign(user.getUsername(), user.getId());

            // 获得当前时间戳时间
            long timestamp = Convert.toLong(
                    JwtUtil.getClaim(signToken, TokenConstants.CREATE_TIME));
            DateTime currDate = DateUtil.date(timestamp);

            // 获得失效偏移量时间
            DateTime dateTime = DateUtil.offsetMillisecond(currDate, expire);
            long endTimestamp = dateTime.getTime();

            // 在redis存一份 token 是为了防止 人为造假
            // 保存用户token
            Long saveLong = redisPlugin.sPut(CacheUtil.getPrefixName() + TICKET_PREFIX + user.getUsername(), signToken);
            if(saveLong != null && saveLong > 0){
                // 设置该用户全部token失效时间， 如果这时又有新设备登录 则续命
                redisPlugin.expire(CacheUtil.getPrefixName() + TICKET_PREFIX + user.getUsername(), expire);

                TokenRet tokenRet = new TokenRet();
                tokenRet.setToken(signToken);
                tokenRet.setEndTimestamp(endTimestamp);

                return Result.success(tokenRet);
            }

        }catch (TokenException te){
            throw te;
        }catch (Exception e){
            log.error(e.getMessage() , e);
        }
        // 生成Token失败
        throw new TokenException(TokenMsg.EXCEPTION_TOKEN_CREATE_ERROR);
    }

    /**
     * 根据 Token 获得用户ID
     * @param token token
     * @return Integer
     */
    public static Integer getUserIdByToken(String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        String userId = "";
        try {
            userId = JwtUtil.getClaim(token, TokenConstants.USER_ID);
        } catch (Exception ignored){}
        return Integer.parseInt(userId);
    }

    /**
     * 根据 Token 获得 username
     * @param token token
     * @return String
     */
    public static String getUserNameByToken(String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        String username = "";
        try {
            username = JwtUtil.getClaim(token, TokenConstants.USERNAME);
        }catch (Exception ignored){}
        return username;
    }



    /**
     * 退出登陆
     * @param token token
     */
    public static void logout(String token) {
        if(StringUtils.isEmpty(token)){
            return;
        }
        try {
            // 获得要退出用户
            Integer userId = getUserIdByToken(token);
            UserModel user = UserUtil.getUser(userId);
            if(user != null){
                // 删除Token信息
                redisPlugin.sRemove(CacheUtil.getPrefixName() + TICKET_PREFIX + user.getUsername(), token);

                // 如果缓存中 无该用户任何Token信息 则删除用户缓存
                Long size = redisPlugin.sSize(CacheUtil.getPrefixName() + TICKET_PREFIX + user.getUsername());
                if(size == null || size == 0L){
                    // 删除相关信息
                    UserUtil.refreshUser(user);
                    UserUtil.refreshUserRoles(user.getId());
                    UserUtil.refreshUserAllPerms(user.getId());
                    UserUtil.refreshUserMenus(user.getId());
                }
            }

        }catch (Exception ignored){}
    }

    /**
     * 验证 token
     * @param token token
     */
    public static boolean verify(String token) {
        if(StringUtils.isEmpty(token)){
            return false;
        }

        try {
            // 1. 校验是否是有效的 token
            boolean tokenVerify = JwtUtil.verify(token);
            if(!tokenVerify){
                return false;
            }

            // 2. 校验当前缓存中token是否失效
            // 生成MD5 16进制码 用于缩减存储
            // 删除相关信息
            String username = getUserNameByToken(token);
            boolean hashKey = redisPlugin.sHashKey(CacheUtil.getPrefixName() + TICKET_PREFIX + username, token);
            if(!hashKey){
                return false;
            }
            // JWT 自带过期校验 无需多做处理

        } catch (Exception e){
            return false;
        }
        return true;
    }

    // ============================ 锁账号 操作

    /**
     * 验证锁定账号
     * @param username 用户名
     */
    public static void verifyLockAccount(String username){
        // 判断账号是否临时锁定
        Long loseTimeMillis = (Long) redisPlugin.get(CacheUtil.getPrefixName() + ACCOUNT_SLIP_LOCK_PREFIX + username);
        if(loseTimeMillis != null){
            Date currDate = new Date();
            DateTime loseDate = DateUtil.date(loseTimeMillis);
            // 偏移5分钟
            DateTime currLoseDate = DateUtil.offsetSecond(loseDate, LOGIN_PROPERTIES.getSlipLockSpeed());

            // 计算失效剩余时间( 分 )
            long betweenM = DateUtil.between(currLoseDate, currDate, DateUnit.MINUTE);
            if(betweenM > 0){
                String msg = StrUtil.format(TokenMsg.EXCEPTION_LOGIN_ACCOUNT_LOCK.getMessage()
                        ,betweenM + "分钟");
                throw new TokenException(TokenMsg.EXCEPTION_LOGIN_ACCOUNT_LOCK.getCode(), msg);
            }else{
                // 计算失效剩余时间( 秒 )
                long betweenS = DateUtil.between(currLoseDate, currDate, DateUnit.SECOND);
                String msg = StrUtil.format(TokenMsg.EXCEPTION_LOGIN_ACCOUNT_LOCK.getMessage()
                        ,betweenS + "秒");
                throw new TokenException(TokenMsg.EXCEPTION_LOGIN_ACCOUNT_LOCK.getCode(), msg);
            }
        }
    }

    /**
     * 锁定账号
     * @param username 用户名
     */
    public static TokenMsg lockAccount(String username){
        // 如果失败次数 超过阈值 则锁定账号
        Long slipNum = redisPlugin.increment(CacheUtil.getPrefixName() + ACCOUNT_SLIP_COUNT_PREFIX + username);
        if (slipNum != null){
            // 设置失效时间为 5分钟
            redisPlugin.expire(CacheUtil.getPrefixName() + ACCOUNT_SLIP_COUNT_PREFIX + username, LOGIN_PROPERTIES.getSlipLockSpeed());

            // 如果确认 都失败 则存入临时缓存
            if(slipNum >= LOGIN_PROPERTIES.getSlipCount()){
                long currentTimeMillis = System.currentTimeMillis();
                // 存入Redis
                redisPlugin.put(CacheUtil.getPrefixName() + ACCOUNT_SLIP_LOCK_PREFIX + username,
                        currentTimeMillis, LOGIN_PROPERTIES.getSlipLockSpeed());
            }
        }

        return TokenMsg.EXCEPTION_LOGIN_ACCOUNT_NO;
    }

    /**
     * 获得当前失败次数
     * @param username 用户名
     */
    public static long getSlipCount(String username){
        long count = 0L;
        Object obj = redisPlugin.get(CacheUtil.getPrefixName() + ACCOUNT_SLIP_COUNT_PREFIX + username);
        if(obj != null){
            try {
                count = Convert.convert(Long.class, obj);
            }catch (Exception ignored){}
        }
        return count;
    }


    /**
     * 清除锁定账号
     * @param username 用户名
     */
    public static void clearLockAccount(String username){
        // 删除失败次数记录
        redisPlugin.del(CacheUtil.getPrefixName() + ACCOUNT_SLIP_COUNT_PREFIX + username);
        // 删除失败次数记录
        redisPlugin.del(CacheUtil.getPrefixName() + ACCOUNT_SLIP_LOCK_PREFIX + username);
    }


    // ==========================

    /**
     * 获取请求的token
     */
    public static String getRequestToken(HttpServletRequest httpRequest){

        //从header中获取token
        String token = httpRequest.getHeader(TOKEN_NAME);

        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = httpRequest.getParameter(TOKEN_NAME);
        }

        return token;
    }

    /**
     * 初始化
     * @param globalProperties 配置类
     */
    @Autowired
    public void init(GlobalProperties globalProperties, RedisPlugin redisPlugin){
        if(globalProperties != null && globalProperties.getAuth() != null
                && globalProperties.getAuth().getLogin() != null
        ){
            // 登录配置信息
            UserTokenUtil.LOGIN_PROPERTIES = globalProperties.getAuth().getLogin();
        }

        // Redis 插件
        UserTokenUtil.redisPlugin = redisPlugin;
    }


    // =====================

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class TokenRet {

        /** Token */
        @ApiModelProperty(value = "Token")
        private String token;

        /** 失效时间戳 */
        @ApiModelProperty(value = "失效时间戳")
        private Long endTimestamp;

    }

}
