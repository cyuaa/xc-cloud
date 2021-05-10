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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.chenyu.cloud.auth.api.UserApi;
import com.chenyu.cloud.auth.model.MenuModel;
import com.chenyu.cloud.auth.model.UserModel;
import com.chenyu.cloud.common.exception.TokenException;
import com.chenyu.cloud.common.properties.GlobalProperties;
import com.chenyu.cloud.common.response.CommonMsg;
import com.chenyu.cloud.common.response.CoreMsg;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.common.response.TokenMsg;
import com.chenyu.cloud.core.util.CacheUtil;
import com.chenyu.cloud.core.util.DistributedLockUtil;
import com.chenyu.cloud.security.threadlocal.TokenThreadLocal;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chenyu.cloud.common.constants.OrderConstants.UTIL_ORDER;

/**
 * 获取用户信息Util
 * Created by JackyChen on 2021/4/29.
 */
@Slf4j
@Order(UTIL_ORDER)
@Component
@Lazy(false)
public class UserUtil {

    /** 前缀 */
    public static final String PREFIX_ID = "userId:";
    public static final String PREFIX_ID_ROLES = "userId:roles:";
    public static final String PREFIX_ID_PERMISSIONS = "userId:permissions:";
    public static final String PREFIX_ID_MENUS = "userId:menus:";
    public static final String PREFIX_USERNAME = "username:";

    /** 修改租户权限 */
    private static final String PERMS_TENANT = "system_user_tenant";

    /** 用户Service */
    private static UserApi userApi;

    /** 超级管理员 */
    public static String SUPER_ADMIN;

    /**
     * 获得当前系统登陆用户
     * @return UserModel
     */
    public static UserModel getUser(){
        String token = TokenThreadLocal.get();

        // 如果还是没获取到token 则抛出异常
        if(StringUtils.isEmpty(token)){
            // Token失效，请重新登录
            throw new TokenException(TokenMsg.EXCEPTION_TOKEN_LOSE_EFFICACY);
        }

        Integer userId = UserTokenUtil.getUserIdByToken(token);
        UserModel user = getUser(userId);
        if(user == null){
            // Token失效，请重新登录
            throw new TokenException(TokenMsg.EXCEPTION_TOKEN_LOSE_EFFICACY);
        }
        return user;
    }

    /**
     * 根据ID 获得用户
     * @param userId 用户ID
     * @return UserModel
     */
    public static UserModel getUser(Integer userId){

        // 缓存Key
        String cacheKey = PREFIX_ID + userId;

        // 先从缓存里拿
        UserModel userModel = CacheUtil.getTimed(UserModel.class, cacheKey);
        if (userModel != null){
            return userModel;
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
            userModel = CacheUtil.getTimed(UserModel.class, cacheKey);
            if (userModel != null){
                return userModel;
            }

            // 查询数据库
            // 设置为系统内部调用 否则 会拿到 空值
            UserModel model = userApi.findByIdInter(userModel.getId());
            if(null != model){
                userModel = model;
                // 存入缓存
                CacheUtil.put(cacheKey, userModel);
            }

        } catch (Exception e){
            log.error(e.getMessage(), e);
        } finally {
            // 释放锁
            DistributedLockUtil.unlock(cacheKey);
        }

        if(userModel == null){
            // 设置空变量 用于防止穿透判断
            CacheUtil.putNilFlag(cacheKey);
            return null;
        }

        return userModel;
    }


