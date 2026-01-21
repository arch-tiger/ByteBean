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
    public Integer field11;
    public Integer field12;
    public Integer field13;
    public Integer field14;
    public Integer field15;
    public Integer field16;
    public Integer field17;
    public Integer field18;
    public Integer field19;
    public Integer field20;
    public Integer field21;
    public Integer field22;
    public Integer field23;
    public Integer field24;
    public Integer field25;
    public Integer field26;
    public Integer field27;
    public Integer field28;
    public Integer field29;
    public Integer field30;
    public Integer field31;
    public Integer field32;
    public Integer field33;
    public Integer field34;
    public Integer field35;
    public Integer field36;
    public Integer field37;
    public Integer field38;
    public Integer field39;
    public Integer field40;
    public Integer field41;
    public Integer field42;
    public Integer field43;
    public Integer field44;
    public Integer field45;
    public Integer field46;
    public Integer field47;
    public Integer field48;
    public Integer field49;
    public Integer field50;

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
