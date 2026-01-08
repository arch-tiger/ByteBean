package com.github.archtiger.definition.invoker.method;

/**
 * INT 方法调用器接口
 * <p>
 * 用于调用返回 int 类型的方法，避免返回值装箱开销
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
@FunctionalInterface
public interface IntMethodInvoker {
    /**
     * 调用目标方法
     *
     * @param target 目标对象
     * @param args   方法参数
     * @return 方法返回值
     */
    int invoke(Object target, Object... args);
}
