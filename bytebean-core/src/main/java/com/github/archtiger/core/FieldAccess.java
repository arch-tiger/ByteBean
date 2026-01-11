package com.github.archtiger.core;

/**
 * 字段访问接口
 * <p>
 * 提供基于索引的字段访问能力，包括通用方法和基本类型专用方法。
 * 基本类型专用方法避免了自动装箱拆箱，提供更好的性能。
 * </p>
 *
 * @author ZIJIDELU
 * @datetime 2026/1/10 22:02
 */
public interface FieldAccess {
    /**
     * 获取指定索引的字段值
     * <p>
     * 适用于引用类型字段，或需要统一处理的场景。
     * 对于基本类型字段，建议使用对应的专用方法（如 {@link #getInt}）以避免装箱开销。
     * </p>
     *
     * @param index    字段索引，与字段声明顺序一致
     * @param instance 目标对象实例
     * @return 字段值（基本类型会自动装箱）
     */
    Object get(int index, Object instance);

    /**
     * 设置指定索引的字段值
     * <p>
     * 适用于引用类型字段，或需要统一处理的场景。
     * 对于基本类型字段，建议使用对应的专用方法（如 {@link #setInt}）以避免拆箱开销。
     * </p>
     *
     * @param index    字段索引，与字段声明顺序一致
     * @param instance 目标对象实例
     * @param value    要设置的值
     */
    void set(int index, Object instance, Object value);

    // ============================================================
    // 基本类型专用方法 - 避免装箱拆箱，提供最佳性能
    // ============================================================

    int getInt(int index, Object instance);

    long getLong(int index, Object instance);

    boolean getBoolean(int index, Object instance);

    double getDouble(int index, Object instance);

    float getFloat(int index, Object instance);

    byte getByte(int index, Object instance);

    short getShort(int index, Object instance);

    char getChar(int index, Object instance);

    void setInt(int index, Object instance, int value);

    void setLong(int index, Object instance, long value);

    void setBoolean(int index, Object instance, boolean value);

    void setDouble(int index, Object instance, double value);

    void setFloat(int index, Object instance, float value);

    void setByte(int index, Object instance, byte value);

    void setShort(int index, Object instance, short value);

    void setChar(int index, Object instance, char value);
}
