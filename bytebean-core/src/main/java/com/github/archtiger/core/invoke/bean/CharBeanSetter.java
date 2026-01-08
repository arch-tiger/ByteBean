package com.github.archtiger.core.invoke.bean;

/**
 * CharBeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象字符基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface CharBeanSetter {
    void set(Object target, char value);
}
