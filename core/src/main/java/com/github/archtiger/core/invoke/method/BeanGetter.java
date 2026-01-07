package com.github.archtiger.core.invoke.method;

/**
 * Bean Getter方法调用器接口
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface BeanGetter {
    /**
     * 调用目标getter方法
     *
     * @param target 目标对象
     * @return 方法返回值
     */
    Object invoke(Object target);
}
