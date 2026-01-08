package com.github.archtiger.core.invoke.bean;

/**
 * LongBeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象长整型基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface LongBeanSetter {
    void set(Object target, long value);
}
