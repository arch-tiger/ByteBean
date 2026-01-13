package com.github.archtiger.core.invoker.field;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试空字段列表的情况
 */
class EmptyFieldsTest {

    @Test
    void testEmptyFieldsList() {
        // 测试字段列表为空的情况
        // 当类没有非私有、非静态字段时，字段列表为空
        // 这种情况下，tableswitch(0, -1, ...) 会导致 min > max，这是无效的
        // FieldAccessGenerator 应该抛出 IllegalArgumentException
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            FieldInvokerGenerator.generate(EmptyEntity.class);
        });
        
        assertTrue(exception.getMessage().contains("has no accessible non-static fields"));
    }
}
