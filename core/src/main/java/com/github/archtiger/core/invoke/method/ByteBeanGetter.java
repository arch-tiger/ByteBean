package com.github.archtiger.core.invoke.method;

/**
 * Bean Getter方法调用器接口（byte返回值）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface ByteBeanGetter {
    /**
     * 调用目标getter方法
     *
     * @param target 目标对象
     * @return byte返回值
     */
    byte invoke(Object target);
}
