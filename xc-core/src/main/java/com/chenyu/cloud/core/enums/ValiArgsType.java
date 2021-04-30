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
package com.chenyu.cloud.core.enums;


/**
 * 通过单例 模式 生成系统唯一标示
 */
public enum ValiArgsType {

    /** 不能为空 */
    IS_NOT_NULL,
    /** 字母，数字和下划线 */
    IS_GENERAL,
    /** 数字 */
    IS_NUMBER,
    /** 小数浮点 */
    IS_DECIMAL,
    /** 纯字母 */
    IS_LETTER,
    /** 大写 */
    IS_UPPER_CASE,
    /** 小写 */
    IS_LOWER_CASE,
    /** ip4 */
    IS_IPV4,
    /** 金额 */
    IS_MONEY,
    /** 邮箱 */
    IS_EMAIL,
    /** 手机号 */
    IS_MOBILE,
    /** 18位身份证 */
    IS_CITIZENID,
    /** 邮编 */
    IS_ZIPCODE,
    /** URL */
    IS_URL,
    /** 汉字 */
    IS_CHINESE,
    /** 汉字，字母，数字和下划线 */
    IS_GENERAL_WITH_CHINESE,
    /** MAC地址 */
    IS_MAC,
    /** 中国车牌 */
    IS_PLATE_NUMBER,

    ;

}
