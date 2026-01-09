package com.github.archtiger.definition.invoker.method;

/**
 * Int 方法调用器接口
 * <p>
 * 用于调用只有1个 int 参数的方法，返回 Object 类型
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
@FunctionalInterface
public interface MethodInt1 {
    /**
     * 调用目标方法
     *
     * @param target 目标对象
     * @param arg1   第1个参数
     * @return 方法返回值
     */
    Object invoke(Object target, int arg1);
}
