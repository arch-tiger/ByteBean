package com.github.archtiger.bytebean.core.model;

import com.github.archtiger.bytebean.api.method.MethodInvoker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 方法访问信息
 *
 * @param methodInvokerClass 方法访问器类
 * @param methods 方法列表
 * @param ok 是否成功
 * @author ZIJIDELU
 * @since 1.0.0
 */
public record MethodInvokerResult(
        Class<? extends MethodInvoker> methodInvokerClass,
        List<Method> methods,
        boolean ok
) {
    private static final MethodInvokerResult FAIL = new MethodInvokerResult(null, Collections.emptyList(), false);

    /**
     * 创建失败的访问结果
     *
     * @return 失败的访问结果
     */
    public static MethodInvokerResult fail() {
        return FAIL;
    }

    /**
     * 创建成功的访问结果
     *
     * @param methodAccessClass 方法访问器类
     * @param methods 方法列表
     * @return 成功的访问结果
     */
    public static MethodInvokerResult success(Class<? extends MethodInvoker> methodAccessClass, List<Method> methods) {
        return new MethodInvokerResult(methodAccessClass, methods, true);
    }
}
