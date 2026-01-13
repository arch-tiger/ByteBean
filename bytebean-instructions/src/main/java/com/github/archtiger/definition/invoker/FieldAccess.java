package com.github.archtiger.definition.invoker;

/**
 * 字段访问器接口，定义了对类字段的通用访问方法。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/10 22:02
 */
public interface FieldAccess {
    Object get(int index, Object instance);

    void set(int index, Object instance, Object value);

    // 基本类型 getter
    byte getByte(int index, Object instance);

    short getShort(int index, Object instance);

    int getInt(int index, Object instance);

    long getLong(int index, Object instance);

    float getFloat(int index, Object instance);

    double getDouble(int index, Object instance);

    boolean getBoolean(int index, Object instance);

    char getChar(int index, Object instance);

    // 基本类型 setter
    void setByte(int index, Object instance, byte value);

    void setShort(int index, Object instance, short value);

    void setInt(int index, Object instance, int value);

    void setLong(int index, Object instance, long value);

    void setFloat(int index, Object instance, float value);

    void setDouble(int index, Object instance, double value);

    void setBoolean(int index, Object instance, boolean value);

    void setChar(int index, Object instance, char value);
}
