package com.github.archtiger.core.invoke.bean;

/**
 * BeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象属性值的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface BeanSetter {
    /**
     * 设置目标对象的属性值
     *
     * @param target 目标对象
     * @param value  属性值
     */
    void set(Object target, Object value);
}