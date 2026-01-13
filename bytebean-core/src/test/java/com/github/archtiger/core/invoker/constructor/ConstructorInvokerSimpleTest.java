package com.github.archtiger.core.invoker.constructor;

import com.github.archtiger.definition.constructor.ConstructorInvoker;
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
class ConstructorInvokerSimpleTest {
    private ConstructorInvoker constructorInvoker;
    private ConstructorInvokerHelper constructorInvokerHelper;

    @BeforeEach
    void setUp() {
        constructorInvokerHelper = ConstructorInvokerHelper.of(TestConstructorEntity.class);
        constructorInvoker = constructorInvokerHelper.getConstructorAccess();
    }

    @Test
    void testNoArgsConstructor() {
        // 测试无参构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex());
        assertNotNull(instance);
        assertTrue(instance instanceof TestConstructorEntity);
    }

    @Test
    void testSingleIntConstructor() {
        // 测试 int 参数构造器
        int constructorIndex = constructorInvokerHelper.getConstructorIndex(int.class);
        Object instance = constructorInvoker.newInstance(constructorIndex, 42);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(42, entity.getIntValue());
    }

    @Test
    void testSingleIntConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Integer 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), Integer.valueOf(100));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(100, entity.getIntValue());
    }

    @Test
    void testSingleStringConstructor() {
        // 测试 String 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class), "test");
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals("test", entity.getStringValue());
    }

    @Test
    void testTwoIntLongConstructor() {
        // 测试 int, long 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class), 10, 20L);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(10, entity.getIntValue());
        assertEquals(20L, entity.getLongValue());
    }

    @Test
    void testTwoIntLongConstructorWithBoxedValues() {
        // 测试自动拆箱：传入包装类型
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class), Integer.valueOf(100), Long.valueOf(200L));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(100, entity.getIntValue());
        assertEquals(200L, entity.getLongValue());
    }

    @Test
    void testIndexOutOfBounds() {
        // 测试索引越界异常
        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(1000);
        });
    }

    @Test
    void testNullStringArgument() {
        // 测试 String 参数为 null
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class), (String) null);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertNull(entity.getStringValue());
    }
}
