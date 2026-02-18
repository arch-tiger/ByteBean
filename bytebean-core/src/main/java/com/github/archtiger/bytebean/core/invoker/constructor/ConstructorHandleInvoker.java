package com.github.archtiger.bytebean.core.invoker.constructor;

import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 构造器调用器
 *
 * @author ZIJIDELU
 * @datetime 2026/2/6 19:44
 */
public final class ConstructorHandleInvoker extends ConstructorInvoker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final MethodHandle[] constructorHandles;
    private final MethodHandle defaultConstructorHandle;

    private ConstructorHandleInvoker(MethodHandle[] constructorHandles,
                                     MethodHandle defaultConstructorHandle) {
        this.constructorHandles = constructorHandles;
        this.defaultConstructorHandle = defaultConstructorHandle;
    }

    public static ConstructorHandleInvoker of(Class<?> targetClass) {
        List<Constructor<?>> constructors = ByteBeanReflectUtil.getConstructors(targetClass);
        MethodHandle[] constructorHandles = new MethodHandle[constructors.size()];
        try {
            for (int i = 0; i < constructors.size(); i++) {
                Constructor<?> constructor = constructors.get(i);
                if (constructor.getParameterCount() == 0) {
                    constructorHandles[i] = LOOKUP.unreflectConstructor(constructor)
                            .asType(MethodType.methodType(Object.class));
                } else {
                    constructorHandles[i] = LOOKUP.unreflectConstructor(constructor)
                            .asSpreader(Object[].class, constructor.getParameterCount());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return new ConstructorHandleInvoker(constructorHandles, constructorHandles[0]);
    }

    @Override
    public Object newInstance(int index, Object... args) {
        try {
            return constructorHandles[index].invoke(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object newInstance() {
        try {
            return defaultConstructorHandle.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
