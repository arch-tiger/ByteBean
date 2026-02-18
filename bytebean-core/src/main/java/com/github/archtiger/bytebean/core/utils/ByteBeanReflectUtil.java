package com.github.archtiger.bytebean.core.utils;

import cn.hutool.core.util.ReflectUtil;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 反射工具类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/13 18:02
 */
public class ByteBeanReflectUtil {

    /**
     * 获取指定类的字段集合
     * <p>
     * 获取当前类所有，非static，非private字段
     *
     * @param targetClass 目标类
     * @return Field 字段列表
     */
    public static List<Field> getFields(Class<?> targetClass) {
        Field[] fields = targetClass.getDeclaredFields();
        if (fields.length == 0) {
            return Collections.emptyList();
        }

        List<Field> fieldList = new ArrayList<>();
        for (Field field : fields) {
            // 跳过静态字段
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // 跳过私有字段
            if (Modifier.isPrivate(field.getModifiers())) {
                continue;
            }

            fieldList.add(field);
        }

        // 排序规则: 先按字段名排序，再按字段类型描述符排序
        // 例如: int a, long b, int c 会被排序为: a, c, b
        fieldList.sort(Comparator
                .comparing(Field::getName)
                .thenComparing(f -> Type.getDescriptor(f.getType()))
        );

        return fieldList;
    }

    /**
     * 获取指定类的方法集合
     * <p>
     * 获取当前类及父类所有，非static，非private方法。
     *
     * @param targetClass 目标类
     * @return Method 方法列表
     */
    public static List<Method> getMethods(Class<?> targetClass) {
        Method[] methods = ReflectUtil.getMethods(targetClass);
        if (methods.length == 0) {
            return Collections.emptyList();
        }

        List<Method> methodList = new ArrayList<>();
        for (Method method : methods) {
            // 跳过 java.lang.Object
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }

            // 跳过静态方法
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            // 跳过私有方法
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }

            methodList.add(method);
        }

        methodList.sort(Comparator
                .comparing(Method::getName)
                .thenComparing(m -> Type.getMethodDescriptor(m))
        );

        return methodList;
    }

    /**
     * 获取指定类的构造方法集合
     * <p>
     * 获取当前类所有，非private构造方法。
     *
     * @param targetClass 目标类
     * @return Constructor 构造方法列表
     */
    public static List<Constructor<?>> getConstructors(Class<?> targetClass) {
        Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
        if (constructors.length == 0) {
            return Collections.emptyList();
        }

        List<Constructor<?>> constructorList = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            // 跳过私有构造方法
            if (Modifier.isPrivate(constructor.getModifiers())) {
                continue;
            }

            constructorList.add(constructor);
        }

        // 排序规则: 先按参数数量排序，再按参数类型描述符排序
        constructorList.sort(Comparator
                .comparing((Constructor<?> c) -> c.getParameterTypes().length)
                .thenComparing(Type::getConstructorDescriptor)
        );

        return constructorList;
    }
}
