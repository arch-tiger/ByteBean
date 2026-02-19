package com.github.archtiger.bytebean.core.model;

import java.util.function.Function;

/**
 * ClassValue的简化实现，通过函数式接口初始化值。
 * <p>
 * 该类继承了Java 7引入的{@link ClassValue}，提供了基于函数式接口的初始化方式。
 * ClassValue是线程安全的，每个类只会计算一次值，后续访问直接返回缓存值。
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * SimpleClassValue<FieldInvoker> valueCache = new SimpleClassValue<>(cls ->
 *     FieldInvokerHelper.of(cls)
 * );
 * }</pre>
 * <p>
 * <b>特点：</b>
 * <ul>
 *   <li>线程安全，值计算和缓存由ClassValue保证</li>
 *   <li>使用函数式接口，简洁易用</li>
 *   <li>相比静态Map，自动处理类卸载和缓存清理</li>
 * </ul>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 * @param <T> 值的类型
 */
public final class SimpleClassValue<T> extends ClassValue<T> {

    /**
     * 用于计算值的函数。
     * 当首次访问某个Class时，会调用此函数计算对应的值。
     */
    private final Function<Class<?>, T> function;

    /**
     * 构造函数。
     *
     * @param function 用于计算值的函数，接收Class作为参数，返回值
     */
    public SimpleClassValue(Function<Class<?>, T> function) {
        this.function = function;
    }

    /**
     * 当首次访问某个Class时，调用此方法计算值。
     *
     * @param type 目标Class
     * @return 计算得到的值
     */
    @Override
    protected T computeValue(Class<?> type) {
        return function.apply(type);
    }
}
