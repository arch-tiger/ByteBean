package com.github.archtiger.core.factory;

/**
 * 调用器加载器接口
 * <p>
 * 用于加载调用器，支持创建调用器和创建调用器或抛出异常两种方式
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9 13:04
 */
public interface InvokerLoader<T> {
    /**
     * 创建调用器
     *
     * @return 调用器
     */
    T createInvoker();

    /**
     * 创建调用器或抛出异常
     *
     * @return 调用器
     */
    T createInvokerOrThrow();
}
