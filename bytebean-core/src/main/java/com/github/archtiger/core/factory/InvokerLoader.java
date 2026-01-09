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
     * 加载调用器，若已存在则复用，若不存在则创建
     *
     * @return 调用器实例
     */
    T load();

    /**
     * 加载调用器或失败抛异常
     *
     * @return 调用器实例
     */
    T loadOrFail();

    /**
     * 是否可以实例化调用器
     *
     * @return 是否可以实例化调用器
     */
    boolean canInstantiate();
}
