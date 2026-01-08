package com.github.archtiger.core.invoke.bean;

/**
 * LongBeanGetter
 * <p>
 * 接口定义了一个用于获取目标对象长整型基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface LongBeanGetter {
    long get(Object target);
}
