package com.github.archtiger.bytebean.core.invoker.constructor;

import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConstructorAccess 接口的严格测试
 * <p>
 * 测试包括：
 * 1. 无参构造器调用
 * 2. 单参数构造器调用（所有基本类型和引用类型）
 * 3. 多参数构造器调用
 * 4. 自动拆装箱功能测试
 * 5. 索引越界异常
 * 6. null 值处理
 * 7. 边界值测试
 * 8. 极端场景测试
 */
class ConstructorInvokerTest {
    private ConstructorInvoker constructorInvoker;
    private ConstructorInvokerHelper constructorInvokerHelper;

    @BeforeEach
    void setUp() {
        constructorInvokerHelper = ConstructorInvokerHelper.of(TestConstructorEntity.class);
        constructorInvoker = constructorInvokerHelper.getConstructorInvoker();
    }



    // ==================== 无参构造器测试 ====================

    @Test
    void testNoArgsConstructor() {
        // 测试无参构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex());
        assertNotNull(instance);
        assertTrue(instance instanceof TestConstructorEntity);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(0, entity.getIntValue());
        assertEquals(0L, entity.getLongValue());
        assertEquals(0.0f, entity.getFloatValue(), 0.0f);
        assertEquals(0.0, entity.getDoubleValue(), 0.0);
        assertFalse(entity.isBooleanValue());
        assertEquals((byte) 0, entity.getByteValue());
        assertEquals((short) 0, entity.getShortValue());
        assertEquals('\u0000', entity.getCharValue());
        assertNull(entity.getStringValue());
        assertNull(entity.getIntegerValue());
    }

    // ==================== 单参数构造器测试 ====================

