package com.github.archtiger.core.model;

import com.github.archtiger.core.access.constructor.ConstructorAccess;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

/**
 * 构造器访问信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public record ConstructorAccessInfo(
        Class<? extends ConstructorAccess> constructorAccessClass,
        List<Constructor<?>> constructors,
        boolean ok
) {
    private static final ConstructorAccessInfo FAIL = new ConstructorAccessInfo(null, Collections.emptyList(), false);

    public static ConstructorAccessInfo fail() {
        return FAIL;
    }

    public static ConstructorAccessInfo success(Class<? extends ConstructorAccess> constructorAccessClass, List<Constructor<?>> constructors) {
        return new ConstructorAccessInfo(constructorAccessClass, constructors, true);
    }
}
