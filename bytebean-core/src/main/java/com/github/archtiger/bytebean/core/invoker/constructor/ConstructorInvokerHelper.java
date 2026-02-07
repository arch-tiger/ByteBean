package com.github.archtiger.bytebean.core.invoker.constructor;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;
import com.github.archtiger.bytebean.core.model.ConstructorInvokerResult;
import com.github.archtiger.bytebean.core.support.ByteBeanConstant;
import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 构造器访问助手
 *
 * @author ZIJIDELU
 * @datetime 2026/1/13 11:32
 */
public class ConstructorInvokerHelper extends ConstructorInvoker {
    private static final Map<Class<?>, ConstructorInvokerHelper> CONSTRUCTOR_INVOKER_HELPER_CACHE = new WeakKeyValueConcurrentMap<>();
    private final ConstructorInvoker constructorInvoker;
    private final Class<?>[][] constructorParameterTypes;

    private ConstructorInvokerHelper(ConstructorInvoker constructorInvoker, Class<?>[][] constructorParameterTypes) {
        this.constructorInvoker = constructorInvoker;
        this.constructorParameterTypes = constructorParameterTypes;
    }

    /**
     * 创建 ConstructorAccessHelper 实例
     *
     * @param targetClass 目标类
     * @return ConstructorAccessHelper 实例
     */
    public static ConstructorInvokerHelper of(Class<?> targetClass) {
        return CONSTRUCTOR_INVOKER_HELPER_CACHE.computeIfAbsent(targetClass, k -> {
            final List<Constructor<?>> constructors = ByteBeanReflectUtil.getConstructors(targetClass);
            final Class<?>[][] constructorParameterTypes = constructors
                    .stream()
                    .map(Constructor::getParameterTypes)
                    .toArray(Class[][]::new);

            // 构造器数量小于等于阈值时，使用字节码调用
            if (constructors.size() <= ByteBeanConstant.CONSTRUCTOR_SHARDING_THRESHOLD_VALUE) {
                final ConstructorInvokerResult constructorInvokerResult = ConstructorInvokerGenerator.generate(targetClass);
                if (!constructorInvokerResult.ok()) {
                    return null;
                }

                try {
                    final ConstructorInvoker constructorInvoker = constructorInvokerResult.constructorInvokerClass().getDeclaredConstructor().newInstance();
                    return new ConstructorInvokerHelper(constructorInvoker, constructorParameterTypes);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            final ConstructorHandleInvoker constructorHandleInvoker = ConstructorHandleInvoker.of(targetClass);
            return new ConstructorInvokerHelper(constructorHandleInvoker, constructorParameterTypes);
        });

    }

    /**
     * 获取构造器索引
     *
     * @param paramTypes 构造器参数类型
     * @return 构造器索引
     */
    public int getConstructorIndex(Class<?>... paramTypes) {
        for (int i = 0, n = constructorParameterTypes.length; i < n; i++) {
            if (Arrays.equals(paramTypes, constructorParameterTypes[i])) {
                return i;
            }
        }

        return ExceptionCode.INVALID_INDEX;
    }

    /**
     * 获取构造器索引或抛出异常
     *
     * @param paramTypes 构造器参数类型
     * @return 构造器索引
     */
    public int getConstructorIndexOrThrow(Class<?>... paramTypes) {
        int constructorIndex = getConstructorIndex(paramTypes);
        if (constructorIndex == ExceptionCode.INVALID_INDEX) {
            throw ExceptionUtil.constructorNotFound(paramTypes);
        }

        return constructorIndex;
    }

    @Override
    public Object newInstance(int index, Object... args) {
        return constructorInvoker.newInstance(index, args);
    }

    @Override
    public Object newInstance() {
        return constructorInvoker.newInstance();
    }
}
