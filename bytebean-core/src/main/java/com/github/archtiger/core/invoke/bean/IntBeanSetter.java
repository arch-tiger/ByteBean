package com.github.archtiger.core.invoke.bean;

/**
 * IntBeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象整数基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface IntBeanSetter {
    void set(Object target, int value);
}