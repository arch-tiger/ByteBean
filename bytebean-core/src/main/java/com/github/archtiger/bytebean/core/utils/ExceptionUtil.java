package com.github.archtiger.bytebean.core.utils;

import java.util.Arrays;

/**
 * 异常工具类，提供统一的异常创建方法。
 * <p>
 * 该类封装了ByteBean中常用的异常创建逻辑，确保异常消息格式的一致性。
 * </p>
 *
 * @author ArchTiger
 * @since 1.0.0
 */
public class ExceptionUtil {

    /**
     * 私有构造函数，防止实例化。
     */
    private ExceptionUtil() {
    }

    /**
     * 创建表示字段无法读取的IllegalArgumentException。
     *
     * @param fieldName 字段名称
     * @return IllegalArgumentException实例
     */
    public static IllegalArgumentException fieldNotGet(String fieldName) {
        return new IllegalArgumentException("Cannot get field: " + fieldName);
    }

    /**
     * 创建表示字段无法写入的IllegalArgumentException。
     *
     * @param fieldName 字段名称
     * @return IllegalArgumentException实例
     */
    public static IllegalArgumentException fieldNotSet(String fieldName) {
        return new IllegalArgumentException("Cannot set field: " + fieldName);
    }

    /**
     * 创建表示方法未找到的IllegalArgumentException。
     *
     * @param methodName    方法名称
     * @param parameterTypes 参数类型数组
     * @return IllegalArgumentException实例，包含方法名和参数类型信息
     */
    public static IllegalArgumentException methodNotFound(String methodName, Class<?>[] parameterTypes) {
        return new IllegalArgumentException(String.format("Cannot find method %s with parameter types: [%s]",
                methodName,
                Arrays.toString(parameterTypes)
        ));
    }

    /**
     * 创建表示构造器未找到的IllegalArgumentException。
     *
     * @param parameterTypes 参数类型数组
     * @return IllegalArgumentException实例，包含参数类型信息
     */
    public static IllegalArgumentException constructorNotFound(Class<?>[] parameterTypes) {
        return new IllegalArgumentException(String.format("Cannot find constructor with parameter types: [%s]",
                Arrays.toString(parameterTypes)
        ));
    }
}
