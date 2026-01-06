package com.github.archtiger.core.factory;

import com.github.archtiger.core.bytecode.InvokerAppender;
import com.github.archtiger.core.engine.Invoker;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 方法调用器工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 23:24
 */
public final class InvokerFactory {
    private InvokerFactory() {
    }

    /**
     * 创建方法调用器
     *
     * @param targetClass 目标类
     * @param method      目标方法
     * @return 方法调用器
     * @throws Exception 异常
     */
    public static Invoker create(Class<?> targetClass, Method method) throws Exception {
        String paramsHash = Integer.toHexString(Arrays.hashCode(method.getParameterTypes()));
        String className = targetClass.getName() + "$$" + method.getName() + "_" + paramsHash + "Invoker";

        Class<? extends Invoker> invokerClass = new ByteBuddy()
                .subclass(Invoker.class)
                .name(className)
                .method(m -> m.getName().equals("invoke"))
                .intercept(new Implementation.Simple(new InvokerAppender(targetClass, method)))
                .make()
                .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        return invokerClass.getDeclaredConstructor().newInstance();
    }
}
