package com.github.archtiger.extensions;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/10 22:02
 */
public interface FieldAccessBak {
    Object get(int index, Object instance);
    void set(int index, Object instance, Object value);
}
