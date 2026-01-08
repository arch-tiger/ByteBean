package com.github.archtiger.core.invoke.bean;

/**
 * BeanGetter
 * <p>
 * 接口定义了一个用于获取目标对象属性值的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface BeanGetter {
    /**
     * 获取目标对象的属性值
     *
     * @param target 目标对象
     * @return 属性值
     */
    Object get(Object target);
}