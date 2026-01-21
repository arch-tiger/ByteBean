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
    public Integer field1;
    public Integer field2;
    public Integer field3;
    public Integer field4;
    public Integer field5;
    public Integer field6;
    public Integer field7;
    public Integer field8;
    public Integer field9;
    public Integer field10;

    public static void main(String[] args) {
        ByteBeanReflectUtil.getMethods(Field10Entity.class).forEach(System.out::println);
    }
}
