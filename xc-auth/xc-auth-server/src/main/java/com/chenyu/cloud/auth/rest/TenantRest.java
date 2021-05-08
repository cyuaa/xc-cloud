package com.chenyu.cloud.auth.rest;

import com.chenyu.cloud.auth.api.TenantApi;
import com.chenyu.cloud.auth.model.TenantModel;
import com.chenyu.cloud.common.response.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 租户Rest
 * Created by JackyChen on 2021/04/28.
 */
@Api(tags = TenantApi.TITLE)
@RestController
@RequestMapping("/tenant")
public class TenantRest {


    public Result<TenantModel> findById(Integer id) {
        return null;
    }

    public Result<?> page(Integer pageNo, Integer pageSize, HttpServletRequest request) {
        return null;
    }

    public Result<?> insert(TenantModel model) {
        return null;
    }

    public Result<?> update(TenantModel model) {
        return null;
    }

    public Result<?> del(Integer id) {
        return null;
    }

    public Result<?> delAll(List<Integer> ids) {
        return null;
    }

    public Result<?> updateStatus(Integer id, Integer status) {
        return null;
    }

    public Result<TenantModel> findByStatus(Integer id, Integer status) {
        return null;
    }
}
