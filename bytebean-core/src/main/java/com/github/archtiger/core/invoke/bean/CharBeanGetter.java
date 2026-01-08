package com.github.archtiger.core.invoke.bean;

/**
 * CharBeanGetter
 * <p>
 * 接口定义了一个用于获取目标对象字符基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface CharBeanGetter {
    char get(Object target);
}
