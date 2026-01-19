package com.github.archtiger.bytebean.core.invoker.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 拥有50个字段的测试实体类
 * 用于性能测试
 *
 * @author ZIJIDELU
 * @datetime 2026/1/15
 */
@Setter
@Getter
public class Field50Entity {

    // Getter and Setter methods
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
    private Integer field11;
    private Integer field12;
    private Integer field13;
    private Integer field14;
    private Integer field15;
    private Integer field16;
    private Integer field17;
    private Integer field18;
    private Integer field19;
    private Integer field20;
    private Integer field21;
    private Integer field22;
    private Integer field23;
    private Integer field24;
    private Integer field25;
    private Integer field26;
    private Integer field27;
    private Integer field28;
    private Integer field29;
    private Integer field30;
    private Integer field31;
    private Integer field32;
    private Integer field33;
    private Integer field34;
    private Integer field35;
    private Integer field36;
    private Integer field37;
    private Integer field38;
    private Integer field39;
    private Integer field40;
    private Integer field41;
    private Integer field42;
    private Integer field43;
    private Integer field44;
    private Integer field45;
    private Integer field46;
    private Integer field47;
    private Integer field48;
    private Integer field49;
    private Integer field50;

    public Field50Entity() {
        // 默认构造函数
    }

    // 5参数setter方法 - 用于多参数性能测试
    public void setFiveFields(Integer f1, Integer f2, Integer f3, Integer f4, Integer f5) {
        this.field1 = f1;
        this.field2 = f2;
        this.field3 = f3;
        this.field4 = f4;
        this.field5 = f5;
    }
}
