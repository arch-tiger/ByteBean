package com.github.archtiger.core.invoke;

/**
 * 构造函数调用器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
@FunctionalInterface
public interface ConstructorInvoker {
    Object newInstance(Object... args);
}