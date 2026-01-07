package com.github.archtiger.core.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * 调用器名称信息类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7 20:43
 */
public record InvokerNameInfo(
        Class<?> targetClass,
        Class<?> declaringClass,
        Class<?> invokerType,
        String memberName,
        Class<?>[] parameterTypes
) {

    private static final String PREFIX = "$ByteBean";
    private static final String SEP = "$";

    /**
     * Field
     */
    public static InvokerNameInfo forField(
            Class<?> targetClass, Field field, Class<?> invokerType) {

        return new InvokerNameInfo(
                targetClass,
                field.getDeclaringClass(),
                invokerType,
                field.getName(),
                new Class<?>[]{field.getType()}
        );
    }

    /**
     * Method Invoker
     */
    public static InvokerNameInfo forMethod(
            Class<?> targetClass, Method method, Class<?> invokerType) {

        return new InvokerNameInfo(
                targetClass,
                method.getDeclaringClass(),
                invokerType,
                method.getName(),
                method.getParameterTypes()
        );
    }

    /**
     * Constructor Invoker
     */
    public static InvokerNameInfo forConstructor(
            Class<?> targetClass, Constructor<?> ctor, Class<?> invokerType) {

        return new InvokerNameInfo(
                targetClass,
                ctor.getDeclaringClass(),
                invokerType,
                "<init>",
                ctor.getParameterTypes()
        );
    }

    /**
     * 计算最终生成的 Invoker 类名
     */
    public String calcInvokerClassName() {
        StringJoiner joiner = new StringJoiner(SEP);

        // 1. 目标类
        joiner.add(targetClass.getName());

        // 2. 框架前缀
        joiner.add(PREFIX);

        // 3. Invoker 类型
        joiner.add(invokerType.getSimpleName());

        // 4. 成员名（父类需要区分）
        if (!targetClass.equals(declaringClass)) {
            joiner.add(declaringClass.getSimpleName());
        }
        joiner.add(memberName);

        // 5. 参数个数
        joiner.add(Integer.toString(parameterTypes.length));

        // 6. 参数签名 hash（构造器 / 方法）
        joiner.add(calcHshType(parameterTypes));

        return joiner.toString();
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

