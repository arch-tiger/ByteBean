package com.github.archtiger.core.engine;

/**
 * 构造函数调用器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
@FunctionalInterface
public interface Creator {
    Object newInstance(Object... args);
}