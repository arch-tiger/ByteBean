package com.github.archtiger.core.invoke.method;

/**
 * Bean Setter方法调用器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface BeanSetter {
    /**
     * 调用目标setter方法
     *
     * @param target 目标对象
     * @param value  方法参数
     */
    void invoke(Object target, Object value);
}
