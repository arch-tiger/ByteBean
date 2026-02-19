package com.github.archtiger.bytebean.core.model;

import com.github.archtiger.bytebean.api.field.FieldInvoker;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 字段访问信息
 *
 * @param fieldInvokerClass 字段访问器类
 * @param fields 字段列表
 * @param ok 是否成功
 * @author ZIJIDELU
 * @since 1.0.0
 */
public record FieldInvokerResult(
        Class<? extends FieldInvoker> fieldInvokerClass,
        List<Field> fields,
        boolean ok
) {
    private static final FieldInvokerResult FAIL = new FieldInvokerResult(null, Collections.emptyList(), false);

    /**
     * 创建失败的访问结果
     *
     * @return 失败的访问结果
     */
    public static FieldInvokerResult fail() {
        return FAIL;
    }

    /**
     * 创建成功的访问结果
     *
     * @param fieldAccessClass 字段访问器类
     * @param fields 字段列表
     * @return 成功的访问结果
     */
    public static FieldInvokerResult success(Class<? extends FieldInvoker> fieldAccessClass, List<Field> fields) {
        return new FieldInvokerResult(fieldAccessClass, fields, true);
    }
}
