package com.github.archtiger.bytebean.api.bean;

/**
 * 方法到构造器复制器
 * <p>
 * 用于从源对象方法提取数据构造新目标对象（方法→构造器复制）
 * 适用于从可变对象创建不可变对象的场景
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
@FunctionalInterface
public interface Method0ToCtorCopier extends ObjectCopier {
    /**
     * 通过方法方式将 source 的数据复制到 target
     * <p>
     * 通过 source 对象的 getter 方法提取数据，然后通过 target 的构造器创建新实例
     * 通常用于从可变对象创建不可变对象（如DTO到Entity的转换）
     *
     * @param source 来源对象
     * @param target 目标对象（通常用于提供构造器信息）
     */
    @Override
    void copy(Object source, Object target);
}