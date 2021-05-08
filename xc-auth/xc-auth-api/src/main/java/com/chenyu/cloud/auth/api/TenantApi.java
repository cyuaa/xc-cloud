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
package com.chenyu.cloud.auth.api;

import com.chenyu.cloud.auth.model.TenantModel;
import com.chenyu.cloud.common.constants.FeignConstants;
import com.chenyu.cloud.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.chenyu.cloud.common.constants.FeignConstants.AUTH_TENANT;
import static com.chenyu.cloud.common.constants.FeignConstants.AUTH_TENANT_API;


/**
 *
 *
 * 对外 API 直接 暴露 @GetMapping 或者 @PostMapping
 * 对内也推荐 单机版 不需要设置 Mapping 但是调用方法得从Controller写起
 *
 *
 */
@FeignClient(name = FeignConstants.XC_CLOUD_AUTH, path = AUTH_TENANT, contextId = AUTH_TENANT_API)
public interface TenantApi {

    /** 标题 */
    String TITLE = "租户管理";
    /** 子标题 */
    String SUB_TITLE = "租户";

    /**
     * 租户 查一条
     * @param id
     * @return Result
     */
    @GetMapping("/{id}")
    Result<TenantModel> findById(@PathVariable("id") Integer id);

    /**
     * 租户 查询分页
     * @param pageNo 当前页
     * @param pageSize 每页条数
     * @param request request
     * @return Result
     */
    @GetMapping
    Result<?> page(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    );

    /**
     * 租户 新增
     * @param model 模型
     * @return Result
     */
    @PostMapping
    Result<?> insert(@RequestBody TenantModel model);

    /**
     * 租户 修改
     * @param model 模型
     * @return Result
     */
    @PutMapping
    Result<?> update(@RequestBody TenantModel model);

    /**
     * 租户 删除
     * @param id ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    Result<?> del(@PathVariable("id") Integer id);

    /**
     * 租户 批量删除
     * @param ids ID 数组
     * @return Result
     */
    @PostMapping("/batch")
    Result<?> delAll(@RequestBody List<Integer> ids);

    /**
     * 变更租户状态
     *
     * @param id 租户ID
     * @param status 状态
     * @return Result
     */
    @PutMapping("/status")
    Result<?> updateStatus(@RequestParam("id") Integer id, @RequestParam("status") Integer status);

    // =========================

    /**
     * 获得已启用租户 查一条
     * @param id 模型
     * @return Result
     */
    @GetMapping("/status/{status}")
    Result<TenantModel> findByStatus(@RequestParam Integer id, @PathVariable("status") Integer status);
}
