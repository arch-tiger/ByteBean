package com.github.archtiger.bytebean.extensions;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ZIJIDELU
 * @datetime 2026/2/18 13:58
 */
public class ByteBeanCopierUtil {
    /**
     * 获取参数数量最多的 public 构造器。
     */
    public static Constructor<?> calcMaxParameterConstructor(Class<?> clazz) {
        // 统一使用参数最多的公开构造器作为 record 重建入口。
        return Arrays.stream(clazz.getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new IllegalStateException("No constructor found in class: " + clazz.getName()));
    }

    /**
     * 计算bean可用方法集合。
     */
    public static Map<String, Method> calcBeanMethodMap(Class<?> clazz) {
        return Arrays.stream(ReflectUtil.getMethods(clazz))
                .filter(e -> Modifier.isPublic(e.getModifiers()) && !Modifier.isStatic(e.getModifiers()))
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    /**
     * 计算可用 setter 方法集合。
     */
    public static Map<String, Method> calcBeanSetterMethodMap(Method[] methods) {
        return Arrays.stream(methods)
                .filter(e -> Modifier.isPublic(e.getModifiers()) && !Modifier.isStatic(e.getModifiers()))
                .filter(e -> e.getDeclaringClass() != Object.class)
                .filter(ByteBeanCopierUtil::isSetter)
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    /**
     * 计算可用 getter 方法集合。
     */
    public static Map<String, Method> calcBeanGetterMethodMap(Method[] methods) {
        return Arrays.stream(methods)
                .filter(e -> Modifier.isPublic(e.getModifiers()) && !Modifier.isStatic(e.getModifiers()))
                .filter(e -> e.getDeclaringClass() != Object.class)
                .filter(e -> ByteBeanCopierUtil.isGetterWithGet(e) || ByteBeanCopierUtil.isGetterWithIs(e))
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    /**
     * 是否是标准 getXxx() getter。
     */
    public static boolean isGetterWithGet(Method method) {
        // 匹配 getXxx() 且无参的方法。
        return method.getName().startsWith("get")
                && method.getName().length() > 3
                && method.getParameterTypes().length == 0;
    }

    /**
     * 是否是标准 isXxx() getter。
     */
    public static boolean isGetterWithIs(Method method) {
        // 匹配 boolean/Boolean 返回的 isXxx() 且无参方法。
        return method.getName().startsWith("is")
                && method.getName().length() > 2
                && (method.getReturnType() == boolean.class)
                && method.getParameterTypes().length == 0;
    }

    /**
     * 是否是标准 setXxx(arg) setter。
     */
    public static boolean isSetter(Method method) {
        // 匹配单参数 setXxx(arg) 方法。
        return method.getName().startsWith("set")
                && method.getName().length() > 3
                && method.getParameterTypes().length == 1;
    }

    /**
     * 获取 setter 方法对应的字段名。
     */
    public static String calcFieldNameWithSetter(String setterName) {
        // 匹配 setXxx(arg) 方法。
        String substring = setterName.substring(3);
        return substring.substring(0, 1).toLowerCase() + substring.substring(1);
    }

    /**
     * 获取 getter 方法对应的字段名。
     */
    public static String calcFieldNameWithGetter(String getterName) {
        // 匹配 getXxx() 或 isXxx() 方法。
        final String substring;
        if (getterName.startsWith("is")) {
            // 匹配 isXxx() 方法。
            substring = getterName.substring(2);
        } else {
            // 匹配 getXxx() 方法。
            substring = getterName.substring(3);
        }

        return substring.substring(0, 1).toLowerCase() + substring.substring(1);
    }

    /**
     * 计算 getter 方法名。
     *
     * @param fieldName  字段名
     * @param returnType 返回类型
     * @return getter 方法名
     */
    public static String calcGetterName(String fieldName, Class<?> returnType) {
        if (returnType == boolean.class) {
            return "is" + StrUtil.upperFirst(fieldName);
        } else {
            return StrUtil.genGetter(fieldName);
        }
    }
}