package com.github.archtiger.bytebean.core.invoker.field;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.api.field.FieldInvoker;
import com.github.archtiger.bytebean.core.model.FieldInvokerResult;
import com.github.archtiger.bytebean.core.constant.ByteBeanConstant;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;
import com.github.archtiger.bytebean.core.constant.ExceptionCode;
import com.github.archtiger.bytebean.core.utils.ExceptionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * 字段访问器Helper，提供字段索引管理和缓存能力。
 * <p>
 * 该类继承自{@link FieldInvoker}，在提供字段读写能力的同时，
 * 维护了字段名称到索引的映射，并支持按目标类进行缓存。
 * <p>
 * <b>特点：</b>
 * <ul>
 *   <li>使用WeakKeyValueConcurrentMap缓存，避免内存泄漏</li>
 *   <li>支持通过字段名获取索引</li>
 *   <li>当字段不存在或为final时，抛出IllegalArgumentException</li>
 *   <li>根据字段数量自动选择字节码或VarHandle实现</li>
 * </ul>
 *
 * @author archtiger
 * @since 1.0.0
 */
public class FieldInvokerHelper extends FieldInvoker {

    /**
     * FieldInvokerHelper缓存，按目标Class索引。
     * 使用WeakKeyValueConcurrentMap确保在目标Class被卸载时自动清除缓存。
     */
    private static final Map<Class<?>, FieldInvokerHelper> FIELD_INVOKER_HELPER_CACHE = new WeakKeyValueConcurrentMap<>();

    /**
     * 实际的字段访问器实现，可能是字节码生成或VarHandle实现。
     */
    private final FieldInvoker fieldInvoker;

    /**
     * 字段名称数组，按索引顺序排列。
     */
    private final String[] fieldNames;

    /**
     * 字段修饰符数组，按索引顺序排列。
     * 用于判断字段是否为final、static等。
     */
    private final int[] modifiers;

    private FieldInvokerHelper(FieldInvoker fieldInvoker, String[] fieldNames, int[] modifiers) {
        this.fieldInvoker = fieldInvoker;
        this.fieldNames = fieldNames;
        this.modifiers = modifiers;
    }

    /**
     * 创建 FieldInvokerHelper 实例
     *
     * @param targetClass 目标类
     * @return FieldInvokerHelper 实例，若生成失败则返回 null
     */
    public static FieldInvokerHelper of(Class<?> targetClass) {
        return FIELD_INVOKER_HELPER_CACHE.computeIfAbsent(targetClass, k -> {
            final List<Field> fields = ByteBeanReflectUtil.getFields(targetClass);
            if (fields.isEmpty()) {
                return null;
            }

            final String[] fieldNames = fields.stream().map(Field::getName).toArray(String[]::new);
            final int[] modifiers = fields.stream().mapToInt(Field::getModifiers).toArray();

            // 若字段数量小于等于阈值，则使用 FieldInvokerGenerator 生成 FieldInvoker
            if (fields.size() <= ByteBeanConstant.FIELD_SHARDING_THRESHOLD_VALUE) {
                final FieldInvokerResult fieldInvokerResult = FieldInvokerGenerator.generate(targetClass);
                if (fieldInvokerResult.ok()) {
                    try {
                        final FieldInvoker fieldInvoker = fieldInvokerResult.fieldInvokerClass().getDeclaredConstructor().newInstance();
                        return new FieldInvokerHelper(fieldInvoker, fieldNames, modifiers);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            final FieldVarHandleInvoker fieldVarHandleInvoker = FieldVarHandleInvoker.of(targetClass);
            return new FieldInvokerHelper(fieldVarHandleInvoker, fieldNames, modifiers);
        });

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
     *
     * @param fieldName 字段名
     * @return 字段获取器索引
     * @throws IllegalArgumentException 当字段不存在时抛出
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
     *
     * @param fieldName 字段名
     * @return 字段设置器索引
     * @throws IllegalArgumentException 当字段不存在或为final时抛出
     */
    public int getFieldSetterIndexOrThrow(String fieldName) {
        int fieldSetterIndex = getFieldSetterIndex(fieldName);
        if (fieldSetterIndex == ExceptionCode.INVALID_INDEX) {
            throw ExceptionUtil.fieldNotSet(fieldName);
        }

        return fieldSetterIndex;
    }

    @Override
    public Object get(int index, Object instance) {
        return fieldInvoker.get(index, instance);
    }

    @Override
    public void set(int index, Object instance, Object value) {
        fieldInvoker.set(index, instance, value);
    }

    @Override
    public byte getByte(int index, Object instance) {
        return fieldInvoker.getByte(index, instance);
    }

    @Override
    public short getShort(int index, Object instance) {
        return fieldInvoker.getShort(index, instance);
    }

    @Override
    public int getInt(int index, Object instance) {
        return fieldInvoker.getInt(index, instance);
    }

    @Override
    public long getLong(int index, Object instance) {
        return fieldInvoker.getLong(index, instance);
    }

    @Override
    public float getFloat(int index, Object instance) {
        return fieldInvoker.getFloat(index, instance);
    }

    @Override
    public double getDouble(int index, Object instance) {
        return fieldInvoker.getDouble(index, instance);
    }

    @Override
    public boolean getBoolean(int index, Object instance) {
        return fieldInvoker.getBoolean(index, instance);
    }

    @Override
    public char getChar(int index, Object instance) {
        return fieldInvoker.getChar(index, instance);
    }

    @Override
    public void setByte(int index, Object instance, byte value) {
        fieldInvoker.setByte(index, instance, value);
    }

    @Override
    public void setShort(int index, Object instance, short value) {
        fieldInvoker.setShort(index, instance, value);
    }

    @Override
    public void setInt(int index, Object instance, int value) {
        fieldInvoker.setInt(index, instance, value);
    }

    @Override
    public void setLong(int index, Object instance, long value) {
        fieldInvoker.setLong(index, instance, value);
    }

    @Override
    public void setFloat(int index, Object instance, float value) {
        fieldInvoker.setFloat(index, instance, value);
    }

    @Override
    public void setDouble(int index, Object instance, double value) {
        fieldInvoker.setDouble(index, instance, value);
    }

    @Override
    public void setBoolean(int index, Object instance, boolean value) {
        fieldInvoker.setBoolean(index, instance, value);
    }

    @Override
    public void setChar(int index, Object instance, char value) {
        fieldInvoker.setChar(index, instance, value);
    }
}
