package com.github.archtiger.core.factory;

import com.github.archtiger.core.bytecode.CreatorAppender;
import com.github.archtiger.core.engine.Creator;
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
public final class CreatorFactory {

    private CreatorFactory() {
    }

    public static Creator create(Class<?> targetClass, Constructor<?> constructor) {
        Class<? extends Creator> creatorClass =
                new ByteBuddy()
                        .subclass(Creator.class)
                        .name(targetClass.getName() + "$$Creator$" + constructor.getParameterCount())
                        .method(ElementMatchers.named("newInstance"))
                        .intercept(new Implementation.Simple(new CreatorAppender(targetClass, constructor)))
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
