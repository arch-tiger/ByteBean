package com.github.archtiger.bytebean.core.invoker.entity;

import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 10个字段的测试实体类
 * 所有字段均为对象类型（Integer）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/15
 */
@Setter
@Getter
public class Field10Entity {
    private Integer field1;
    private Integer field2;
    private Integer field3;
    private Integer field4;
    private Integer field5;
    private Integer field6;
    private Integer field7;
    private Integer field8;
    private Integer field9;
    private Integer field10;

    public static void main(String[] args) {
        ByteBeanReflectUtil.getMethods(Field10Entity.class).forEach(System.out::println);
    }
}
