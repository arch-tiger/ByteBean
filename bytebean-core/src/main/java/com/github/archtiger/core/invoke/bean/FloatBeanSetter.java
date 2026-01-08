package com.github.archtiger.core.invoke.bean;

/**
 * FloatBeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象单精度浮点型基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface FloatBeanSetter {
    void set(Object target, float value);
}
