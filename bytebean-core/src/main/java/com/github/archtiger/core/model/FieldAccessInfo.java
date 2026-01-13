package com.github.archtiger.core.model;

import com.github.archtiger.definition.invoker.FieldAccess;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 字段访问信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 18:55
 */
public record FieldAccessInfo(
        Class<? extends FieldAccess> fieldAccessClass,
        List<Field> fields,
        boolean ok
) {
    private static final FieldAccessInfo FAIL = new FieldAccessInfo(null, Collections.emptyList(), false);

    public static FieldAccessInfo fail() {
        return FAIL;
    }

    public static FieldAccessInfo success(Class<? extends FieldAccess> fieldAccessClass, List<Field> fields) {
        return new FieldAccessInfo(fieldAccessClass, fields, true);
    }
}
