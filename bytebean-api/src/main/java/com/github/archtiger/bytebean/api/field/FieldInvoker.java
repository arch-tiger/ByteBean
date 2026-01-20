package com.github.archtiger.bytebean.api.field;

/**
 * 字段访问器接口，定义了对类字段的通用访问方法。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/10 22:02
 */
public abstract class FieldInvoker {
    public abstract Object get(int index, Object instance);

    public abstract void set(int index, Object instance, Object value);

    // 基本类型 getter
    public abstract byte getByte(int index, Object instance);

    public abstract short getShort(int index, Object instance);

    public abstract int getInt(int index, Object instance);

    public abstract long getLong(int index, Object instance);

    public abstract float getFloat(int index, Object instance);

    public abstract double getDouble(int index, Object instance);

    public abstract boolean getBoolean(int index, Object instance);

    public abstract char getChar(int index, Object instance);

    // 基本类型 setter
    public abstract void setByte(int index, Object instance, byte value);

    public abstract void setShort(int index, Object instance, short value);

    public abstract void setInt(int index, Object instance, int value);

    public abstract void setLong(int index, Object instance, long value);

    public abstract void setFloat(int index, Object instance, float value);

    public abstract void setDouble(int index, Object instance, double value);

    public abstract void setBoolean(int index, Object instance, boolean value);

    public abstract void setChar(int index, Object instance, char value);
}
