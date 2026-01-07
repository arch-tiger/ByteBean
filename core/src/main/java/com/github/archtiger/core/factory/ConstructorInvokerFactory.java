package com.github.archtiger.core.factory;

import com.github.archtiger.core.invoke.ConstructorInvoker;
import com.github.archtiger.core.bytecode.ConstructorAppender;
import com.github.archtiger.core.support.NameUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;

/**
 * 创建器工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class ConstructorInvokerFactory {

    private ConstructorInvokerFactory() {
    }

    public static ConstructorInvoker create(Class<?> targetClass, Constructor<?> constructor) {
        Class<? extends ConstructorInvoker> creatorClass = new ByteBuddy()
                .subclass(ConstructorInvoker.class)
                .name(NameUtil.calcForConstructorInvoker(targetClass, constructor))
                .method(ElementMatchers.named("newInstance"))
                .intercept(new Implementation.Simple(new ConstructorAppender(targetClass, constructor)))
                .make()
                .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        try {
            return creatorClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
