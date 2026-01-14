package com.github.archtiger.bytebean.core.invoker;

import cn.hutool.core.util.ClassUtil;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerGenerator;
import com.github.archtiger.bytebean.core.model.MethodInvokerResult;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;
import com.github.archtiger.bytebean.api.method.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 方法访问助手
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:23
 */
public class MethodInvokerHelper {
    private final MethodInvoker methodInvoker;
    private final String[] methodNames;
    private final Class<?>[][] methodParamTypes;

    private MethodInvokerHelper(MethodInvokerResult methodInvokerResult) {
        this.methodNames = methodInvokerResult.methods().stream().map(Method::getName).toArray(String[]::new);
        this.methodParamTypes = methodInvokerResult.methods().stream().map(Method::getParameterTypes).toArray(Class[][]::new);
        try {
            this.methodInvoker = methodInvokerResult.methodInvokerClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建方法访问助手
     */
    public static MethodInvokerHelper of(Class<?> targetClass) {
        MethodInvokerResult methodInvokerResult = MethodInvokerGenerator.generate(targetClass);
        if (!methodInvokerResult.ok()) {
            return null;
        }

        return new MethodInvokerHelper(methodInvokerResult);
    }

    /**
     * 获取方法访问器
     */
    public MethodInvoker getMethodInvoker() {
        return methodInvoker;
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

        return ExceptionCode.INVALID_INDEX;
    }

    /**
     * 获取方法索引，若不存在则抛出异常
     */
    public int getMethodIndexOrThrow(String methodName, Class<?>... paramTypes) {
        int methodIndex = getMethodIndex(methodName, paramTypes);
        if (methodIndex == ExceptionCode.INVALID_INDEX) {
            throw ExceptionUtil.methodNotFound(methodName, paramTypes);
        }

        return methodIndex;
    }

    /**
     * 调用方法，返回Object类型结果
     */
    public Object invoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));

        return methodInvoker.invoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回short类型结果
     */
    public short shortInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.shortInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回byte类型结果
     */
    public byte byteInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.byteInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回char类型结果
     */
    public char charInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.charInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回int类型结果
     */
    public int intInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.intInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回long类型结果
     */
    public long longInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.longInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回double类型结果
     */
    public double doubleInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.doubleInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回float类型结果
     */
    public float floatInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.floatInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，返回boolean类型结果
     */
    public boolean booleanInvoke(Object target, String methodName, Object... args) {
        int methodIndex = getMethodIndexOrThrow(methodName, ClassUtil.getClasses(args));
        return methodInvoker.booleanInvoke(methodIndex, target, args);
    }

    /**
     * 调用方法，参数short类型
     */
    public Object invokeShort1(Object target, String methodName, short arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, short.class);
        return methodInvoker.invokeShort1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数byte类型
     */
    public Object invokeByte1(Object target, String methodName, byte arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, byte.class);
        return methodInvoker.invokeByte1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数char类型
     */
    public Object invokeChar1(Object target, String methodName, char arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, char.class);
        return methodInvoker.invokeChar1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数int类型
     */
    public Object invokeInt1(Object target, String methodName, int arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, int.class);
        return methodInvoker.invokeInt1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数long类型
     */
    public Object invokeLong1(Object target, String methodName, long arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, long.class);
        return methodInvoker.invokeLong1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数double类型
     */
    public Object invokeDouble1(Object target, String methodName, double arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, double.class);
        return methodInvoker.invokeDouble1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数float类型
     */
    public Object invokeFloat1(Object target, String methodName, float arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, float.class);
        return methodInvoker.invokeFloat1(methodIndex, target, arg1);
    }

    /**
     * 调用方法，参数boolean类型
     */
    public Object invokeBoolean1(Object target, String methodName, boolean arg1) {
        int methodIndex = getMethodIndexOrThrow(methodName, boolean.class);
        return methodInvoker.invokeBoolean1(methodIndex, target, arg1);
    }

}
