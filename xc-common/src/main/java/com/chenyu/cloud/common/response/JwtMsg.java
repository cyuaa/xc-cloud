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
package com.chenyu.cloud.common.response;

/**
 * JWT - 消息
 * Created by JackyChen on 2021/4/29.
 */
public enum JwtMsg implements BaseMsg {

    /** 异常消息 */
    EXCEPTION_TOKEN(11000,"认证解密异常: {}"),
    EXCEPTION_DECODE(10101,"解密异常: {}"),
    EXCEPTION_ENCODE(10102,"加密异常: {}"),


    ;

    private final int code;
    private final String message;

    JwtMsg(int code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
