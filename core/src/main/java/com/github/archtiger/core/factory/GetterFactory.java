package com.github.archtiger.core.factory;

import com.github.archtiger.core.bytecode.GetterAppender;
import com.github.archtiger.core.engine.Getter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;

import java.lang.reflect.Field;

/**
 * Getter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class GetterFactory {
    private GetterFactory() {
    }

    /**
     * 创建 Getter
     *
     * @param targetClass 目标类
     * @param fieldName   字段名
     * @return Getter 实例
     * @throws Exception 如果创建失败
     */
    public static Getter createGetter(Class<?> targetClass, String fieldName) throws Exception {

        try {
            Field field = targetClass.getDeclaredField(fieldName);

            Class<? extends Getter> getterClass = new ByteBuddy()
                    .subclass(Getter.class)
                    .name(targetClass.getName() + "$$" + fieldName + "Getter")
                    .method(m -> m.getName().equals("get"))
                    .intercept(new Implementation.Simple(new GetterAppender(targetClass, field)))
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return getterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
