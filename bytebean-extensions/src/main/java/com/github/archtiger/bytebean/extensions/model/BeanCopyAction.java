package com.github.archtiger.bytebean.extensions.model;

/**
 * Bean复制操作，描述单个字段的复制动作。
 * <p>
 * 该Record记录了来源对象的getter索引和目标对象的setter索引，
 * 用于在复制过程中快速定位方法进行调用。
 * </p>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 * @param originGetterIndex 来源对象的getter方法索引
 * @param targetSetterIndex 目标对象的setter方法索引
 */
public record BeanCopyAction(int originGetterIndex, int targetSetterIndex) {
}
