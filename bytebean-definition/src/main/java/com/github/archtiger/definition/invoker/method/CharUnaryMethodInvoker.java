package com.github.archtiger.definition.invoker.method;

/**
 * CharUnaryMethodInvoker
 * char 类型一元方法调用器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
@FunctionalInterface
public interface CharUnaryMethodInvoker {
    /**
     * 调用目标方法
     *
     * @param target 目标对象
     * @param arg    方法参数
     * @return 方法返回值
     */
    Object invoke(Object target, char arg);
}