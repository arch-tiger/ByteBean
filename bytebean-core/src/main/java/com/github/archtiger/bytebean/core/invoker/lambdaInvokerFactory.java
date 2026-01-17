package com.github.archtiger.bytebean.core.invoker;

import com.github.archtiger.bytebean.api.method.MethodExecutor;
import com.github.archtiger.bytebean.core.invoker.method.MethodHandleInvoker;

import java.lang.invoke.*;
import java.lang.reflect.Method;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/17 20:43
 */
public final class lambdaInvokerFactory {
    public static class MethodHandleWrapper {
        final MethodHandle handle;
        final int paramCount;

       public MethodHandleWrapper(MethodHandle handle, int paramCount) {
            this.handle = handle;
            this.paramCount = paramCount;
        }
    }

    /**
     * 为指定的实例方法生成 MethodExecutor
     * 使用 LambdaMetafactory 动态创建 MethodExecutor 实例
     *
     * @param lookup MethodHandles.Lookup
     * @param method 目标实例方法
     * @return MethodExecutor 实例
     */
    public static MethodExecutor create(MethodHandles.Lookup lookup, Method method) {
        try {
            MethodHandle rawHandle = lookup.unreflect(method);
            int paramCount = method.getParameterCount();

            // 将 rawHandle 转换为统一类型 (Object, Object[])Object
            MethodHandle adaptedHandle;
            if (paramCount == 0) {
                // 无参数: (Target)Ret -> (Target, Object[])Ret
                adaptedHandle = MethodHandles.dropArguments(rawHandle, 1, Object[].class);
                adaptedHandle = adaptedHandle.asType(MethodType.methodType(Object.class, Object.class, Object[].class));
            } else {
                // 多参数: (Target, Args...)Ret -> (Target, Object[])Ret
                Class<?>[] paramTypes = new Class<?>[paramCount];
                for (int i = 0; i < paramCount; i++) {
                    paramTypes[i] = Object.class;
                }
                MethodHandle genericHandle = rawHandle.asType(MethodType.methodType(Object.class, Object.class, paramTypes));
                adaptedHandle = genericHandle.asSpreader(Object[].class, paramCount);
                adaptedHandle = adaptedHandle.asType(MethodType.methodType(Object.class, Object.class, Object[].class));
            }

            MethodHandleWrapper wrapper = new MethodHandleWrapper(adaptedHandle, paramCount);

            // 统一使用 invokeWithArgs 方法
            MethodHandle invokeMethod = lookup.findStatic(
                    MethodHandleInvoker.class,
                    "invokeWithArgs",
                    MethodType.methodType(Object.class, MethodHandleWrapper.class, Object.class, Object[].class)
            );

            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "invoke",
                    MethodType.methodType(MethodExecutor.class, MethodHandleWrapper.class),
                    MethodType.methodType(Object.class, Object.class, Object[].class),
                    invokeMethod,
                    MethodType.methodType(Object.class, Object.class, Object[].class)
            );
            return (MethodExecutor) callSite.getTarget().invoke(wrapper);

        } catch (Throwable e) {
            throw new RuntimeException("Failed to create MethodExecutor for " + method, e);
        }
    }

    /**
     * 多参数方法的适配方法
     */
    public static Object invokeWithArgs(MethodHandleWrapper wrapper, Object target, Object[] args) {
        try {
            return wrapper.handle.invokeExact(target, args);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
}