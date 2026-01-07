package com.github.archtiger.core.invoke.method;

/**
 * Bean Getter方法调用器接口（boolean返回值）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
@FunctionalInterface
public interface BooleanBeanGetter {
    /**
     * 调用目标getter方法
     *
     * @param target 目标对象
     * @return boolean返回值
     */
    boolean invoke(Object target);
}
