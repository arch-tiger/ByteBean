package com.github.archtiger.definition.invoker.constructor;

/**
 * 构造函数调用器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
@FunctionalInterface
public interface CtorInvoker {
    /**
     * 使用指定参数调用构造函数创建新实例
     *
     * @param args 构造函数参数
     * @return 新创建的实例
     */
    Object newInstance(Object... args);
}