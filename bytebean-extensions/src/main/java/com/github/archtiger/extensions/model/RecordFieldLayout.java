package com.github.archtiger.extensions.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 记录字段布局
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 15:36
 */
public class RecordFieldLayout implements FieldLayout {
    @Override
    public int fieldCount() {
        return 0;
    }

    @Override
    public int indexOf(String name) {
        return 0;
    }

    @Override
    public String fieldName(int index) {
        return "";
    }

    @Override
    public Class<?> fieldType(int index) {
        return null;
    }

    @Override
    public boolean containsIndex(int index) {
        return false;
    }

    @Override
    public boolean isFieldReadable(int index) {
        return false;
    }

    @Override
    public boolean isFieldWritable(int index) {
        return false;
    }

    @Override
    public List<String> fieldNames() {
        return List.of();
    }

    @Override
    public Map<String, Integer> nameIndexMap() {
        return Map.of();
    }

    @Override
    public List<Field> fields() {
        return List.of();
    }
}
