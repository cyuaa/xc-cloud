package com.chenyu.cloud.auth.fallback;

import com.chenyu.cloud.auth.api.UserApi;
import com.chenyu.cloud.auth.dto.UserPasswordDto;
import com.chenyu.cloud.auth.model.*;
import com.chenyu.cloud.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * Created by JackyChen on 2021/05/08.
 */
@Component
@Slf4j
public class UserFallbackFactory implements FallbackFactory<UserApi> {
    @Override
    public UserApi create(Throwable cause) {
        return new UserApi() {
            @Override
            public Result<UserInfo> getInfo() {
                return null;
            }

            @Override
            public Result<UserInfo> findById(Integer userId) {
                return null;
            }

            @Override
            public UserModel findByIdInter(Integer userId) {
                return null;
            }

            @Override
            public Result<UserOrgRefModel> getOrg() {
                return null;
            }

            @Override
            public Result<UserOrgRefModel> getOrgByUserId(Integer userId) {
                return null;
            }

            @Override
            public Result<List<Integer>> getRoleIdsByUserId(Integer userId) {
                return null;
            }

            @Override
            public Result<?> updatePassword(UserPasswordDto userPassword) {
                return null;
            }

            @Override
            public Result<?> resetPasswordById(Integer userId) {
                return null;
            }

            @Override
            public Result<?> updateStatus(Integer userId, Integer enable) {
                return null;
            }

            @Override
            public Result<?> updateIcon(MultipartHttpServletRequest request, Integer userId) {
                return null;
            }

            @Override
            public Result<?> page(Integer pageNo, Integer pageSize, UserOrgRefModel org) {
                return null;
            }

            @Override
            public Result<?> update(UserModel model) {
                return null;
            }

            @Override
            public Result<?> del(Integer id) {
                return null;
            }

            @Override
            public Result<?> delAll(List<Integer> ids) {
                return null;
            }

            @Override
            public UserModel findByUsername(String username) {
                log.error("User Feign Get Data Failed: [{}]", cause.getMessage());
                cause.printStackTrace();
                return null;
            }

            @Override
            public List<RoleModel> getRoleModelsByUserId(Integer userId) {
                return null;
            }

            @Override
            public List<String> getAllPerms(Integer userId) {
                return null;
            }

            @Override
            public List<MenuModel> getMenuListByUserId(Integer userId) {
                return null;
            }

            @Override
            public Result<UserOrgRefModel> getOrgInfoByUserId(Integer userId) {
                return null;
            }
        };

    }
}
