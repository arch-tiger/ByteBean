package com.github.archtiger.definition.invoker.copy;

/**
 * 构造器到方法复制器
 * <p>
 * 用于从源对象构造新目标对象（构造器→方法复制）
 * 适用于 record 等不可变对象的复制场景
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
@FunctionalInterface
public interface ConstructorToMethodCopier extends ObjectCopier {
    /**
     * 通过构造器方式将 source 的数据复制到 target
     * <p>
     * 从 source 对象提取数据，通过 target 的构造器创建新实例
     * 通常用于不可变对象的复制
     *
     * @param source 来源对象
     * @param target 目标对象（通常用于提供构造器信息）
     */
    @Override
    void copy(Object source, Object target);
}