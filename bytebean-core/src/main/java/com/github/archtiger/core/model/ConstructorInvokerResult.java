package com.github.archtiger.core.model;

import com.github.archtiger.definition.invoker.constructor.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

/**
 * 构造器访问信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public record ConstructorInvokerResult(
        Class<? extends ConstructorInvoker> constructorAccessClass,
        List<Constructor<?>> constructors,
        boolean ok
) {
    private static final ConstructorInvokerResult FAIL = new ConstructorInvokerResult(null, Collections.emptyList(), false);

    public static ConstructorInvokerResult fail() {
        return FAIL;
    }

    public static ConstructorInvokerResult success(Class<? extends ConstructorInvoker> constructorAccessClass, List<Constructor<?>> constructors) {
        return new ConstructorInvokerResult(constructorAccessClass, constructors, true);
    }
}
