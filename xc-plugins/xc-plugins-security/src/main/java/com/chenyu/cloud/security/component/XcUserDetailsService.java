package com.chenyu.cloud.security.component;

import com.chenyu.cloud.auth.model.RoleModel;
import com.chenyu.cloud.auth.model.UserModel;
import com.chenyu.cloud.auth.rest.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证service, 获取用户信息
 * Created by JackyChen on 2021/04/29.
 */
@Component
public class XcUserDetailsService implements UserDetailsService {

    @Autowired
    private UserApi userApi;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userApi.findByUsername(username);
        if (null == userModel){
            throw new UsernameNotFoundException("用户名不存在!");
        }

        List<RoleModel> roles = userApi.getRoleModelsByUserId(userModel.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(roleModel -> new SimpleGrantedAuthority(roleModel.getName())).collect(Collectors.toList());
        return new XcUserDetails(userModel.getUsername(), userModel.getPassword(), true, true, true, true, authorities);
    }
}
