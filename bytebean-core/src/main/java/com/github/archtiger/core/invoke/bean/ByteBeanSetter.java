package com.github.archtiger.core.invoke.bean;

/**
 * ByteBeanSetter
 * <p>
 * 接口定义了一个用于设置目标对象字节基本类型的函数式接口。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
@FunctionalInterface
public interface ByteBeanSetter {
    void set(Object target, byte value);
}
