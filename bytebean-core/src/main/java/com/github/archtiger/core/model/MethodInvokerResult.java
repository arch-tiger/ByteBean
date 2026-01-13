package com.github.archtiger.core.model;

import com.github.archtiger.definition.method.MethodInvoker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 方法访问信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:00
 */
public record MethodInvokerResult(
        Class<? extends MethodInvoker> methodAccessClass,
        List<Method> methods,
        boolean ok
) {
    private static final MethodInvokerResult FAIL = new MethodInvokerResult(null, Collections.emptyList(), false);

    public static MethodInvokerResult fail() {
        return FAIL;
    }

    public static MethodInvokerResult success(Class<? extends MethodInvoker> methodAccessClass, List<Method> methods) {
        return new MethodInvokerResult(methodAccessClass, methods, true);
    }
}
