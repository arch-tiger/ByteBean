package com.github.archtiger.core.invoke.bean;

/**
 * ShortBeanGetter
 * <p>
 * 接口定义了一个用于获取目标对象短整型基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface ShortBeanGetter {
    short get(Object target);
}
