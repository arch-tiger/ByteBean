package com.github.archtiger.bytebean.core.invoker;

import cn.hutool.core.util.ClassUtil;
import com.github.archtiger.bytebean.core.invoker.constructor.ConstructorInvokerGenerator;
import com.github.archtiger.bytebean.core.model.ConstructorInvokerResult;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;
import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * 构造器访问助手
 *
 * @author ZIJIDELU
 * @datetime 2026/1/13 11:32
 */
public class ConstructorInvokerHelper {
    private final ConstructorInvoker constructorInvoker;
    private final Class<?>[][] constructorParameterTypes;

    private ConstructorInvokerHelper(ConstructorInvokerResult constructorInvokerResult) {
        this.constructorParameterTypes = constructorInvokerResult.constructors()
                .stream()
                .map(Constructor::getParameterTypes)
                .toArray(Class[][]::new);
        try {
            this.constructorInvoker = constructorInvokerResult.constructorAccessClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 ConstructorAccessHelper 实例
     *
     * @param targetClass 目标类
     * @return ConstructorAccessHelper 实例
     */
    public static ConstructorInvokerHelper of(Class<?> targetClass) {
        ConstructorInvokerResult constructorInvokerResult = ConstructorInvokerGenerator.generate(targetClass);
        if (!constructorInvokerResult.ok()) {
            return null;
        }

        return new ConstructorInvokerHelper(constructorInvokerResult);
    }

    /**
     * 获取构造器访问实例
     *
     * @return 构造器访问实例
     */
    public ConstructorInvoker getConstructorInvoker() {
        return constructorInvoker;
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

    /**
     * 创建新实例
     *
     * @param args 构造器参数
     * @return 新实例
     */
    public Object newInstance(Object... args) {
        int constructorIndex = getConstructorIndexOrThrow(ClassUtil.getClasses(args));

        return constructorInvoker.newInstance(constructorIndex, args);
    }

}
