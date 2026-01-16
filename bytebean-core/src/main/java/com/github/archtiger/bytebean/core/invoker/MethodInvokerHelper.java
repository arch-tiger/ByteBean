package com.github.archtiger.bytebean.core.invoker;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerGenerator;
import com.github.archtiger.bytebean.core.model.MethodInvokerResult;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 方法访问助手
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:23
 */
public final class MethodInvokerHelper extends MethodInvoker {
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

    @Override
    public Object invoke(int index, Object instance, Object... arguments) {
        return methodInvoker.invoke(index, instance, arguments);
    }

    @Override
    public Object invoke(int index, Object instance) {
        return methodInvoker.invoke(index, instance);
    }

    @Override
    public Object invoke1(int index, Object instance, Object arg) {
        return methodInvoker.invoke1(index, instance, arg);
    }

    @Override
    public int intInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.intInvoke(index, instance, arguments);
    }

    @Override
    public long longInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.longInvoke(index, instance, arguments);
    }

    @Override
    public float floatInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.floatInvoke(index, instance, arguments);
    }

    @Override
    public double doubleInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.doubleInvoke(index, instance, arguments);
    }

    @Override
    public boolean booleanInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.booleanInvoke(index, instance, arguments);
    }

    @Override
    public byte byteInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.byteInvoke(index, instance, arguments);
    }

    @Override
    public short shortInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.shortInvoke(index, instance, arguments);
    }

    @Override
    public char charInvoke(int index, Object instance, Object... arguments) {
        return methodInvoker.charInvoke(index, instance, arguments);
    }

    @Override
    public Object invokeInt1(int index, Object instance, int arg) {
        return methodInvoker.invokeInt1(index, instance, arg);
    }

    @Override
    public Object invokeLong1(int index, Object instance, long arg) {
        return methodInvoker.invokeLong1(index, instance, arg);
    }

    @Override
    public Object invokeFloat1(int index, Object instance, float arg) {
        return methodInvoker.invokeFloat1(index, instance, arg);
    }

    @Override
    public Object invokeDouble1(int index, Object instance, double arg) {
        return methodInvoker.invokeDouble1(index, instance, arg);
    }

    @Override
    public Object invokeBoolean1(int index, Object instance, boolean arg) {
        return methodInvoker.invokeBoolean1(index, instance, arg);
    }

    @Override
    public Object invokeByte1(int index, Object instance, byte arg) {
        return methodInvoker.invokeByte1(index, instance, arg);
    }

    @Override
    public Object invokeShort1(int index, Object instance, short arg) {
        return methodInvoker.invokeShort1(index, instance, arg);
    }

    @Override
    public Object invokeChar1(int index, Object instance, char arg) {
        return methodInvoker.invokeChar1(index, instance, arg);
    }
}
