package com.github.archtiger.core.model;

import com.github.archtiger.core.access.method.MethodAccess;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 方法访问信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:00
 */
public record MethodAccessInfo(
        Class<? extends MethodAccess> methodAccessClass,
        List<Method> methods,
        boolean ok
) {
    private static final MethodAccessInfo FAIL = new MethodAccessInfo(null, Collections.emptyList(), false);

    public static MethodAccessInfo fail() {
        return FAIL;
    }

    public static MethodAccessInfo success(Class<? extends MethodAccess> methodAccessClass, List<Method> methods) {
        return new MethodAccessInfo(methodAccessClass, methods, true);
    }
}
