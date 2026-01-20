package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.api.field.FieldInvoker;
import com.github.archtiger.bytebean.core.model.FieldInvokerResult;
import com.github.archtiger.bytebean.core.support.ByteBeanConstant;
import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * FieldInvokerHelper 类
 *
 * @author archtiger
 * @datetime 2026/01/13 17:00
 */
public class FieldInvokerHelper extends FieldInvoker {
    private final FieldInvoker fieldInvoker;
    private final String[] fieldNames;
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
        List<Field> fields = ByteBeanReflectUtil.getFields(targetClass);
        if (fields.isEmpty()) {
            return null;
        }

        String[] fieldNames = fields.stream().map(Field::getName).toArray(String[]::new);
        int[] modifiers = fields.stream().mapToInt(Field::getModifiers).toArray();

        if (fields.size() > ByteBeanConstant.FIELD_SHARDING_THRESHOLD_VALUE) {
            FieldVarHandleInvoker fieldVarHandleInvoker = FieldVarHandleInvoker.of(targetClass);
            return new FieldInvokerHelper(fieldVarHandleInvoker, fieldNames, modifiers);
        }

        FieldInvokerResult fieldInvokerResult = FieldInvokerGenerator.generate(targetClass);
        if (!fieldInvokerResult.ok()) {
            return null;
        }

        try {
            FieldInvoker fieldInvoker = fieldInvokerResult.fieldInvokerClass().getDeclaredConstructor().newInstance();
            return new FieldInvokerHelper(fieldInvoker, fieldNames, modifiers);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

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
