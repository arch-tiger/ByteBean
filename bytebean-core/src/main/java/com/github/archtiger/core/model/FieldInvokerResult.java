package com.github.archtiger.core.model;

import com.github.archtiger.definition.field.FieldInvoker;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 字段访问信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 18:55
 */
public record FieldInvokerResult(
        Class<? extends FieldInvoker> fieldAccessClass,
        List<Field> fields,
        boolean ok
) {
    private static final FieldInvokerResult FAIL = new FieldInvokerResult(null, Collections.emptyList(), false);

    public static FieldInvokerResult fail() {
        return FAIL;
    }

    public static FieldInvokerResult success(Class<? extends FieldInvoker> fieldAccessClass, List<Field> fields) {
        return new FieldInvokerResult(fieldAccessClass, fields, true);
    }
}
