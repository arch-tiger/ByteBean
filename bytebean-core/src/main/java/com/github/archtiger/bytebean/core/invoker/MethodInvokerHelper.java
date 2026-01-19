package com.github.archtiger.bytebean.core.invoker;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.invoker.method.MethodHandleInvoker;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerGenerator;
import com.github.archtiger.bytebean.core.model.MethodGroup;
import com.github.archtiger.bytebean.core.model.MethodIdentify;
import com.github.archtiger.bytebean.core.model.MethodInvokerResult;
import com.github.archtiger.bytebean.core.support.ByteBeanConstant;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.core.support.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
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

    private MethodInvokerHelper(MethodInvoker methodInvoker,
                                String[] methodNames,
                                Class<?>[][] methodParamTypes
    ) {
        this.methodNames = methodNames;
        this.methodParamTypes = methodParamTypes;
        this.methodInvoker = methodInvoker;
    }

    /**
     * 创建方法访问助手
     */
    public static MethodInvokerHelper of(Class<?> targetClass) {
        MethodGroup methodGroup = MethodGroup.of(targetClass);
        if (!methodGroup.ok()) {
            return null;
        }
        String[] methodNames = new String[methodGroup.methodAllList().size()];
        Class<?>[][] methodParamTypes = new Class<?>[methodGroup.methodAllList().size()][];
        for (int i = 0; i < methodGroup.methodAllList().size(); i++) {
            MethodIdentify methodIdentify = methodGroup.methodAllList().get(i);
            methodNames[i] = methodIdentify.method().getName();
            methodParamTypes[i] = methodIdentify.method().getParameterTypes();
        }

        if (methodGroup.methodAllList().size() > ByteBeanConstant.METHOD_SHARDING_THRESHOLD_VALUE) {
            MethodHandleInvoker methodHandleInvoker = MethodHandleInvoker.of(targetClass);
            return new MethodInvokerHelper(methodHandleInvoker, methodNames, methodParamTypes);
        }

        MethodInvokerResult generate = MethodInvokerGenerator.generate(targetClass);
        if (!generate.ok()) {
            return null;
        }

        try {
            MethodInvoker methodInvoker = generate.methodInvokerClass().getDeclaredConstructor().newInstance();
            return new MethodInvokerHelper(methodInvoker, methodNames, methodParamTypes);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
    public Object invoke2(int index, Object instance, Object arg1, Object arg2) {
        return methodInvoker.invoke2(index, instance, arg1, arg2);
    }

    @Override
    public Object invoke3(int index, Object instance, Object arg1, Object arg2, Object arg3) {
        return methodInvoker.invoke3(index, instance, arg1, arg2, arg3);
    }

    @Override
    public Object invoke4(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4) {
        return methodInvoker.invoke4(index, instance, arg1, arg2, arg3, arg4);
    }

    @Override
    public Object invoke5(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return methodInvoker.invoke5(index, instance, arg1, arg2, arg3, arg4, arg5);
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
