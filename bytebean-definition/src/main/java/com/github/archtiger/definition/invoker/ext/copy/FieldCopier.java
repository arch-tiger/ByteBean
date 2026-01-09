package com.github.archtiger.definition.invoker.ext.copy;

/**
 * 字段复制器
 * <p>
 * 用于相同名称、相同类型的字段直接复制
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
@FunctionalInterface
public interface FieldCopier extends ObjectCopier {
    /**
     * 通过字段访问将 source 的数据复制到 target
     * <p>
     * 直接访问相同名称、相同类型的字段进行复制
     *
     * @param source 来源对象
     * @param target 目标对象
     */
    @Override
    void copy(Object source, Object target);
}