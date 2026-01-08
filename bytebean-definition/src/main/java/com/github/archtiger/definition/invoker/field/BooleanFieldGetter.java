package com.github.archtiger.definition.invoker.field;

/**
 * BOOLEAN获取器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface BooleanFieldGetter {
    /**
     * 获取字段值
     *
     * @param target 目标对象
     * @return 字段值
     */
    boolean get(Object target);
}
