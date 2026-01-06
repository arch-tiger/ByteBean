package com.github.archtiger.core.factory;

import com.github.archtiger.core.bytecode.InvokerAppender;
import com.github.archtiger.core.engine.MethodInvoker;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;

import java.lang.reflect.Method;

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
    public static MethodInvoker create(Class<?> targetClass, Method method) throws Exception {
        Class<? extends MethodInvoker> invokerClass = new ByteBuddy()
                .subclass(MethodInvoker.class)
                .name(targetClass.getName() + "$$" + method.getName() + "Invoker")
                .method(m -> m.getName().equals("invoke"))
                .intercept(new Implementation.Simple(new InvokerAppender(targetClass, method)))
                .make()
                .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        return invokerClass.getDeclaredConstructor().newInstance();
    }
}
