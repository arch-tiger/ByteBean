package com.github.archtiger.core.invoker;

import com.github.archtiger.core.invoker.field.FieldInvokerGenerator;
import com.github.archtiger.core.model.FieldInvokerResult;
import com.github.archtiger.definition.field.FieldInvoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * FieldInvokerHelper 类
 *
 * @author archtiger
 * @datetime 2026/01/13 17:00
 */
public class FieldInvokerHelper {
    private final FieldInvoker fieldInvoker;
    private final String[] fieldNames;
    private final int[] modifiers;

    private FieldInvokerHelper(FieldInvokerResult fieldInvokerResult) {
        this.fieldNames = fieldInvokerResult.fields().stream().map(Field::getName).toArray(String[]::new);
        this.modifiers = fieldInvokerResult.fields().stream().mapToInt(Field::getModifiers).toArray();
        try {
            this.fieldInvoker = fieldInvokerResult.fieldAccessClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static FieldInvokerHelper of(Class<?> targetClass) {
        FieldInvokerResult fieldInvokerResult = FieldInvokerGenerator.generate(targetClass);
        if (!fieldInvokerResult.ok()) {
            return null;
        }

        return new FieldInvokerHelper(fieldInvokerResult);
    }

    public FieldInvoker getFieldInvoker() {
        return fieldInvoker;
    }

    public int getFieldIndex(String fieldName) {
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(fieldName)) {
                return i;
            }
        }

        return -1;
    }

    public Object get(Object instance, String fieldName) {
        return fieldInvoker.get(getFieldIndex(fieldName), instance);
    }

    public void set(Object instance, String fieldName, Object value) {
        int index = getFieldIndex(fieldName);
        int modifier = modifiers[index];
        if (Modifier.isFinal(modifier)) {
            throw new UnsupportedOperationException("final field cannot be set");
        }

        fieldInvoker.set(getFieldIndex(fieldName), instance, value);
    }
}
