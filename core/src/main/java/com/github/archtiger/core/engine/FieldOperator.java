package com.github.archtiger.core.engine;

import com.github.archtiger.core.model.FieldLayout;

public final class FieldOperator {

    private final FieldAccessor accessor;
    private final FieldLayout layout;

    public FieldOperator(FieldAccessor accessor) {
        this.accessor = accessor;
        this.layout = accessor.fieldLayout();
    }

    public Object get(Object bean, String field) {
        int idx = layout.indexOf(field);
        return accessor.get(bean, idx);
    }

    public void set(Object bean, String field, Object value) {
        int idx = layout.indexOf(field);
        accessor.set(bean, idx, value);
    }

    public FieldLayout layout() {
        return layout;
    }
}
