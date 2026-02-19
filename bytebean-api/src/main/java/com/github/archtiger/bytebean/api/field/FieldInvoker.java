package com.github.archtiger.bytebean.api.field;

/**
 * 字段访问器抽象类，提供高性能的字段读写能力。
 * <p>
 * 该接口定义了对类字段的通用访问方法，支持引用类型和所有基本类型的读写操作。
 * 实现类通常通过字节码生成或MethodHandle技术来优化字段访问性能，
 * 比传统反射方式快数倍。
 * </p>
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * FieldInvoker invoker = FieldInvokerHelper.of(MyClass.class);
 * int ageIndex = invoker.getFieldSetterIndexOrThrow("age");
 *
 * // 写入字段
 * invoker.setInt(ageIndex, instance, 25);
 *
 * // 读取字段
 * int age = invoker.getInt(ageIndex, instance);
 * }</pre>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public abstract class FieldInvoker {

    /**
     * 读取指定索引的字段值（引用类型）。
     *
     * @param index    字段索引，通过 {@code FieldInvokerHelper} 获取
     * @param instance 目标对象实例，非null
     * @return 字段值，如果字段为null则返回null
     * @throws IllegalArgumentException 如果索引超出范围
     * @throws RuntimeException       如果字段访问失败
     */
    public abstract Object get(int index, Object instance);

    /**
     * 设置指定索引的字段值（引用类型）。
     *
     * @param index    字段索引，通过 {@code FieldInvokerHelper} 获取
     * @param instance 目标对象实例，非null
     * @param value    要设置的值，可为null
     * @throws IllegalArgumentException 如果索引超出范围或字段为final
     * @throws RuntimeException       如果字段写入失败
     */
    public abstract void set(int index, Object instance, Object value);

    // 基本类型 getter

    /**
     * 读取指定索引的 byte 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return byte 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 byte
     */
    public abstract byte getByte(int index, Object instance);

    /**
     * 读取指定索引的 short 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return short 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 short
     */
    public abstract short getShort(int index, Object instance);

    /**
     * 读取指定索引的 int 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return int 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 int
     */
    public abstract int getInt(int index, Object instance);

    /**
     * 读取指定索引的 long 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return long 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 long
     */
    public abstract long getLong(int index, Object instance);

    /**
     * 读取指定索引的 float 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return float 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 float
     */
    public abstract float getFloat(int index, Object instance);

    /**
     * 读取指定索引的 double 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return double 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 double
     */
    public abstract double getDouble(int index, Object instance);

    /**
     * 读取指定索引的 boolean 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return boolean 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 boolean
     */
    public abstract boolean getBoolean(int index, Object instance);

    /**
     * 读取指定索引的 char 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @return char 类型字段值
     * @throws IllegalArgumentException 如果索引超出范围或字段类型不是 char
     */
    public abstract char getChar(int index, Object instance);

    // 基本类型 setter

    /**
     * 设置指定索引的 byte 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 byte 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 byte
     */
    public abstract void setByte(int index, Object instance, byte value);

    /**
     * 设置指定索引的 short 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 short 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 short
     */
    public abstract void setShort(int index, Object instance, short value);

    /**
     * 设置指定索引的 int 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 int 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 int
     */
    public abstract void setInt(int index, Object instance, int value);

    /**
     * 设置指定索引的 long 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 long 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 long
     */
    public abstract void setLong(int index, Object instance, long value);

    /**
     * 设置指定索引的 float 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 float 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 float
     */
    public abstract void setFloat(int index, Object instance, float value);

    /**
     * 设置指定索引的 double 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 double 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 double
     */
    public abstract void setDouble(int index, Object instance, double value);

    /**
     * 设置指定索引的 boolean 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 boolean 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 boolean
     */
    public abstract void setBoolean(int index, Object instance, boolean value);

    /**
     * 设置指定索引的 char 类型字段值。
     *
     * @param index    字段索引
     * @param instance 目标对象实例，非null
     * @param value    要设置的 char 值
     * @throws IllegalArgumentException 如果索引超出范围、字段为final或字段类型不是 char
     */
    public abstract void setChar(int index, Object instance, char value);
}
