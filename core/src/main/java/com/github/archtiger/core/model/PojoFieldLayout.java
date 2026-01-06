package com.github.archtiger.core.model;

import com.github.archtiger.core.exception.InvalidFieldIndexException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * POJO 字段布局
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 00:01
 */
public final class PojoFieldLayout implements FieldLayout {
    private final Field[] fields;
    private final List<String> fieldNames;
    private final Map<String, Integer> nameIndexMap;

    private PojoFieldLayout(Field[] fields,
                            List<String> fieldNames,
                            Map<String, Integer> nameIndexMap) {
        this.fields = fields;
        this.fieldNames = fieldNames;
        this.nameIndexMap = nameIndexMap;
    }

    /**
     * 创建 POJO 字段布局
     *
     * @param clazz POJO 类
     * @return POJO 字段布局
     */
    public static PojoFieldLayout of(Class<?> clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields.length == 0) {
            return new PojoFieldLayout(
                    new Field[0],
                    Collections.emptyList(),
                    Collections.emptyMap()
            );
        }

        final Map<String, Integer> nameIndexMap = new ConcurrentHashMap<>();
        final AtomicInteger index = new AtomicInteger(0);
        final List<Field> fieldList = new CopyOnWriteArrayList<>();
        for (Field declaredField : declaredFields) {
            if (!isFieldAccessible(declaredField)) {
                continue;
            }

            fieldList.add(declaredField);
            nameIndexMap.put(declaredField.getName(), index.getAndIncrement());
        }

        if (fieldList.isEmpty()) {
            return new PojoFieldLayout(
                    new Field[0],
                    Collections.emptyList(),
                    Collections.emptyMap()
            );
        }
        final Map<String, Integer> nameIndexFinalMap = Collections.unmodifiableMap(nameIndexMap);
        final Field[] fields = fieldList.toArray(new Field[0]);
        final List<String> names = fieldList.stream().map(Field::getName).toList();
        return new PojoFieldLayout(fields, names, nameIndexFinalMap);
    }

    /**
     * 检查字段是否可访问
     *
     * @param field 字段
     * @return 如果字段可访问则返回 true，否则返回 false
     */
    private static boolean isFieldAccessible(Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers) && !Modifier.isStatic(modifiers);
    }

    @Override
    public int fieldCount() {
        return fields.length;
    }

    @Override
    public int indexOf(String name) {
        Integer i = nameIndexMap.get(name);
        if (i == null) {
            return -1;
        }
        return i;
    }

    @Override
    public String fieldName(int index) {
        if (containsIndex(index)) {
            return fields[index].getName();
        }

        throw new InvalidFieldIndexException(index);
    }

    @Override
    public Class<?> fieldType(int index) {
        if (containsIndex(index)) {
            return fields[index].getType();
        }

        throw new InvalidFieldIndexException(index);
    }

    @Override
    public boolean containsIndex(int index) {
        return index >= 0 && index < fields.length;
    }

    @Override
    public boolean isFieldReadable(int index) {
        return containsIndex(index);
    }

    @Override
    public boolean isFieldWritable(int index) {
        if (!containsIndex(index)) {
            return false;
        }
        Field field = fields[index];
        return !Modifier.isFinal(field.getModifiers());
    }


    @Override
    public List<String> fieldNames() {
        return fieldNames;
    }

}
