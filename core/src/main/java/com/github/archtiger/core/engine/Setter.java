package com.github.archtiger.core.engine;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface Setter {
    /**
     * 设置字段值
     *
     * @param target 目标对象
     * @param value  要设置的值
     */
    void set(Object target, Object value);
}
