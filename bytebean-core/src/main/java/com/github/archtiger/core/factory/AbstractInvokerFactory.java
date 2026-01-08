package com.github.archtiger.core.factory;

import com.github.archtiger.core.exception.UnsupportedCreateInvokerException;
import com.github.archtiger.core.model.InvokerNameInfo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.InvocationTargetException;

/**
 * 抽象调用器工厂类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7 20:40
 */
public abstract class AbstractInvokerFactory<T> {
    private static final TypeCache<String> TYPE_CACHE =
            new TypeCache.WithInlineExpunction<>(TypeCache.Sort.WEAK);

    private final Class<?> targetClass;

    protected AbstractInvokerFactory(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    protected Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * 定义调用器类
     *
     * @return 调用器类
     */
    abstract protected Class<T> defineInvokerClass();

    /**
     * 定义调用器名称
     *
     * @return 调用器名称
     */
    abstract protected InvokerNameInfo defineInvokerName();

    /**
     * 定义字节码追加器
     *
     * @return 字节码追加器
     */
    abstract protected ByteCodeAppender defineByteCodeAppender();

    /**
     * 定义调用器方法名
     *
     * @return 调用器方法名
     */
    abstract protected String defineInvokerMethodName();

    /**
     * 是否可以实例化调用器
     *
     * @return 是否可以实例化调用器
     */
    abstract protected boolean canInstantiate();

    /**
     * 加载调用器
     *
     * @return 调用器
     */
    @SuppressWarnings("unchecked")
    public T createInvoker() {
        if (!canInstantiate()) {
            return null;
        }
        final ClassLoader classLoader = getTargetClass().getClassLoader();
        final String className = defineInvokerName().calcInvokerClassName();

        final Class<?> invokerClass = TYPE_CACHE.findOrInsert(classLoader, className, () -> new ByteBuddy()
                .subclass(defineInvokerClass())
                .name(className)
                .method(m -> m.getName().equals(defineInvokerMethodName()))
                .intercept(new Implementation.Simple(defineByteCodeAppender()))
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
        );

        try {
            return (T) invokerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载调用器或抛出异常
     *
     * @return 调用器
     */
    public T createInvokerOrThrow() {
        if (!canInstantiate()) {
            throw new UnsupportedCreateInvokerException(targetClass, defineInvokerClass());
        }

        return createInvoker();
    }
}
