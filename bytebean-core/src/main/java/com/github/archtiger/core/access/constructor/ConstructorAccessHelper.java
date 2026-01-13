package com.github.archtiger.core.access.constructor;

import cn.hutool.core.util.ClassUtil;
import com.github.archtiger.core.model.ConstructorAccessInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * 构造器访问助手
 *
 * @author ZIJIDELU
 * @datetime 2026/1/13 11:32
 */
public class ConstructorAccessHelper {
    private final ConstructorAccess constructorAccess;
    private final Class<?>[][] constructorParameterTypes;

    private ConstructorAccessHelper(ConstructorAccessInfo constructorAccessInfo) {
        this.constructorParameterTypes = constructorAccessInfo.constructors()
                .stream()
                .map(Constructor::getParameterTypes)
                .toArray(Class[][]::new);
        try {
            this.constructorAccess = constructorAccessInfo.constructorAccessClass().getDeclaredConstructor().newInstance();
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
    public static ConstructorAccessHelper of(Class<?> targetClass) {
        ConstructorAccessInfo constructorAccessInfo = ConstructorAccessGenerator.generate(targetClass);
        if (!constructorAccessInfo.ok()) {
            return null;
        }

        return new ConstructorAccessHelper(constructorAccessInfo);
    }

    /**
     * 获取构造器访问实例
     *
     * @return 构造器访问实例
     */
    public ConstructorAccess getConstructorAccess() {
        return constructorAccess;
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

        return -1;
    }

    /**
     * 创建新实例
     *
     * @param args 构造器参数
     * @return 新实例
     */
    public Object newInstance(Object... args) {
        Class<?>[] classes = ClassUtil.getClasses(args);
        return constructorAccess.newInstance(getConstructorIndex(classes), args);
    }

}
