package com.github.archtiger.core.invoke.bean;

/**
 * ShortBeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象短整型基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface ShortBeanSetter {
    void set(Object target, short value);
}
