package com.github.archtiger.core.engine;

/**
 * 方法调用器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface Invoker {
    /**
     * 调用目标方法
     *
     * @param target 目标对象
     * @param args   方法参数
     * @return 方法返回值，如果 void 返回 null
     */
    Object invoke(Object target, Object... args);
}
