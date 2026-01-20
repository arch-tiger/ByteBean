package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.model.FieldInvokerResult;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;
import com.github.archtiger.bytebean.api.field.FieldInvoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * FieldInvokerHelper 类
 *
 * @author archtiger
 * @datetime 2026/01/13 17:00
 */
public class FieldInvokerHelper {
    private final FieldInvoker fieldInvoker;
    private final String[] fieldNames;
    private final int[] modifiers;

    private FieldInvokerHelper(FieldInvokerResult fieldInvokerResult) {
        this.fieldNames = fieldInvokerResult.fields().stream().map(Field::getName).toArray(String[]::new);
        this.modifiers = fieldInvokerResult.fields().stream().mapToInt(Field::getModifiers).toArray();
        try {
            this.fieldInvoker = fieldInvokerResult.fieldInvokerClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 FieldInvokerHelper 实例
     *
     * @param targetClass 目标类
     * @return FieldInvokerHelper 实例，若生成失败则返回 null
     */
    public static FieldInvokerHelper of(Class<?> targetClass) {
        FieldInvokerResult fieldInvokerResult = FieldInvokerGenerator.generate(targetClass);
        if (!fieldInvokerResult.ok()) {
            return null;
        }

        return new FieldInvokerHelper(fieldInvokerResult);
    }

    /**
     * 获取字段访问器
     *
     * @return 字段访问器
     */
    public FieldInvoker getFieldInvoker() {
        return fieldInvoker;
    }

    /**
     * 获取字段索引
     *
     * @param fieldName 字段名
     * @return 字段索引，若不存在则返回 -1
     */
    public int getFieldGetterIndex(String fieldName) {
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(fieldName)) {
                return i;
            }
        }

        return ExceptionCode.INVALID_INDEX;
    }

    /**
     * 获取字段获取器索引，若不存在则抛出异常
     */
    public int getFieldGetterIndexOrThrow(String fieldName) {
        int fieldGetterIndex = getFieldGetterIndex(fieldName);
        if (fieldGetterIndex == ExceptionCode.INVALID_INDEX) {
            throw ExceptionUtil.fieldNotGet(fieldName);
        }

        return fieldGetterIndex;
    }

    /**
     * 获取字段设置器索引
     *
     * @param fieldName 字段名
     * @return 字段设置器索引，若不存在则返回 -1
     */
    public int getFieldSetterIndex(String fieldName) {
        for (int i = 0; i < fieldNames.length; i++) {
            // 跳过final字段
            if (!Modifier.isFinal(modifiers[i]) && fieldNames[i].equals(fieldName)) {
                return i;
            }
        }

        return ExceptionCode.INVALID_INDEX;
    }

    /**
     * 获取字段设置器索引，若不存在则抛出异常
     */
    public int getFieldSetterIndexOrThrow(String fieldName) {
        int fieldSetterIndex = getFieldSetterIndex(fieldName);
        if (fieldSetterIndex == ExceptionCode.INVALID_INDEX) {
            throw ExceptionUtil.fieldNotSet(fieldName);
        }

        return fieldSetterIndex;
    }

    /**
     * 获取字段值
     *
     * @param instance  实例对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public Object get(Object instance, String fieldName) {
        int fieldGetterIndex = getFieldGetterIndexOrThrow(fieldName);

        return fieldInvoker.get(fieldGetterIndex, instance);
    }

    /**
     * 设置字段值
     *
     * @param instance  实例对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    public void set(Object instance, String fieldName, Object value) {
        int fieldSetterIndex = getFieldSetterIndexOrThrow(fieldName);

        fieldInvoker.set(fieldSetterIndex, instance, value);
    }
}
