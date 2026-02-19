package com.github.archtiger.bytebean.core.model;

import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

/**
 * 构造器访问信息
 *
 * @param constructorInvokerClass 构造器访问器类
 * @param constructors 构造器列表
 * @param ok 是否成功
 * @author ZIJIDELU
 * @since 1.0.0
 */
public record ConstructorInvokerResult(
        Class<? extends ConstructorInvoker> constructorInvokerClass,
        List<Constructor<?>> constructors,
        boolean ok
) {
    private static final ConstructorInvokerResult FAIL = new ConstructorInvokerResult(null, Collections.emptyList(), false);

    /**
     * 创建失败的访问结果
     *
     * @return 失败的访问结果
     */
    public static ConstructorInvokerResult fail() {
        return FAIL;
    }

    /**
     * 创建成功的访问结果
     *
     * @param constructorAccessClass 构造器访问器类
     * @param constructors 构造器列表
     * @return 成功的访问结果
     */
    public static ConstructorInvokerResult success(Class<? extends ConstructorInvoker> constructorAccessClass, List<Constructor<?>> constructors) {
        return new ConstructorInvokerResult(constructorAccessClass, constructors, true);
    }
}
