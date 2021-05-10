/**
 * Copyright 2020 OPSLI 快速开发平台 https://www.opsli.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.chenyu.cloud.core.util;

import cn.hutool.core.util.RandomUtil;
import com.chenyu.cloud.common.exception.TokenException;
import com.chenyu.cloud.common.response.TokenMsg;
import com.chenyu.cloud.redis.RedisPlugin;
import com.google.common.collect.Lists;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

import static com.chenyu.cloud.common.constants.OrderConstants.UTIL_ORDER;


/**
 * 验证码
 * Created by JackyChen on 2021/04/16.
 */
@Component
@Order(UTIL_ORDER)
@Lazy(false)
public class CaptchaUtil {

    /** 验证码宽度 */
    private static final int CAPTCHA_WIDTH = 180;
    /** 验证码高度 */
    private static final int CAPTCHA_HEIGHT = 58;
    /** 验证码位数 */
    private static final int CAPTCHA_LEN = 4;
    /** 验证码策略 */
    private static final List<CaptchaStrategy> CAPTCHA_STRATEGY_LIST;

    /** 缓存前缀 */
    private static final String PREFIX = "temp:captcha:";
    /** 默认验证码保存 5 分钟 */
    private static final int TIME_OUT = 300;
    /** Redis插件 */
    private static RedisPlugin redisPlugin;

    static {
        CAPTCHA_STRATEGY_LIST = Lists.newArrayListWithCapacity(3);
        CAPTCHA_STRATEGY_LIST.add(new CaptchaStrategyBySpec());
        CAPTCHA_STRATEGY_LIST.add(new CaptchaStrategyByGif());
        CAPTCHA_STRATEGY_LIST.add(new CaptchaStrategyByArithmetic());
    }

    /**
     * 获得验证码
     *
     * @param uuid UUID
     */
    public static void createCaptcha(String uuid, OutputStream out) {
        if (StringUtils.isBlank(uuid)) {
            throw new RuntimeException("uuid不能为空");
        }

        // 随机生成验证码
        int randomInt = RandomUtil.randomInt(0, CAPTCHA_STRATEGY_LIST.size());

        // 获得验证码生成策略
        CaptchaStrategy captchaStrategy = CAPTCHA_STRATEGY_LIST.get(randomInt);

        // 生成验证码
        Captcha captcha = captchaStrategy.createCaptcha();

        // 保存至缓存
        boolean ret = redisPlugin.put(CacheUtil.getPrefixName() + PREFIX + uuid, captcha.text(), TIME_OUT);
        if(ret){
            // 输出
            captcha.out(out);
        }
    }

    /**
     * 校验验证码
     *
     * @param uuid UUID
     * @param code 验证码
     */
    public static void validate(String uuid, String code) {
        // 判断UUID 是否为空
        if (StringUtils.isEmpty(uuid)) {
            throw new TokenException(TokenMsg.EXCEPTION_CAPTCHA_UUID_NULL);
        }

        // 判断 验证码是否为空
        if (StringUtils.isEmpty(code)) {
            throw new TokenException(TokenMsg.EXCEPTION_CAPTCHA_CODE_NULL);
        }

        // 验证码
        String codeTemp = (String) redisPlugin.get(CacheUtil.getPrefixName() + PREFIX + uuid);
        if (StringUtils.isEmpty(codeTemp)) {
            throw new TokenException(TokenMsg.EXCEPTION_CAPTCHA_NULL);
        }

        // 验证 验证码是否正确
        boolean captchaFlag = codeTemp.equalsIgnoreCase(code);
        if (!captchaFlag) {
            throw new TokenException(TokenMsg.EXCEPTION_CAPTCHA_ERROR);
        }
    }


    /**
     * 删除验证码
     *
     * @param uuid UUID
     * @return boolean
     */
    public static boolean delCaptcha(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }

        //删除验证码
        return redisPlugin.del(CacheUtil.getPrefixName() + PREFIX + uuid);
    }


    // ==========================

    @Autowired
    public void setRedisPlugin(RedisPlugin redisPlugin) {
        CaptchaUtil.redisPlugin = redisPlugin;
    }


    // ======================

    public interface CaptchaStrategy{

        /**
         * 生成验证码对象
         * @return Captcha
         */
        Captcha createCaptcha();

    }

    /**
     * 数字英文混合验证码 静态
     */
    private static class CaptchaStrategyBySpec implements CaptchaStrategy{
        @Override
        public Captcha createCaptcha() {
            // 生成验证码
            SpecCaptcha captcha = new SpecCaptcha(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_LEN);
            captcha.setCharType(Captcha.TYPE_DEFAULT);
            return captcha;
        }
    }

    /**
     * 数字英文混合验证码 动态
     */
    private static class CaptchaStrategyByGif implements CaptchaStrategy{
        @Override
        public Captcha createCaptcha() {
            // 生成验证码
            GifCaptcha captcha = new GifCaptcha(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_LEN);
            captcha.setCharType(Captcha.TYPE_DEFAULT);
            return captcha;
        }
    }

    /**
     * 算数验证码 动态
     */
    private static class CaptchaStrategyByArithmetic implements CaptchaStrategy{
        @Override
        public Captcha createCaptcha() {
            // 生成验证码
            return new ArithmeticCaptcha(CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        }
    }

}
