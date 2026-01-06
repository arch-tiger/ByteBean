package com.github.archtiger.core.engine;

import com.github.archtiger.core.model.PojoFieldLayout;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/6 11:12
 */
public final class PojoFieldAccessor implements FieldAccessor {
    private final PojoFieldLayout layout;
    private final FieldAccessor accessor;

    public PojoFieldAccessor(PojoFieldLayout layout, FieldAccessor accessor) {
        this.layout = layout;
        this.accessor = accessor;
    }

    @Override
    public Object get(Object target, int index) {
        return null;
    }

    @Override
    public void set(Object target, int index, Object value) {

    }

    @Override
    public PojoFieldLayout layout() {
        return layout;
    }
}
