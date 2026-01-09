package com.github.archtiger.definition.invoker.field;

/**
 * double设置器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface DoubleFieldSetter {
    /**
     * 设置字段值
     *
     * @param target 目标对象
     * @param value  要设置的值
     */
    void set(Object target, double value);
}
