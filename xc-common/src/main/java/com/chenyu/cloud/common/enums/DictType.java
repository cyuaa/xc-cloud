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
package com.chenyu.cloud.common.enums;


/**
 * 字典类型
 */
public enum DictType {

    /** 是否 */
    NO_YES_NO("no_yes",0),
    NO_YES_YES("no_yes",1),


    ;

    private final String type;
    private final Integer value;

    DictType(String type, Integer value){
        this.type = type;
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getType() {
        return this.type;
    }

    /**
     * 获得对应的字典
     * @param type 类型
     * @param value 值
     * @return DictType
     */
    public static DictType getDict(String type, Integer value) {
        DictType[] var1 = values();
        for (DictType dict : var1) {
            if(dict.type.equals(type) &&
                    dict.value.equals(value)
            ){
                return dict;
            }
        }
        return null;
    }

    /**
     * 是否是字典内
     * @param type 类型
     * @param value 值
     * @return boolean
     */
    public static boolean hasDict(String type, Integer value) {
        DictType[] var1 = values();
        for (DictType dict : var1) {
            if(dict.type.equals(type) &&
                    dict.value.equals(value)){
                return true;
            }
        }
        return false;
    }
}
