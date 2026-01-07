package com.github.archtiger.core.invoke.method;

/**
 * Bean Setter方法调用器接口（byte参数）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface ByteBeanSetter {
    /**
     * 调用目标setter方法
     *
     * @param target 目标对象
     * @param value  byte参数
     */
    void invoke(Object target, byte value);
}
