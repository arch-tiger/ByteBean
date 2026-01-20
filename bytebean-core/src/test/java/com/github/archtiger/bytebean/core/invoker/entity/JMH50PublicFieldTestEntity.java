package com.github.archtiger.bytebean.core.invoker.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * JMH 基准测试用的简单实体
 * 包含 100 个 public Integer 字段，便于 ReflectASM 访问
 *
 * @author ZIJIDELU
 * @datetime 2026/1/20
 */
@Getter
@Setter
public class JMH50PublicFieldTestEntity {

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

    // 为了方便，添加一个设置方法
    public void setAllFields(Integer value) {
        for (int i = 1; i <= 50; i++) {
            try {
                java.lang.reflect.Field field = this.getClass().getField("field" + i);
                field.set(this, value);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
