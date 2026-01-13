package com.github.archtiger.definition.bean;

/**
 * 对象复制器接口
 * <p>
 * 基础对象复制接口，支持将 source 的数据复制到 target
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
@FunctionalInterface
public interface ObjectCopier {
    /**
     * 将 source 的数据复制到 target
     * <p>
     * 具体复制方式由实现类决定：
     * - FieldCopier：字段直接复制
     * - PropertyCopier：getter/setter方法复制
     * - ConstructorToMethodCopier：构造器→方法复制（不可变对象复制）
     * - MethodToConstructorCopier：方法→构造器复制（可变到不可变转换）
     *
     * @param source 来源对象
     * @param target 目标对象
     */
    void copy(Object source, Object target);
}
