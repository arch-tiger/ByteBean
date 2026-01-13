package com.github.archtiger.core.access.constructor;

import com.github.archtiger.definition.invoker.ConstructorAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConstructorAccess 简单功能测试
 * <p>
 * 测试核心功能：
 * 1. 无参构造器调用
 * 2. 单参数构造器调用（基本类型和引用类型）
 * 3. 多参数构造器调用
 * 4. 自动拆装箱功能
 * 5. 索引越界异常
 */
class ConstructorAccessSimpleTest {
    private ConstructorAccess constructorAccess;
    private ConstructorAccessHelper constructorAccessHelper;

    @BeforeEach
    void setUp() {
        constructorAccessHelper = ConstructorAccessHelper.of(TestConstructorEntity.class);
        constructorAccess = constructorAccessHelper.getConstructorAccess();
    }

    @Test
    void testNoArgsConstructor() {
        // 测试无参构造器
        Object instance = constructorAccess.newInstance(constructorAccessHelper.getConstructorIndex());
        assertNotNull(instance);
        assertTrue(instance instanceof TestConstructorEntity);
    }

    @Test
    void testSingleIntConstructor() {
        // 测试 int 参数构造器
        int constructorIndex = constructorAccessHelper.getConstructorIndex(int.class);
        Object instance = constructorAccess.newInstance(constructorIndex, 42);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(42, entity.getIntValue());
    }

    @Test
    void testSingleIntConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Integer 对象
        Object instance = constructorAccess.newInstance(constructorAccessHelper.getConstructorIndex(int.class), Integer.valueOf(100));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(100, entity.getIntValue());
    }

    @Test
    void testSingleStringConstructor() {
        // 测试 String 参数构造器
        Object instance = constructorAccess.newInstance(constructorAccessHelper.getConstructorIndex(String.class), "test");
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals("test", entity.getStringValue());
    }

    @Test
    void testTwoIntLongConstructor() {
        // 测试 int, long 参数构造器
        Object instance = constructorAccess.newInstance(constructorAccessHelper.getConstructorIndex(int.class, long.class), 10, 20L);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(10, entity.getIntValue());
        assertEquals(20L, entity.getLongValue());
    }

    @Test
    void testTwoIntLongConstructorWithBoxedValues() {
        // 测试自动拆箱：传入包装类型
        Object instance = constructorAccess.newInstance(constructorAccessHelper.getConstructorIndex(int.class, long.class), Integer.valueOf(100), Long.valueOf(200L));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(100, entity.getIntValue());
        assertEquals(200L, entity.getLongValue());
    }

    @Test
    void testIndexOutOfBounds() {
        // 测试索引越界异常
        assertThrows(IllegalArgumentException.class, () -> {
            constructorAccess.newInstance(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            constructorAccess.newInstance(1000);
        });
    }

    @Test
    void testNullStringArgument() {
        // 测试 String 参数为 null
        Object instance = constructorAccess.newInstance(constructorAccessHelper.getConstructorIndex(String.class), (String) null);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertNull(entity.getStringValue());
    }
}
