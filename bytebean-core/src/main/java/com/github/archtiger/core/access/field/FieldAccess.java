package com.github.archtiger.core.access.field;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/10 22:02
 */
public interface FieldAccess {
    Object get(int index, Object instance);

    void set(int index, Object instance, Object value);

    int getInt(int index, Object instance);

    void setInt(int index, Object instance, int value);
}
