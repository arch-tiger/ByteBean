package com.github.archtiger.core.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 字段布局接口
 * 提供字段数量、索引、类型、可写性等信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 00:01
 */
public interface FieldLayout {

    /**
     * 获取字段数量
     *
     * @return 字段数量
     */
    int fieldCount();

    /**
     * 根据字段名获取索引
     *
     * @param name 字段名
     * @return 字段索引，不存在返回 -1
     */
    int indexOf(String name);

    /**
     * 根据索引获取字段名
     *
     * @param index 字段索引
     * @return 字段名
     */
    String fieldName(int index);

    /**
     * 根据索引获取字段类型
     *
     * @param index 字段索引
     * @return 字段类型
     */
    Class<?> fieldType(int index);

    /**
     * 检查索引是否有效
     *
     * @param index 字段索引
     * @return 如果索引有效则返回 true，否则返回 false
     */
    boolean containsIndex(int index);

    /**
     * 检查字段是否可读
     *
     * @param index 字段索引
     * @return 如果字段可读则返回 true，否则返回 false
     */
    boolean isFieldReadable(int index);

    /**
     * 检查字段是否可写
     *
     * @param index 字段索引
     * @return 如果字段可写则返回 true，否则返回 false
     */
    boolean isFieldWritable(int index);

    /**
     * 获取所有字段名
     *
     * @return 所有字段名列表
     */
    List<String> fieldNames();

    /**
     * 获取字段名到索引的映射
     *
     * @return 字段名到索引的映射
     */
    Map<String, Integer> nameIndexMap();

    /**
     * 获取所有字段
     *
     * @return 所有字段列表
     */
    List<Field> fields();
}
