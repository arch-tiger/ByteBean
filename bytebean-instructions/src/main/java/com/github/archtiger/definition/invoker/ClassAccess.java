package com.github.archtiger.definition.invoker;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/10 19:51
 */
public interface ClassAccess {
    Object get(int index);

    void set(int index, Object value);

    Object invoke(int index, Object... args);

    Object newInstance(int index, Object... args);
}
