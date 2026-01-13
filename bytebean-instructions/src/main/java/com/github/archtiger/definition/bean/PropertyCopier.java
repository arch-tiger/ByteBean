package com.github.archtiger.definition.bean;

/**
 * 属性复制器
 * <p>
 * 用于通过 getXXX/setXXX 方法进行数据复制
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
@FunctionalInterface
public interface PropertyCopier extends ObjectCopier {
    /**
     * 通过 getter/setter 方法将 source 的数据复制到 target
     * <p>
     * 调用 source 的 getXXX 方法获取值，然后调用 target 的 setXXX 方法设置值
     *
     * @param source 来源对象
     * @param target 目标对象
     */
    @Override
    void copy(Object source, Object target);
}