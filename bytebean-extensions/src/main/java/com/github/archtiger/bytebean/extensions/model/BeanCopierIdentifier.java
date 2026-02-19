package com.github.archtiger.bytebean.extensions.model;

/**
 * Bean复制器标识符，用于唯一标识一对来源类和目标类的组合。
 * <p>
 * 该Record作为缓存键，用于存储和获取特定类对的复制函数。
 * </p>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 * @param originClass 来源对象类
 * @param targetClass 目标对象类
 */
public record BeanCopierIdentifier(Class<?> originClass, Class<?> targetClass) {
}

