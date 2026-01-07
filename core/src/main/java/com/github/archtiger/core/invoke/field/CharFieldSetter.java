package com.github.archtiger.core.invoke.field;

/**
 * CHAR设置器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface CharFieldSetter {
    /**
     * 设置字段值
     *
     * @param target 目标对象
     * @param value  要设置的值
     */
    void set(Object target, char value);
}
