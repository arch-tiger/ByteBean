package com.github.archtiger.core.factory;

import com.github.archtiger.core.bytecode.SetterAppender;
import com.github.archtiger.core.engine.Setter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;

import java.lang.reflect.Field;

/**
 * Setter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class SetterFactory {
    private SetterFactory() {
    }

    /**
     * 创建 Setter
     *
     * @param targetClass 目标类
     * @param field       字段
     * @return Setter 实例
     */
    public static Setter createSetter(Class<?> targetClass, Field field) {

        try {
            Class<? extends Setter> setterClass = new ByteBuddy()
                    .subclass(Setter.class)
                    .name(targetClass.getName() + "$$" + field.getName() + "Setter")
                    .method(m -> m.getName().equals("set"))
                    .intercept(new Implementation.Simple(new SetterAppender(targetClass, field)))
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return setterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
