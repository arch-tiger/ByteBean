package com.github.archtiger.bytebean.core.utils;

import java.util.Arrays;

/**
 * 异常工具类
 *
 * @author ArchTiger
 * @date 2026/1/13 23:36
 */
public class ExceptionUtil {
    public static IllegalArgumentException fieldNotGet(String fieldName) {
        return new IllegalArgumentException("Cannot get field: " + fieldName);
    }

    public static IllegalArgumentException fieldNotSet(String fieldName) {
        return new IllegalArgumentException("Cannot set field: " + fieldName);
    }

    public static IllegalArgumentException methodNotFound(String methodName, Class<?>[] parameterTypes) {
        return new IllegalArgumentException(String.format("Cannot find method %s with parameter types: [%s]",
                methodName,
                Arrays.toString(parameterTypes)
        ));
    }

    public static IllegalArgumentException constructorNotFound(Class<?>[] parameterTypes) {
        return new IllegalArgumentException(String.format("Cannot find constructor with parameter types: [%s]",
                Arrays.toString(parameterTypes)
        ));
    }
}
