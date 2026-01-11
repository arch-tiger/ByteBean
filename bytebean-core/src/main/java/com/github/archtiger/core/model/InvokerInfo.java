package com.github.archtiger.core.model;

/**
 * 调用器信息
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9 20:24
 */
public record InvokerInfo<T>(
        Class<?> targetClass,
        Class<T> invokerClass,
        Class<?> invokerReturnType
) {
}