    @Test
    void testSingleIntConstructor() {
        // 测试 int 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), 42);
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
    void testSingleLongConstructor() {
        // 测试 long 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(long.class), 1000L);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(1000L, entity.getLongValue());
    }

    @Test
    void testSingleLongConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Long 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(long.class), Long.valueOf(2000L));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(2000L, entity.getLongValue());
    }

    @Test
    void testSingleFloatConstructor() {
        // 测试 float 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), 3.14f);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(3.14f, entity.getFloatValue(), 0.0001f);
    }

    @Test
    void testSingleFloatConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Float 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), Float.valueOf(2.5f));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(2.5f, entity.getFloatValue(), 0.0001f);
    }

    @Test
    void testSingleDoubleConstructor() {
        // 测试 double 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(double.class), 2.71828);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(2.71828, entity.getDoubleValue(), 0.000001);
    }

    @Test
    void testSingleDoubleConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Double 对象
        int constructorIndex = constructorInvokerHelper.getConstructorIndex(double.class);
        Object instance = constructorInvoker.newInstance(constructorIndex, Double.valueOf(1.5));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(1.5, entity.getDoubleValue(), 0.000001);
    }

    @Test
    void testSingleBooleanConstructor() {
        // 测试 boolean 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(boolean.class), true);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertTrue(entity.isBooleanValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(boolean.class), false);
        entity = (TestConstructorEntity) instance;
        assertFalse(entity.isBooleanValue());
    }

    @Test
    void testSingleBooleanConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Boolean 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(boolean.class), Boolean.valueOf(true));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertTrue(entity.isBooleanValue());
    }

    @Test
    void testSingleByteConstructor() {
        // 测试 byte 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class), (byte) 42);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((byte) 42, entity.getByteValue());
    }

    @Test
    void testSingleByteConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Byte 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class), Byte.valueOf((byte) 100));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((byte) 100, entity.getByteValue());
    }

    @Test
    void testSingleShortConstructor() {
        // 测试 short 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(short.class), (short) 123);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((short) 123, entity.getShortValue());
    }

    @Test
    void testSingleShortConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Short 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(short.class), Short.valueOf((short) 200));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((short) 200, entity.getShortValue());
    }

    @Test
    void testSingleCharConstructor() {
        // 测试 char 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(char.class), 'A');
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals('A', entity.getCharValue());
    }

    @Test
    void testSingleCharConstructorWithBoxedValue() {
        // 测试自动拆箱：传入 Character 对象
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(char.class), Character.valueOf('Z'));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals('Z', entity.getCharValue());
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
    void testSingleStringConstructorWithNull() {
        // 测试 String 参数为 null
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class), (String) null);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertNull(entity.getStringValue());
    }

    @Test
    void testSingleIntegerConstructor() {
        // 测试 Integer 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(Integer.class), 42);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(42, entity.getIntegerValue().intValue());
    }

    @Test
    void testSingleIntegerConstructorWithNull() {
        // 测试 Integer 参数为 null
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(Integer.class), (Integer) null);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertNull(entity.getIntegerValue());
    }

    // ==================== 多参数构造器测试 ====================

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
    void testTwoIntStringConstructor() {
        // 测试 int, String 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, String.class), 42, "hello");
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(42, entity.getIntValue());
        assertEquals("hello", entity.getStringValue());
    }

    @Test
    void testTwoStringIntConstructor() {
        // 测试 String, int 参数构造器（参数顺序不同）
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class, int.class), "world", 99);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals("world", entity.getStringValue());
        assertEquals(99, entity.getIntValue());
    }

    @Test
    void testThreeIntLongDoubleConstructor() {
        // 测试 int, long, double 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class, double.class), 1, 2L, 3.0);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(1, entity.getIntValue());
        assertEquals(2L, entity.getLongValue());
        assertEquals(3.0, entity.getDoubleValue(), 0.000001);
    }

    @Test
    void testFourByteShortIntLongConstructor() {
        // 测试 byte, short, int, long 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class, short.class, int.class, long.class), (byte) 1, (short) 2, 3, 4L);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((byte) 1, entity.getByteValue());
        assertEquals((short) 2, entity.getShortValue());
        assertEquals(3, entity.getIntValue());
        assertEquals(4L, entity.getLongValue());
    }

    @Test
    void testFourFloatDoubleBooleanCharConstructor() {
        // 测试 float, double, boolean, char 参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class, double.class, boolean.class, char.class), 1.0f, 2.0, true, 'X');
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(1.0f, entity.getFloatValue(), 0.0001f);
        assertEquals(2.0, entity.getDoubleValue(), 0.000001);
        assertTrue(entity.isBooleanValue());
        assertEquals('X', entity.getCharValue());
    }

    @Test
    void testEightPrimitivesConstructor() {
        // 测试所有基本类型参数构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class),
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A');
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((byte) 1, entity.getByteValue());
        assertEquals((short) 2, entity.getShortValue());
        assertEquals(3, entity.getIntValue());
        assertEquals(4L, entity.getLongValue());
        assertEquals(5.0f, entity.getFloatValue(), 0.0001f);
        assertEquals(6.0, entity.getDoubleValue(), 0.000001);
        assertTrue(entity.isBooleanValue());
        assertEquals('A', entity.getCharValue());
    }

    @Test
    void testEightPrimitivesConstructorWithBoxedValues() {
        // 测试所有基本类型参数构造器（使用包装类型，测试自动拆箱）
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class),
                Byte.valueOf((byte) 10), Short.valueOf((short) 20), Integer.valueOf(30),
                Long.valueOf(40L), Float.valueOf(50.0f), Double.valueOf(60.0),
                Boolean.valueOf(true), Character.valueOf('Z'));
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals((byte) 10, entity.getByteValue());
        assertEquals((short) 20, entity.getShortValue());
        assertEquals(30, entity.getIntValue());
        assertEquals(40L, entity.getLongValue());
        assertEquals(50.0f, entity.getFloatValue(), 0.0001f);
        assertEquals(60.0, entity.getDoubleValue(), 0.000001);
        assertTrue(entity.isBooleanValue());
        assertEquals('Z', entity.getCharValue());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testIntBoundaryValues() {
        // 测试 int 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), Integer.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Integer.MIN_VALUE, entity.getIntValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), Integer.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Integer.MAX_VALUE, entity.getIntValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), 0);
        entity = (TestConstructorEntity) instance;
        assertEquals(0, entity.getIntValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), -1);
        entity = (TestConstructorEntity) instance;
        assertEquals(-1, entity.getIntValue());
    }

    @Test
    void testLongBoundaryValues() {
        // 测试 long 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(long.class), Long.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Long.MIN_VALUE, entity.getLongValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(long.class), Long.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Long.MAX_VALUE, entity.getLongValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(long.class), 0L);
        entity = (TestConstructorEntity) instance;
        assertEquals(0L, entity.getLongValue());
    }

    @Test
    void testFloatBoundaryValues() {
        // 测试 float 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), Float.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Float.MIN_VALUE, entity.getFloatValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), Float.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Float.MAX_VALUE, entity.getFloatValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), Float.POSITIVE_INFINITY);
        entity = (TestConstructorEntity) instance;
        assertTrue(Float.isInfinite(entity.getFloatValue()));
        assertTrue(entity.getFloatValue() > 0);

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), Float.NEGATIVE_INFINITY);
        entity = (TestConstructorEntity) instance;
        assertTrue(Float.isInfinite(entity.getFloatValue()));
        assertTrue(entity.getFloatValue() < 0);

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), Float.NaN);
        entity = (TestConstructorEntity) instance;
        assertTrue(Float.isNaN(entity.getFloatValue()));

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(float.class), 0.0f);
        entity = (TestConstructorEntity) instance;
        assertEquals(0.0f, entity.getFloatValue(), 0.0f);
    }

    @Test
    void testDoubleBoundaryValues() {
        // 测试 double 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(double.class), Double.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Double.MIN_VALUE, entity.getDoubleValue(), 0.0);

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(double.class), Double.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Double.MAX_VALUE, entity.getDoubleValue(), 0.0);

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(double.class), Double.POSITIVE_INFINITY);
        entity = (TestConstructorEntity) instance;
        assertTrue(Double.isInfinite(entity.getDoubleValue()));
        assertTrue(entity.getDoubleValue() > 0);

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(double.class), Double.NEGATIVE_INFINITY);
        entity = (TestConstructorEntity) instance;
        assertTrue(Double.isInfinite(entity.getDoubleValue()));
        assertTrue(entity.getDoubleValue() < 0);

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(double.class), Double.NaN);
        entity = (TestConstructorEntity) instance;
        assertTrue(Double.isNaN(entity.getDoubleValue()));
    }

    @Test
    void testByteBoundaryValues() {
        // 测试 byte 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class), Byte.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Byte.MIN_VALUE, entity.getByteValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class), Byte.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Byte.MAX_VALUE, entity.getByteValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class), (byte) 0);
        entity = (TestConstructorEntity) instance;
        assertEquals((byte) 0, entity.getByteValue());
    }

    @Test
    void testShortBoundaryValues() {
        // 测试 short 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(short.class), Short.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Short.MIN_VALUE, entity.getShortValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(short.class), Short.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Short.MAX_VALUE, entity.getShortValue());
    }

    @Test
    void testCharBoundaryValues() {
        // 测试 char 边界值
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(char.class), Character.MIN_VALUE);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(Character.MIN_VALUE, entity.getCharValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(char.class), Character.MAX_VALUE);
        entity = (TestConstructorEntity) instance;
        assertEquals(Character.MAX_VALUE, entity.getCharValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(char.class), '\u0000');
        entity = (TestConstructorEntity) instance;
        assertEquals('\u0000', entity.getCharValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(char.class), '中');
        entity = (TestConstructorEntity) instance;
        assertEquals('中', entity.getCharValue());
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class), str);
            TestConstructorEntity entity = (TestConstructorEntity) instance;
            assertEquals(str, entity.getStringValue());
        }
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBounds() {
        // 测试负索引
        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(-1);
        });

        // 测试超出范围的索引
        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(1000);
        });

        // 测试 Integer.MIN_VALUE
        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(Integer.MIN_VALUE);
        });

        // 测试 Integer.MAX_VALUE
        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(Integer.MAX_VALUE);
        });
    }

    @Test
    void testIndexOutOfBoundsWithArgs() {
        // 测试带参数的索引越界
        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(-1, 42);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            constructorInvoker.newInstance(1000, "test");
        });
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullArgsArray() {
        // 测试 args 为 null（无参构造器）
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(), (Object[]) null);
        assertNotNull(instance);
        assertTrue(instance instanceof TestConstructorEntity);
    }

    @Test
    void testEmptyArgsArray() {
        // 测试空数组（无参构造器）
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(), new Object[0]);
        assertNotNull(instance);
        assertTrue(instance instanceof TestConstructorEntity);
    }

    @Test
    void testNullStringArgument() {
        // 测试 String 参数为 null
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class), (String) null);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertNull(entity.getStringValue());
    }

    @Test
    void testNullIntegerArgument() {
        // 测试 Integer 参数为 null
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(Integer.class), (Integer) null);
        assertNotNull(instance);
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertNull(entity.getIntegerValue());
    }

    // ==================== 组合测试 ====================

    @Test
    void testAllConstructors() {
        // 测试所有构造器
        // 无参构造器
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex());
        assertNotNull(instance);

        // 单参数构造器
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), 1);
        assertNotNull(instance);
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(long.class), 2L);
        assertNotNull(instance);
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(String.class), "test");
        assertNotNull(instance);

        // 多参数构造器
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class), 10, 20L);
        assertNotNull(instance);
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class, double.class), 1, 2L, 3.0);
        assertNotNull(instance);
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class),
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A');
        assertNotNull(instance);
    }

    @Test
    void testMultipleInstances() {
        // 测试创建多个实例
        for (int i = 0; i < 10; i++) {
            Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), i);
            assertNotNull(instance);
            TestConstructorEntity entity = (TestConstructorEntity) instance;
            assertEquals(i, entity.getIntValue());
        }
    }

    @Test
    void testMixedPrimitiveAndBoxedValues() {
        // 测试混合使用基本类型和包装类型（自动拆箱）
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class),
                100, Long.valueOf(200L)); // int 基本类型 + Long 包装类型
        TestConstructorEntity entity = (TestConstructorEntity) instance;
        assertEquals(100, entity.getIntValue());
        assertEquals(200L, entity.getLongValue());

        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class, long.class),
                Integer.valueOf(300), 400L); // Integer 包装类型 + long 基本类型
        entity = (TestConstructorEntity) instance;
        assertEquals(300, entity.getIntValue());
        assertEquals(400L, entity.getLongValue());
    }

    @Test
    void testRapidInstantiation() {
        // 快速连续创建实例
        for (int i = 0; i < 1000; i++) {
            Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(int.class), i);
            assertNotNull(instance);
            TestConstructorEntity entity = (TestConstructorEntity) instance;
            assertEquals(i, entity.getIntValue());
        }
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        // 第一个构造器 (index 0 - 无参构造器)
        Object instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex());
        assertNotNull(instance);

        // 最后一个构造器需要知道总构造器数
        // 根据 TestConstructorEntity，应该有 18 个构造器（0-17）
        // 测试最后一个构造器
        instance = constructorInvoker.newInstance(constructorInvokerHelper.getConstructorIndex(byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class),
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A');
        assertNotNull(instance);
    }

    @Test
    void testExceptionMessages() {
        // 测试异常消息
        try {
            constructorInvoker.newInstance(-1);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }

        try {
            constructorInvoker.newInstance(1000);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }
    }

}
