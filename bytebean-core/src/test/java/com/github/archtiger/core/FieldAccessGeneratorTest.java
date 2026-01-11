package com.github.archtiger.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * FieldAccessGenerator 基础测试
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
@DisplayName("FieldAccessGenerator 基础测试")
class FieldAccessGeneratorTest {

    static class SimpleClass {
        String stringField;
        int intField;
    }

    @Test
    @DisplayName("测试基本类型的 get 和 set")
    void testBasicGetSet() throws Exception {
        Class<? extends FieldAccess> accessor = FieldAccessGenerator.generate(SimpleClass.class);
        FieldAccess fieldAccess = accessor.newInstance();
        SimpleClass obj = new SimpleClass();

        // String 类型
        fieldAccess.set(0, obj, "test");
        assertEquals("test", fieldAccess.get(0, obj));

        // int 类型 (使用通用方法)
        fieldAccess.set(1, obj, 42);
        assertEquals(42, fieldAccess.get(1, obj));
    }
}
