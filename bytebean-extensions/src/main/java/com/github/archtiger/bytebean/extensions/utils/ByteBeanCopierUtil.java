package com.github.archtiger.bytebean.extensions.utils;

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
 * Bean复制工具类，提供Bean/Record属性复制相关的反射工具方法。
 * <p>
 * 该类为{@link com.github.archtiger.bytebean.extensions.BeanCopier}提供底层支持，
 * 包括方法识别、字段名计算、匹配校验等功能。
 * <p>
 * <b>主要功能：</b>
 * <ul>
 *   <li>识别标准的getter/setter方法</li>
 *   <li>在getter和setter之间进行名称和类型匹配</li>
 *   <li>计算getter/setter对应的字段名</li>
 *   <li>提取Bean的公共方法集合</li>
 * </ul>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public class ByteBeanCopierUtil {

    /**
     * 私有构造函数，防止实例化。
     */
    private ByteBeanCopierUtil() {
    }

    /**
     * 获取参数数量最多的public构造器。
     * <p>
     * 主要用于Record类型的重建，因为Record是不可变的，需要通过构造器创建新实例。
     * </p>
     *
     * @param clazz 目标类
     * @return 参数数量最多的public构造器
     * @throws IllegalStateException 如果类中没有public构造器
     */
    public static Constructor<?> calcMaxParameterConstructor(Class<?> clazz) {
        // 统一使用参数最多的公开构造器作为 record 重建入口。
        return Arrays.stream(clazz.getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new IllegalStateException("No constructor found in class: " + clazz.getName()));
    }

    /**
     * 计算Bean的可用方法集合。
     *
     * @param clazz 目标类
     * @return 按方法名索引的方法Map
     */
    public static Map<String, Method> calcBeanMethodMap(Class<?> clazz) {
        return Arrays.stream(ReflectUtil.getMethods(clazz))
                .filter(e -> e.getDeclaringClass() != Object.class)
                .filter(e -> Modifier.isPublic(e.getModifiers()) && !Modifier.isStatic(e.getModifiers()))
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    /**
     * 计算Bean的可用setter方法集合。
     *
     * @param clazz 目标类
     * @return 按方法名索引的setter方法Map
     */
    public static Map<String, Method> calcBeanSetterMethodMap(Class<?> clazz) {
        return Arrays.stream(ReflectUtil.getMethods(clazz))
                .filter(e -> e.getDeclaringClass() != Object.class)
                .filter(e -> Modifier.isPublic(e.getModifiers()) && !Modifier.isStatic(e.getModifiers()))
                .filter(ByteBeanCopierUtil::isSetter)
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    /**
     * 计算Bean的可用getter方法集合。
     *
     * @param clazz 目标类
     * @return 按方法名索引的getter方法Map
     */
    public static Map<String, Method> calcBeanGetterMethodMap(Class<?> clazz) {
        return Arrays.stream(ReflectUtil.getMethods(clazz))
                .filter(e -> e.getDeclaringClass() != Object.class)
                .filter(e -> Modifier.isPublic(e.getModifiers()) && !Modifier.isStatic(e.getModifiers()))
                .filter(e -> ByteBeanCopierUtil.isGetterWithGet(e) || ByteBeanCopierUtil.isGetterWithIs(e))
                .collect(Collectors.toMap(Method::getName, Function.identity()));
    }

    /**
     * 判断方法是否是标准的getXxx() getter。
     *
     * @param method 要检查的方法
     * @return 如果方法名以"get"开头、长度大于3且无参数，返回true
     */
    public static boolean isGetterWithGet(Method method) {
        // 匹配 getXxx() 且无参的方法。
        return method.getName().startsWith("get")
                && method.getName().length() > 3
                && method.getParameterTypes().length == 0;
    }

    /**
     * 判断方法是否是标准的isXxx() getter（用于boolean类型）。
     *
     * @param method 要检查的方法
     * @return 如果方法名以"is"开头、长度大于2、返回boolean且无参数，返回true
     */
    public static boolean isGetterWithIs(Method method) {
        // 匹配 boolean/Boolean 返回的 isXxx() 且无参方法。
        return method.getName().startsWith("is")
                && method.getName().length() > 2
                && (method.getReturnType() == boolean.class)
                && method.getParameterTypes().length == 0;
    }

    /**
     * 判断方法是否是标准的setXxx(arg) setter。
     *
     * @param method 要检查的方法
     * @return 如果方法名以"set"开头、长度大于3且只有一个参数，返回true
     */
    public static boolean isSetter(Method method) {
        // 匹配单参数 setXxx(arg) 方法。
        return method.getName().startsWith("set")
                && method.getName().length() > 3
                && method.getParameterTypes().length == 1;
    }

    /**
     * 获取setter方法对应的字段名。
     * <p>
     * 例如：setName -> name
     * </p>
     *
     * @param setterName setter方法名
     * @return 对应的字段名
     */
    public static String calcFieldNameWithSetter(String setterName) {
        // 匹配 setXxx(arg) 方法。
        String substring = setterName.substring(3);
        return substring.substring(0, 1).toLowerCase() + substring.substring(1);
    }

    /**
     * 获取getter方法对应的字段名。
     * <p>
     * 例如：getName -> name, isActive -> active
     * </p>
     *
     * @param getterName getter方法名
     * @return 对应的字段名
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
     * 计算字段对应的getter方法名。
     * <p>
     * 根据返回类型自动选择：
     * <ul>
     *   <li>boolean类型 -> isXxx()</li>
     *   <li>其他类型 -> getXxx()</li>
     * </ul>
     *
     * @param fieldName  字段名
     * @param returnType 返回类型
     * @return getter方法名
     */
    public static String calcGetterName(String fieldName, Class<?> returnType) {
        if (returnType == boolean.class) {
            return "is" + StrUtil.upperFirst(fieldName);
        } else {
            return StrUtil.genGetter(fieldName);
        }
    }

    /**
     * 判断getter方法是否匹配setter方法。
     * <p>
     * 匹配条件：方法名对应的字段相同，且getter返回类型与setter参数类型相同。
     * </p>
     *
     * @param originGetter 来源对象的getter方法
     * @param targetSetter 目标对象的setter方法
     * @return 如果匹配返回true
     */
    public static boolean isMatchGetterAndSetter(Method originGetter, Method targetSetter) {
        return isMatchGetterAndSetterName(originGetter, targetSetter) && isMatchGetterAndSetterType(originGetter, targetSetter);
    }

    /**
     * 判断getter方法名是否匹配setter方法名。
     *
     * @param originGetter 来源对象的getter方法
     * @param targetSetter 目标对象的setter方法
     * @return 如果对应字段名相同返回true
     */
    public static boolean isMatchGetterAndSetterName(Method originGetter, Method targetSetter) {
        final String getterFieldName = calcFieldNameWithGetter(originGetter.getName());
        final String setterFieldName = calcFieldNameWithSetter(targetSetter.getName());
        return getterFieldName.equals(setterFieldName);
    }

    /**
     * 判断getter方法返回类型是否匹配setter方法参数类型。
     *
     * @param originGetter 来源对象的getter方法
     * @param targetSetter 目标对象的setter方法
     * @return 如果类型相同返回true
     */
    public static boolean isMatchGetterAndSetterType(Method originGetter, Method targetSetter) {
        return originGetter.getReturnType().equals(targetSetter.getParameterTypes()[0]);
    }

}