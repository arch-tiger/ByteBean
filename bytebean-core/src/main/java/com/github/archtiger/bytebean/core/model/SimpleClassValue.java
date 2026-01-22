package com.github.archtiger.bytebean.core.model;

import java.util.function.Function;

/**
 * 默认的类值实现类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/22 17:39
 */
public final class SimpleClassValue<T> extends ClassValue<T> {
    private final Function<Class<?>, T> function;

    public SimpleClassValue(Function<Class<?>, T> function) {
        this.function = function;
    }

    @Override
    protected T computeValue(Class<?> type) {
        return function.apply(type);
    }
}
