package com.github.archtiger.core.invoke;

/**
 * SHORT获取器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface ShortFieldGetter {
    /**
     * 获取字段值
     *
     * @param target 目标对象
     * @return 字段值
     */
    short get(Object target);
}
