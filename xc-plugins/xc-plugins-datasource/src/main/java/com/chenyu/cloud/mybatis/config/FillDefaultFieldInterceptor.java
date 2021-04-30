package com.chenyu.cloud.mybatis.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import com.chenyu.cloud.common.constants.MyBatisConstants;
import com.chenyu.cloud.security.util.UserUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 填充默认字段(createTime, updateTime, createdBy, updateBy)
 * Created by JackyChen on 2021/04/29.
 */
@Data
@Accessors(chain = true)
@Intercepts({@Signature(
        type = org.apache.ibatis.executor.Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class})})
public class FillDefaultFieldInterceptor implements Interceptor {

    private static final String ET = "et";

    /** 实体类字段 */
    static private final Map<Class<?>, Field[]> ENTITY_FIELD_MAP = new HashMap<>();

    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        // 获取 SQL 命令
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取参数
        Object[] parameters = invocation.getArgs();

        for (Object parameter : parameters) {
            //第一个参数处理。根据它判断是否给“操作属性”赋值。
            //如果是第一个参数 MappedStatement
            if (parameter instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) parameter;
                sqlCommandType = ms.getSqlCommandType();
                //如果是“增加”或“更新”操作，则继续进行默认操作信息赋值。否则，则退出
                if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
                    continue;
                } else {
                    break;
                }
            }

            if (sqlCommandType == SqlCommandType.INSERT) {
                // 新增
                this.insertFill(parameter);

            } else if (sqlCommandType == SqlCommandType.UPDATE) {
                // 修改
                this.updateFill(parameter);
            }
        }

        return invocation.proceed();
    }

    /**
     * 新增数据
     * @param arg
     */
    public void insertFill(Object arg) {
        if(arg == null ){
            return;
        }

        // 当前时间
        Date currDate = DateUtil.date();

        // 字段缓存 减少每次更新 反射
        Field[] fields = ENTITY_FIELD_MAP.get(arg.getClass());
        if(fields == null){
            fields = ReflectUtil.getFields(arg.getClass());
            ENTITY_FIELD_MAP.put(arg.getClass(), fields);
        }

        for (Field f : fields) {

            switch (f.getName()) {
                // 创建人
                case MyBatisConstants.FIELD_CREATE_BY:
                    // 如果创建人 为空则进行默认赋值
                    Object createValue = ReflectUtil.getFieldValue(arg, f.getName());
                    if(StringUtils.isBlank(Convert.toStr(createValue))){
                        BeanUtil.setProperty(arg, MyBatisConstants.FIELD_CREATE_BY, UserUtil.getUser().getId());
                    }
                    break;
                // 更新人
                case MyBatisConstants.FIELD_UPDATE_BY:
                    // 如果更新人 为空则进行默认赋值
                    Object updateValue = ReflectUtil.getFieldValue(arg, f.getName());
                    if(StringUtils.isBlank(Convert.toStr(updateValue))){
                        BeanUtil.setProperty(arg, MyBatisConstants.FIELD_UPDATE_BY, UserUtil.getUser().getId());
                    }
                    break;
                // 创建日期
                case MyBatisConstants.FIELD_CREATE_TIME:
                    BeanUtil.setProperty(arg, MyBatisConstants.FIELD_CREATE_TIME, currDate);
                    break;
                // 更新日期
                case MyBatisConstants.FIELD_UPDATE_TIME:
                    BeanUtil.setProperty(arg, MyBatisConstants.FIELD_UPDATE_TIME, currDate);
                    break;
                // 乐观锁
                case MyBatisConstants.FIELD_OPTIMISTIC_LOCK:
                    BeanUtil.setProperty(arg, MyBatisConstants.FIELD_OPTIMISTIC_LOCK, 0);
                    break;
                // 多租户设置
                case MyBatisConstants.FIELD_TENANT:
                    // 2020-12-05 修复当前租户可能为空字符串报错问题
                    // 如果租户ID 为空则进行默认赋值
                    Object tenantValue = ReflectUtil.getFieldValue(arg, f.getName());
                    if(StringUtils.isBlank(Convert.toStr(tenantValue))){
                        BeanUtil.setProperty(arg, MyBatisConstants.FIELD_TENANT,  UserUtil.getTenantId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 修改数据
     * @param arg
     */
    public void updateFill(Object arg) {
        if(arg == null ){
            return;
        }

        // 排除字段
        List<String> existField = Lists.newArrayList();

        // 2020-09-19
        // 修改这儿 有可能会拿到一个 MapperMethod，需要特殊处理
        if (arg instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) arg;
            if (paramMap.containsKey(ET)) {
                arg = paramMap.get("et");
            } else {
                arg = paramMap.get("param1");
            }
            if (arg == null) {
                return;
            }
        }

        // 字段缓存 减少每次更新 反射
        Field[] fields = ENTITY_FIELD_MAP.get(arg.getClass());
        if(fields == null){
            fields = ReflectUtil.getFields(arg.getClass());
            ENTITY_FIELD_MAP.put(arg.getClass(), fields);
        }

        for (Field f : fields) {
            // 判断是否是排除字段
            if(existField.contains(f.getName())){
                continue;
            }

            switch (f.getName()) {
                // 更新人
                case MyBatisConstants.FIELD_UPDATE_BY:
                    // 如果更新人 为空则进行默认赋值
                    Object updateValue = ReflectUtil.getFieldValue(arg, f.getName());
                    if(StringUtils.isBlank(Convert.toStr(updateValue))){
                        BeanUtil.setProperty(arg, MyBatisConstants.FIELD_UPDATE_BY, UserUtil.getUser().getId());
                    }
                    break;
                // 更新日期
                case MyBatisConstants.FIELD_UPDATE_TIME:
                    BeanUtil.setProperty(arg, MyBatisConstants.FIELD_UPDATE_TIME, DateUtil.date());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof org.apache.ibatis.executor.Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        this.properties = prop;
    }

}
