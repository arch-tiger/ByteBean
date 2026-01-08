package com.github.archtiger.access.engine;

import com.github.archtiger.access.model.FieldLayout;

/**
 * 字段访问器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 00:01
 */
public interface FieldAccessor {
    /**
     * 获取字段值
     *
     * @param target 目标对象
     * @param index  字段索引
     * @return 字段值
     */
    Object get(Object target, int index);

    /**
     * 获取字段值
     *
     * @param target    目标对象
     * @param fieldName 字段名
     * @return 字段值
     */
    Object get(Object target, String fieldName);

    /**
     * 设置字段值
     *
     * @param target 目标对象
     * @param index  字段索引
     * @param value  字段值
     */
    void set(Object target, int index, Object value);

    /**
     * 设置字段值
     *
     * @param target    目标对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    void set(Object target, String fieldName, Object value);

    /**
     * 获取字段布局
     *
     * @return 字段布局
     */
    FieldLayout fieldLayout();
}
