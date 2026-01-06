package com.github.archtiger.core.engine;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface Getter {
    /**
     * 获取字段值
     *
     * @param target 目标对象
     * @return 字段值
     */
    Object get(Object target);
}
