package com.github.archtiger.core.engine;

import com.github.archtiger.core.model.PojoFieldLayout;

public final class FieldOperator {

    private final FieldAccessor accessor;
    private final PojoFieldLayout layout;

    public FieldOperator(FieldAccessor accessor) {
        this.accessor = accessor;
        this.layout = accessor.layout();
    }

    public Object get(Object bean, String field) {
        int idx = layout.indexOf(field);
        return accessor.get(bean, idx);
    }

    public void set(Object bean, String field, Object value) {
        int idx = layout.indexOf(field);
        accessor.set(bean, idx, value);
    }

    public PojoFieldLayout layout() {
        return layout;
    }
}
