package com.github.archtiger.core.invoker.field;

/**
 * 用于测试空字段列表的实体类
 * 这个类没有任何非私有、非静态的字段
 */
class EmptyEntity {
    // 所有字段都是私有或静态的，不会被 FieldAccessGenerator 收集
    private int privateField;
    private static int staticField;
}
