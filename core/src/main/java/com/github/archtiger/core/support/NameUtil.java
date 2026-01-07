package com.github.archtiger.core.support;

import com.github.archtiger.core.invoke.ConstructorInvoker;
import com.github.archtiger.core.invoke.FieldGetter;
import com.github.archtiger.core.invoke.FieldSetter;
import com.github.archtiger.core.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * 名称工具类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7 13:37
 */
public class NameUtil {
    private static final String OPERATOR_PREFIX = "$ByteBean";
    private static final String SPLITTER = "$";

    /**
     * 计算构造函数调用器的名称
     *
     * @param targetClass 目标类
     * @param constructor 构造函数
     * @return 构造函数调用器的名称
     */
    public static String calcForConstructorInvoker(Class<?> targetClass, Constructor<?> constructor) {
        return new StringJoiner(SPLITTER)
                .add(targetClass.getName())
                .add(OPERATOR_PREFIX)
                .add(ConstructorInvoker.class.getSimpleName())
                .add(Integer.toString(constructor.getParameterCount()))
                .add(calcHshType(constructor.getParameterTypes()))
                .toString();
    }

    /**
     * 计算方法调用器的名称
     *
     * @param targetClass 目标类
     * @param method      方法
     * @return 方法调用器的名称
     */
    public static String calcForMethodInvoker(Class<?> targetClass, Method method) {
        final String methodName;
        if (targetClass != method.getDeclaringClass()) {
            methodName = method.getDeclaringClass().getSimpleName() + SPLITTER + method.getName();
        } else {
            methodName = method.getName();
        }

        return new StringJoiner(SPLITTER)
                .add(targetClass.getName())
                .add(OPERATOR_PREFIX)
                .add(MethodInvoker.class.getSimpleName())
                .add(methodName)
                .add(Integer.toString(method.getParameterCount()))
                .add(calcHshType(method.getParameterTypes()))
                .toString();
    }

    /**
     * 计算字段获取器的名称
     *
     * @param targetClass 目标类
     * @param field       字段
     * @return 字段获取器的名称
     */
    public static String calcForFieldGetter(Class<?> targetClass, Field field) {
        final String fieldName;
        if (targetClass != field.getDeclaringClass()) {
            fieldName = field.getDeclaringClass().getSimpleName() + SPLITTER + field.getName();
        } else {
            fieldName = field.getName();
        }
        return new StringJoiner(SPLITTER)
                .add(targetClass.getName())
                .add(OPERATOR_PREFIX)
                .add(FieldGetter.class.getSimpleName())
                .add(fieldName)
                .toString();
    }

    /**
     * 计算字段设置器的名称
     *
     * @param targetClass 目标类
     * @param field       字段
     * @return 字段设置器的名称
     */
    public static String calcForFieldSetter(Class<?> targetClass, Field field) {
        final String fieldName;
        if (targetClass != field.getDeclaringClass()) {
            fieldName = field.getDeclaringClass().getSimpleName() + SPLITTER + field.getName();
        } else {
            fieldName = field.getName();
        }
        return new StringJoiner(SPLITTER)
                .add(targetClass.getName())
                .add(OPERATOR_PREFIX)
                .add(FieldSetter.class.getSimpleName())
                .add(fieldName)
                .toString();
    }

    /**
     * 计算参数类型的哈希值
     *
     * @param types 参数类型数组
     * @return 参数类型的哈希值
     */
    private static String calcHshType(Class<?>[] types) {
        return Integer.toHexString(Arrays.hashCode(types));
    }

}
