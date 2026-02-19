package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.api.field.FieldInvoker;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 基于VarHandle的字段访问器，为大量字段场景提供高性能字段读写能力。
 * <p>
 * 当类的字段数量超过阈值（默认500）时，使用VarHandle实现而非字节码生成。
 * VarHandle是Java 9引入的轻量级对象引用和数组访问API，性能接近直接字节码访问。
 * <p>
 * <b>特点：</b>
 * <ul>
 *   <li>使用privateLookupIn支持私有字段访问</li>
 *   <li>相比反射调用，性能提升约3-5倍</li>
 *   <li>内存占用小，无字节码生成开销</li>
 * </ul>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public final class FieldVarHandleInvoker extends FieldInvoker {

    /**
     * MethodHandles.Lookup实例，用于创建VarHandle。
     */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * VarHandle数组，按字段索引排列。
     * 每个VarHandle对应一个字段，支持直接读写。
     */
    private final VarHandle[] varHandles;

    private FieldVarHandleInvoker(VarHandle[] varHandles) {
        this.varHandles = varHandles;
    }

    /**
     * 创建基于VarHandle的字段访问器
     *
     * @param targetClass 目标类
     * @return FieldVarHandleInvoker 实例
     */
    public static FieldVarHandleInvoker of(Class<?> targetClass) {
        List<Field> fields = ByteBeanReflectUtil.getFields(targetClass);
        VarHandle[] varHandles = new VarHandle[fields.size()];
        try {
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(targetClass, LOOKUP);
            for (int i = 0; i < varHandles.length; i++) {
                varHandles[i] = privateLookup.findVarHandle(targetClass, fields.get(i).getName(), fields.get(i).getType());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return new FieldVarHandleInvoker(varHandles);
    }

    @Override
    public Object get(int index, Object instance) {
        return varHandles[index].get(instance);
    }

    @Override
    public void set(int index, Object instance, Object value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public byte getByte(int index, Object instance) {
        return (byte) varHandles[index].get(instance);
    }

    @Override
    public short getShort(int index, Object instance) {
        return (short) varHandles[index].get(instance);
    }

    @Override
    public int getInt(int index, Object instance) {
        return (int) varHandles[index].get(instance);
    }

    @Override
    public long getLong(int index, Object instance) {
        return (long) varHandles[index].get(instance);
    }

    @Override
    public float getFloat(int index, Object instance) {
        return (float) varHandles[index].get(instance);
    }

    @Override
    public double getDouble(int index, Object instance) {
        return (double) varHandles[index].get(instance);
    }

    @Override
    public boolean getBoolean(int index, Object instance) {
        return (boolean) varHandles[index].get(instance);
    }

    @Override
    public char getChar(int index, Object instance) {
        return (char) varHandles[index].get(instance);
    }

    @Override
    public void setByte(int index, Object instance, byte value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setShort(int index, Object instance, short value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setInt(int index, Object instance, int value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setLong(int index, Object instance, long value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setFloat(int index, Object instance, float value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setDouble(int index, Object instance, double value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setBoolean(int index, Object instance, boolean value) {
        varHandles[index].set(instance, value);
    }

    @Override
    public void setChar(int index, Object instance, char value) {
        varHandles[index].set(instance, value);
    }
}
