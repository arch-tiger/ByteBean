package com.github.archtiger.core.engine;

import com.github.archtiger.core.model.PojoFieldLayout;

/**
 * 字段访问器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 00:01
 */
public interface FieldAccessor {
    Object get(Object target, int index);

    void set(Object target, int index, Object value);

    PojoFieldLayout layout();
}
