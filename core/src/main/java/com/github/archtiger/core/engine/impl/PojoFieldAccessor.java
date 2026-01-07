package com.github.archtiger.core.engine.impl;

import com.github.archtiger.core.engine.FieldAccessor;
import com.github.archtiger.core.model.FieldLayout;
import com.github.archtiger.core.model.PojoFieldLayout;

/**
 * POJO 字段访问器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 11:12
 */
public final class PojoFieldAccessor implements FieldAccessor {
    private final FieldLayout layout;

    private PojoFieldAccessor(FieldLayout layout) {
        this.layout = layout;
    }

    public static FieldAccessor of(Class<?> clazz) {
        return new PojoFieldAccessor(PojoFieldLayout.of(clazz));
    }

    @Override
    public Object get(Object target, int index) {
        return null;
    }

    @Override
    public Object get(Object target, String fieldName) {
        return null;
    }

    @Override
    public void set(Object target, int index, Object value) {

    }

    @Override
    public void set(Object target, String fieldName, Object value) {

    }

    @Override
    public FieldLayout fieldLayout() {
        return layout;
    }
}
