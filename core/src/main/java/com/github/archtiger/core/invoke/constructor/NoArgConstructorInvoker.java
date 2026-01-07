package com.github.archtiger.core.invoke.constructor;

/**
 * 无参构造函数调用器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface NoArgConstructorInvoker {
    /**
     * 调用无参构造函数创建实例
     *
     * @return 新创建的实例
     */
    Object newInstance();
}
