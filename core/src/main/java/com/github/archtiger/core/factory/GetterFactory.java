package com.github.archtiger.core.factory;

import com.github.archtiger.core.accessor.FieldGetter;
import com.github.archtiger.core.bytecode.GetterAppender;
import com.github.archtiger.core.support.NameUtil;
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
     * @param field       字段
     * @return Getter 实例
     * @throws Exception 如果创建失败
     */
    public static FieldGetter createGetter(Class<?> targetClass, Field field) throws Exception {

        try {
            Class<? extends FieldGetter> getterClass = new ByteBuddy()
                    .subclass(FieldGetter.class)
                    .name(NameUtil.calcForFieldGetter(targetClass, field))
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
