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
package com.chenyu.cloud.security.util;

import com.chenyu.cloud.auth.api.TenantApi;
import com.chenyu.cloud.auth.model.TenantModel;
import com.chenyu.cloud.common.enums.DictType;
import com.chenyu.cloud.common.response.CommonMsg;
import com.chenyu.cloud.common.response.CoreMsg;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.core.util.CacheUtil;
import com.chenyu.cloud.core.util.DistributedLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.chenyu.cloud.common.constants.OrderConstants.UTIL_ORDER;

/**
 * 租户工具类
 */
@Slf4j
@Order(UTIL_ORDER)
@Component
@Lazy(false)
public class TenantUtil {

    /** 前缀 */
    public static final String PREFIX_CODE = "tenant:id:";

    /** 租户 Api */
    private static TenantApi tenantApi;


    /**
     * 根据 tenantId 获得租户
     * @param tenantId
     * @return
     */
    public static TenantModel getTenant(Integer tenantId){
        // 缓存Key
        String cacheKey = PREFIX_CODE + tenantId;

        // 先从缓存里拿
        TenantModel tenantModel = CacheUtil.getTimed(TenantModel.class, cacheKey);
        if (tenantModel != null){
            return tenantModel;
        }

        // 拿不到 --------
        // 防止缓存穿透判断
        boolean hasNilFlag = CacheUtil.hasNilFlag(cacheKey);
        if(hasNilFlag){
            return null;
        }

        try {
            // 分布式加锁
            if(!DistributedLockUtil.lock(cacheKey)){
                // 无法申领分布式锁
                log.error(CoreMsg.REDIS_EXCEPTION_LOCK.getMessage());
                return null;
            }

            // 如果获得锁 则 再次检查缓存里有没有， 如果有则直接退出， 没有的话才发起数据库请求
            tenantModel = CacheUtil.getTimed(TenantModel.class, cacheKey);
            if (tenantModel != null){
                return tenantModel;
            }

            // 查询数据库
            Result<TenantModel> result = tenantApi.findByStatus(tenantId, DictType.NO_YES_YES.getValue());
            if(CommonMsg.SUCCESS.getCode().equals(result.getCode())){
                tenantModel = result.getData();
                // 存入缓存
                CacheUtil.put(cacheKey, tenantModel);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            // 释放锁
            DistributedLockUtil.unlock(cacheKey);
        }

        if(tenantModel == null){
            // 设置空变量 用于防止穿透判断
            CacheUtil.putNilFlag(cacheKey);
            return null;
        }

        return tenantModel;
    }


    // ============== 刷新缓存 ==============

    /**
     * 刷新租户 - 删就完了
     * @param tenantId
     * @return
     */
    public static boolean refreshTenant(Integer tenantId){
        if(null == tenantId){
            return true;
        }

        // 计数器
        int count = 0;

        TenantModel tenantModel = CacheUtil.getTimed(TenantModel.class, PREFIX_CODE + tenantId);
        boolean hasNilFlag = CacheUtil.hasNilFlag(PREFIX_CODE + tenantId);

        // 只要不为空 则执行刷新
        if (hasNilFlag){
            count++;
            // 清除空拦截
            boolean tmp = CacheUtil.delNilFlag(PREFIX_CODE + tenantId);
            if(tmp){
                count--;
            }
        }

        if(tenantModel != null){
            count++;
            // 先删除
            boolean tmp = CacheUtil.del(PREFIX_CODE + tenantId);
            if(tmp){
                count--;
            }
        }

        return count == 0;
    }




    // =====================================

    @Autowired
    public void setTenantApi(TenantApi tenantApi) {
        TenantUtil.tenantApi = tenantApi;
    }

}
