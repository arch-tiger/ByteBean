package com.github.archtiger.core.access.method;

import cn.hutool.core.util.ClassUtil;
import com.github.archtiger.core.model.MethodAccessInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 方法访问助手
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:23
 */
public class MethodAccessHelper {
    private final MethodAccess methodAccess;
    private final String[] methodNames;
    private final Class<?>[][] methodParamTypes;

    private MethodAccessHelper(MethodAccessInfo methodAccessInfo) {
        this.methodNames = methodAccessInfo.methods().stream().map(Method::getName).toArray(String[]::new);
        this.methodParamTypes = methodAccessInfo.methods().stream().map(Method::getParameterTypes).toArray(Class[][]::new);
        try {
            this.methodAccess = methodAccessInfo.methodAccessClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建方法访问助手
     */
    public static MethodAccessHelper of(Class<?> targetClass) {
        MethodAccessInfo methodAccessInfo = MethodAccessGenerator.generate(targetClass);
        if (!methodAccessInfo.ok()) {
            return null;
        }

        return new MethodAccessHelper(methodAccessInfo);
    }

    /**
     * 获取方法访问器
     */
    public MethodAccess getMethodAccess() {
        return methodAccess;
    }

    /**
     * 获取方法索引
     */
    public int getMethodIndex(String methodName, Class<?>... paramTypes) {
        for (int i = 0, n = methodNames.length; i < n; i++) {
            // 优先匹配速度最快,最可能失败的情况
            if (methodNames[i].equals(methodName) && Arrays.equals(paramTypes, this.methodParamTypes[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 调用方法，返回Object类型结果
     */
    public Object invoke(Object target, String methodName, Object... args) {
        return methodAccess.invoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回short类型结果
     */
    public short shortInvoke(Object target, String methodName, Object... args) {
        return methodAccess.shortInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回byte类型结果
     */
    public byte byteInvoke(Object target, String methodName, Object... args) {
        return methodAccess.byteInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回char类型结果
     */
    public char charInvoke(Object target, String methodName, Object... args) {
        return methodAccess.charInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回int类型结果
     */
    public int intInvoke(Object target, String methodName, Object... args) {
        return methodAccess.intInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回long类型结果
     */
    public long longInvoke(Object target, String methodName, Object... args) {
        return methodAccess.longInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回double类型结果
     */
    public double doubleInvoke(Object target, String methodName, Object... args) {
        return methodAccess.doubleInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回float类型结果
     */
    public float floatInvoke(Object target, String methodName, Object... args) {
        return methodAccess.floatInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，返回boolean类型结果
     */
    public boolean booleanInvoke(Object target, String methodName, Object... args) {
        return methodAccess.booleanInvoke(getMethodIndex(methodName, ClassUtil.getClasses(args)), target, args);
    }

    /**
     * 调用方法，参数short类型
     */
    public Object invokeShort1(Object target, String methodName, short arg1) {
        return methodAccess.invokeShort1(getMethodIndex(methodName, short.class), target, arg1);
    }

    /**
     * 调用方法，参数byte类型
     */
    public Object invokeByte1(Object target, String methodName, byte arg1) {
        return methodAccess.invokeByte1(getMethodIndex(methodName, byte.class), target, arg1);
    }

    /**
     * 调用方法，参数char类型
     */
    public Object invokeChar1(Object target, String methodName, char arg1) {
        return methodAccess.invokeChar1(getMethodIndex(methodName, char.class), target, arg1);
    }

    /**
     * 调用方法，参数int类型
     */
    public Object invokeInt1(Object target, String methodName, int arg1) {
        return methodAccess.invokeInt1(getMethodIndex(methodName, int.class), target, arg1);
    }

    /**
     * 调用方法，参数long类型
     */
    public Object invokeLong1(Object target, String methodName, long arg1) {
        return methodAccess.invokeLong1(getMethodIndex(methodName, long.class), target, arg1);
    }

    /**
     * 调用方法，参数double类型
     */
    public Object invokeDouble1(Object target, String methodName, double arg1) {
        return methodAccess.invokeDouble1(getMethodIndex(methodName, double.class), target, arg1);
    }

    /**
     * 调用方法，参数float类型
     */
    public Object invokeFloat1(Object target, String methodName, float arg1) {
        return methodAccess.invokeFloat1(getMethodIndex(methodName, float.class), target, arg1);
    }

    /**
     * 调用方法，参数boolean类型
     */
    public Object invokeBoolean1(Object target, String methodName, boolean arg1) {
        return methodAccess.invokeBoolean1(getMethodIndex(methodName, boolean.class), target, arg1);
    }

}