    /**
     * 根据 userName 获得用户
     * @param userName 用户名
     * @return UserModel
     */
    public static UserModel getUserByUserName(String userName){
        // 缓存Key
        String cacheKey = PREFIX_USERNAME + userName;

        // 先从缓存里拿
        UserModel userModel = CacheUtil.getTimed(UserModel.class, cacheKey);
        if (userModel != null){
            return userModel;
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
            userModel = CacheUtil.getTimed(UserModel.class, cacheKey);
            if (userModel != null) {
                return userModel;
            }

            // 查询数据库
            UserModel user = userApi.findByUsername(userName);
            if (null != user) {
                userModel = user;
                // 存入缓存
                CacheUtil.put(cacheKey, userModel);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            // 释放锁
            DistributedLockUtil.unlock(cacheKey);
        }

        if(userModel == null){
            // 设置空变量 用于防止穿透判断
            CacheUtil.putNilFlag(cacheKey);
            return null;
        }

        return userModel;
    }

    /**
     * 根据 userId 获得用户角色列表
     * @param userId 用户ID
     * @return List
     */
    public static List<Integer> getUserRolesByUserId(Integer userId){
        // 缓存Key
        String cacheKey = PREFIX_ID_ROLES + userId;

        List<Integer> roles;

        // 先从缓存里拿
        Object obj = CacheUtil.getTimed(cacheKey);
        roles = Convert.toList(Integer.class, obj);
        if(CollUtil.isNotEmpty(roles)){
            return roles;
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
            obj = CacheUtil.getTimed(cacheKey);
            roles = Convert.toList(Integer.class, obj);
            if(CollUtil.isNotEmpty(roles)){
                return roles;
            }

            // 查询数据库
            Result<List<Integer>> result = userApi.getRoleIdsByUserId(userId);
            if (CommonMsg.SUCCESS.getCode().equals(result.getCode())) {
                List<Integer> roleIdList = result.getData();
                if (null != roleIdList && roleIdList.size() > 0) {
                    roles = roleIdList;
                    // 存入缓存
                    CacheUtil.put(cacheKey, roles);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            // 释放锁
            DistributedLockUtil.unlock(cacheKey);
        }

        if(CollUtil.isEmpty(roles)){
            // 设置空变量 用于防止穿透判断
            CacheUtil.putNilFlag(cacheKey);
            return null;
        }

        return roles;
    }


    /**
     * 根据 userId 获得用户权限列表
     * @param userId 用户ID
     * @return List
     */
    public static List<String> getUserAllPermsByUserId(Integer userId){

        // 缓存Key
        String cacheKey = PREFIX_ID_PERMISSIONS + userId;

        List<String> permissions;

        // 先从缓存里拿
        Object obj = CacheUtil.getTimed(cacheKey);
        permissions = Convert.toList(String.class, obj);
        if(CollUtil.isNotEmpty(permissions)){
            return permissions;
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
            obj = CacheUtil.getTimed(cacheKey);
            permissions = Convert.toList(String.class, obj);
            if(CollUtil.isNotEmpty(permissions)){
                return permissions;
            }

            // 查询数据库
            // TODO: 改成使用feign需要修改 ---
            // List<String> perms = userApi.getAllPerms(userId);
            List<String> perms = Lists.newArrayList();
            if(null != perms && perms.size() > 0){
                permissions = perms;
                // 存入缓存
                CacheUtil.put(cacheKey, permissions);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            // 释放锁
            DistributedLockUtil.unlock(cacheKey);
        }

        if(CollUtil.isEmpty(permissions)){
            // 设置空变量 用于防止穿透判断
            CacheUtil.putNilFlag(cacheKey);
            return null;
        }

        return permissions;
    }

    /**
     * 根据 userId 获得用户菜单
     * @param userId 用户ID
     * @return List
     */
    public static List<MenuModel> getMenuListByUserId(Integer userId){

        // 缓存Key
        String cacheKey = PREFIX_ID_MENUS + userId;

        List<MenuModel> menus;

        // 先从缓存里拿
        Object obj = CacheUtil.getTimed(cacheKey);
        menus = Convert.toList(MenuModel.class, obj);
        if(CollUtil.isNotEmpty(menus)){
            return menus;
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
            obj = CacheUtil.getTimed(cacheKey);
            menus = Convert.toList(MenuModel.class, obj);
            if(CollUtil.isNotEmpty(menus)){
                return menus;
            }

            // 查询数据库
            // TODO: 改成使用feign需要修改 ---
            // List<MenuModel> menuModels = userApi.getMenuListByUserId(userId);
            List<MenuModel> menuModels = Lists.newArrayList();
            if(null != menuModels && menuModels.size() > 0){
                menus = menuModels;
                // 存入缓存
                CacheUtil.put(cacheKey, menus);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            // 释放锁
            DistributedLockUtil.unlock(cacheKey);
        }

        if(CollUtil.isEmpty(menus)){
            // 设置空变量 用于防止穿透判断
            CacheUtil.putNilFlag(cacheKey);
            return null;
        }

        return menus;
    }

    // ============== 刷新缓存 ==============

    /**
     * 刷新用户 - 删就完了
     * @param user 用户
     * @return boolean
     */
    public static boolean refreshUser(UserModel user){
        if(user == null || null != (user.getId())){
            return true;
        }

        UserModel userModelById = CacheUtil.getTimed(UserModel.class, PREFIX_ID + user.getId());
        UserModel userModelByUsername = CacheUtil.getTimed(UserModel.class, PREFIX_USERNAME + user.getUsername());

        boolean hasNilFlagById = CacheUtil.hasNilFlag(PREFIX_ID + user.getId());
        boolean hasNilFlagByName = CacheUtil.hasNilFlag(PREFIX_USERNAME + user.getUsername());

        // 计数器
        int count = 0;

        if (hasNilFlagById){
            count++;
            // 清除空拦截
            boolean tmp = CacheUtil.delNilFlag(PREFIX_ID + user.getId());
            if(tmp){
                count--;
            }
        }

        if (hasNilFlagByName){
            count++;
            // 清除空拦截
            boolean tmp = CacheUtil.delNilFlag(PREFIX_USERNAME + user.getUsername());
            if(tmp){
                count--;
            }
        }

        // 只要有一个不为空 则执行刷新
        if (userModelById != null){
            count++;
            // 先删除
            boolean tmp = CacheUtil.del(PREFIX_ID + user.getId());
            if(tmp){
                count--;
            }
        }

        if (userModelByUsername != null){
            count++;
            // 先删除
            boolean tmp = CacheUtil.del(PREFIX_USERNAME + user.getUsername());
            if(tmp){
                count--;
            }
        }

        return count == 0;
    }


    /**
     * 刷新用户角色 - 删就完了
     * @param userId 用户ID
     * @return boolean
     */
    public static boolean refreshUserRoles(Integer userId){
        Object obj = CacheUtil.getTimed(PREFIX_ID_ROLES + userId);
        boolean hasNilFlag = CacheUtil.hasNilFlag(PREFIX_ID_ROLES + userId);

        // 计数器
        int count = 0;

        // 只要不为空 则执行刷新
        if (hasNilFlag){
            count++;
            // 清除空拦截
            boolean tmp = CacheUtil.delNilFlag(PREFIX_ID_ROLES + userId);
            if(tmp){
                count--;
            }
        }

        if(obj != null){
            count++;
            // 先删除
            boolean tmp = CacheUtil.del(PREFIX_ID_ROLES + userId);
            if(tmp){
                count--;
            }
        }

        return count == 0;
    }

    /**
     * 刷新用户权限 - 删就完了
     * @param userId 用户ID
     * @return boolean
     */
    public static boolean refreshUserAllPerms(Integer userId){
        Object obj = CacheUtil.getTimed(PREFIX_ID_PERMISSIONS + userId);
        boolean hasNilFlag = CacheUtil.hasNilFlag(PREFIX_ID_PERMISSIONS + userId);

        // 计数器
        int count = 0;

        // 只要不为空 则执行刷新
        if (hasNilFlag){
            count++;
            // 清除空拦截
            boolean tmp = CacheUtil.delNilFlag(PREFIX_ID_PERMISSIONS + userId);
            if(tmp){
                count--;
            }
        }

        if(obj != null){
            count++;
            // 先删除
            boolean tmp = CacheUtil.del(PREFIX_ID_PERMISSIONS + userId);
            if(tmp){
                count--;
            }
        }


        return count == 0;
    }

    /**
     * 刷新用户菜单 - 删就完了
     * @param userId 用户ID
     * @return boolean
     */
    public static boolean refreshUserMenus(Integer userId){
        Object obj = CacheUtil.getTimed(PREFIX_ID_MENUS + userId);
        boolean hasNilFlag = CacheUtil.hasNilFlag(PREFIX_ID_MENUS + userId);

        // 计数器
        int count = 0;

        // 只要不为空 则执行刷新
        if (hasNilFlag){
            count++;
            // 清除空拦截
            boolean tmp = CacheUtil.delNilFlag(PREFIX_ID_MENUS + userId);
            if(tmp){
                count--;
            }
        }

        if(obj != null){
            count++;
            // 先删除
            boolean tmp = CacheUtil.del(PREFIX_ID_MENUS + userId);
            if(tmp){
                count--;
            }
        }

        return count == 0;
    }

    /**
     * 获得 租户ID
     * @return Integer
     */
    public static Integer getTenantId(){
        // 判断权限 如果是 admin 超级管理员 则租户ID清空 且findList 不做处理 否则默认都会做处理
        // 如果表中 没有 tenant_id 字段 则不进行多租户处理

        UserModel user = getUser();

        // 如果是超级管理员 则不进行租户处理
        if(StringUtils.equals(SUPER_ADMIN, user.getUsername())){
            return null;
        }
        return user.getTenantId();
    }

    /**
     * 获得 真实租户ID
     * @return Integer
     */
    public static Integer getRealTenantId(){
        UserModel user = getUser();
        return user.getTenantId();
    }

    /**
     * 是否有修改租户的权限
     * @param currUser model
     * @return boolean
     */
    public static boolean isHasUpdateTenantPerms(final UserModel currUser){
        // 排除超级管理员
        if(UserUtil.SUPER_ADMIN.equals(currUser.getUsername())){
            return true;
        }

        // 获得当前用户权限
        List<String> userAllPermsByUserId = UserUtil.getUserAllPermsByUserId(currUser.getId());
        return !CollUtil.isEmpty(userAllPermsByUserId) &&
                userAllPermsByUserId.contains(PERMS_TENANT);
    }

    /**
     * 处理密码
     * @param password 密码
     * @param secretKey 盐值
     * @return String
     */
    public static String handlePassword(String password, String secretKey){
        return MD5Util.encode(password, secretKey);
    }

    // =====================================


    /**
     * 初始化
     * @param globalProperties 配置类
     */
    @Autowired
    public void init(GlobalProperties globalProperties){
        if(globalProperties != null && globalProperties.getAuth() != null
                && globalProperties.getAuth().getToken() != null
            ){
            // 获得 超级管理员
            UserUtil.SUPER_ADMIN = globalProperties.getAuth().getSuperAdmin();
        }
    }

    // =====================================

    @Autowired
    @Lazy
    public void setUserApi(UserApi userApi) {
        UserUtil.userApi = userApi;
    }

}
