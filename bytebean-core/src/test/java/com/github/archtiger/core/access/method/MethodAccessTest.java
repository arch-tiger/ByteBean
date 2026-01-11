package com.github.archtiger.core.access.method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MethodAccess 接口的严格测试
 * <p>
 * 测试包括：
 * 1. 通用 invoke 方法（Object 类型）
 * 2. 所有基本类型的返回方法（intInvoke, longInvoke 等）
 * 3. 单参数基本类型方法（invokeInt1, invokeLong1 等）
 * 4. 索引越界异常
 * 5. null 值处理
 * 6. 边界值测试
 * 7. 极端场景测试
 */
class MethodAccessTest {

    private MethodAccess methodAccess;
    private TestMethodEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        Class<? extends MethodAccess> accessClass = MethodAccessGenerator.generate(TestMethodEntity.class);
        methodAccess = accessClass.getDeclaredConstructor().newInstance();
        entity = new TestMethodEntity();
    }

    // ==================== 通用 invoke 方法测试 ====================

    @Test
    void testInvokeNoArgs() {
        // 测试无参数方法
        entity.setInt(42);
        Object result = methodAccess.invoke(0, entity); // getInt()
        assertNotNull(result);
        assertEquals(42, ((Integer) result).intValue());

        entity.setString("test");
        result = methodAccess.invoke(8, entity); // getString()
        assertEquals("test", result);
    }

    @Test
    void testInvokeVoidMethod() {
        // 测试 void 方法
        Object result = methodAccess.invoke(9, entity); // voidMethod()
        assertNull(result);
    }

    @Test
    void testInvokeSingleArg() {
        // 测试单参数方法
        Object result = methodAccess.invoke(10, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodAccess.invoke(0, entity); // getInt()
        assertEquals(100, ((Integer) result).intValue());
    }

    @Test
    void testInvokeMultipleArgs() {
        // 测试多参数方法
        Object result = methodAccess.invoke(18, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodAccess.invoke(22, entity, "hello", "world"); // concatenate(String, String)
        assertEquals("helloworld", result);
    }

    @Test
    void testInvokeAllMethods() {
        // 测试调用所有方法
        methodAccess.invoke(10, entity, 100); // setInt
        assertEquals(100, ((Integer) methodAccess.invoke(0, entity)).intValue());

        methodAccess.invoke(11, entity, 200L); // setLong
        assertEquals(200L, ((Long) methodAccess.invoke(1, entity)).longValue());

        methodAccess.invoke(12, entity, 3.14f); // setFloat
        assertEquals(3.14f, ((Float) methodAccess.invoke(2, entity)).floatValue(), 0.0001f);

        methodAccess.invoke(13, entity, 2.71828); // setDouble
        assertEquals(2.71828, ((Double) methodAccess.invoke(3, entity)).doubleValue(), 0.000001);

        methodAccess.invoke(14, entity, true); // setBoolean
        assertEquals(true, methodAccess.invoke(4, entity));

        methodAccess.invoke(15, entity, (byte) 42); // setByte
        assertEquals((byte) 42, ((Byte) methodAccess.invoke(5, entity)).byteValue());

        methodAccess.invoke(16, entity, (short) 123); // setShort
        assertEquals((short) 123, ((Short) methodAccess.invoke(6, entity)).shortValue());

        methodAccess.invoke(17, entity, 'A'); // setChar
        assertEquals('A', ((Character) methodAccess.invoke(7, entity)).charValue());

        methodAccess.invoke(19, entity, "test"); // setString
        assertEquals("test", methodAccess.invoke(8, entity));
    }

    // ==================== 基本类型返回方法测试 ====================

    @Test
    void testIntInvoke() {
        entity.setInt(100);
        int result = methodAccess.intInvoke(0, entity); // getInt()
        assertEquals(100, result);

        result = methodAccess.intInvoke(27, entity, 50); // addInt(int)
        assertEquals(150, result);
    }

    @Test
    void testLongInvoke() {
        entity.setLong(1000L);
        long result = methodAccess.longInvoke(1, entity); // getLong()
        assertEquals(1000L, result);

        result = methodAccess.longInvoke(28, entity, 500L); // addLong(long)
        assertEquals(1500L, result);
    }

    @Test
    void testFloatInvoke() {
        entity.setFloat(3.14f);
        float result = methodAccess.floatInvoke(2, entity); // getFloat()
        assertEquals(3.14f, result, 0.0001f);

        result = methodAccess.floatInvoke(29, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, result, 0.0001f);
    }

    @Test
    void testDoubleInvoke() {
        entity.setDouble(2.71828);
        double result = methodAccess.doubleInvoke(3, entity); // getDouble()
        assertEquals(2.71828, result, 0.000001);

        result = methodAccess.doubleInvoke(30, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, result, 0.000001);
    }

    @Test
    void testBooleanInvoke() {
        entity.setBoolean(true);
        boolean result = methodAccess.booleanInvoke(4, entity); // getBoolean()
        assertTrue(result);

        result = methodAccess.booleanInvoke(34, entity, 4); // isEven(int) - 4 是偶数
        assertTrue(result);

        result = methodAccess.booleanInvoke(34, entity, 5); // isEven(int) - 5 是奇数
        assertFalse(result);
    }

    @Test
    void testByteInvoke() {
        entity.setByte((byte) 42);
        byte result = methodAccess.byteInvoke(5, entity); // getByte()
        assertEquals((byte) 42, result);

        result = methodAccess.byteInvoke(35, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, result);
    }

    @Test
    void testShortInvoke() {
        entity.setShort((short) 123);
        short result = methodAccess.shortInvoke(6, entity); // getShort()
        assertEquals((short) 123, result);

        result = methodAccess.shortInvoke(36, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, result);
    }

    @Test
    void testCharInvoke() {
        entity.setChar('A');
        char result = methodAccess.charInvoke(7, entity); // getChar()
        assertEquals('A', result);

        result = methodAccess.charInvoke(37, entity, 'A'); // nextChar(char)
        assertEquals('B', result);
    }

    // ==================== 单参数基本类型方法测试 ====================

    @Test
    void testInvokeInt1() {
        Object result = methodAccess.invokeInt1(10, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodAccess.invokeInt1(27, entity, 50); // addInt(int)
        assertNotNull(result);
        assertEquals(50, ((Integer) result).intValue()); // entity.intValue 初始为 0
    }

    @Test
    void testInvokeLong1() {
        Object result = methodAccess.invokeLong1(11, entity, 1000L); // setLong(long)
        assertNull(result);

        result = methodAccess.invokeLong1(28, entity, 500L); // addLong(long)
        assertEquals(500L, ((Long) result).longValue());
    }

    @Test
    void testInvokeFloat1() {
        Object result = methodAccess.invokeFloat1(12, entity, 3.14f); // setFloat(float)
        assertNull(result);

        result = methodAccess.invokeFloat1(29, entity, 2.5f); // addFloat(float)
        assertEquals(2.5f, ((Float) result).floatValue(), 0.0001f);
    }

    @Test
    void testInvokeDouble1() {
        Object result = methodAccess.invokeDouble1(13, entity, 2.71828); // setDouble(double)
        assertNull(result);

        result = methodAccess.invokeDouble1(30, entity, 1.5); // addDouble(double)
        assertEquals(1.5, ((Double) result).doubleValue(), 0.000001);
    }

    @Test
    void testInvokeBoolean1() {
        Object result = methodAccess.invokeBoolean1(14, entity, true); // setBoolean(boolean)
        assertNull(result);

        // 注意：isEven 的参数是 int，不是 boolean，所以 invokeBoolean1 不会匹配到它
        // 这里只测试 setBoolean 方法
        result = methodAccess.invoke(4, entity); // getBoolean
        assertEquals(true, result);
    }

    @Test
    void testInvokeByte1() {
        Object result = methodAccess.invokeByte1(15, entity, (byte) 42); // setByte(byte)
        assertNull(result);

        result = methodAccess.invokeByte1(35, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, ((Byte) result).byteValue());
    }

    @Test
    void testInvokeShort1() {
        Object result = methodAccess.invokeShort1(16, entity, (short) 123); // setShort(short)
        assertNull(result);

        result = methodAccess.invokeShort1(36, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, ((Short) result).shortValue());
    }

    @Test
    void testInvokeChar1() {
        Object result = methodAccess.invokeChar1(17, entity, 'A'); // setChar(char)
        assertNull(result);

        result = methodAccess.invokeChar1(37, entity, 'A'); // nextChar(char)
        assertEquals('B', ((Character) result).charValue());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testIntBoundaryValues() {
        methodAccess.intInvoke(10, entity, Integer.MIN_VALUE); // setInt
        assertEquals(Integer.MIN_VALUE, methodAccess.intInvoke(0, entity));

        methodAccess.intInvoke(10, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodAccess.intInvoke(0, entity));

        methodAccess.intInvoke(10, entity, 0); // setInt
        assertEquals(0, methodAccess.intInvoke(0, entity));

        methodAccess.intInvoke(10, entity, -1); // setInt
        assertEquals(-1, methodAccess.intInvoke(0, entity));
    }

    @Test
    void testLongBoundaryValues() {
        methodAccess.longInvoke(11, entity, Long.MIN_VALUE); // setLong
        assertEquals(Long.MIN_VALUE, methodAccess.longInvoke(1, entity));

        methodAccess.longInvoke(11, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodAccess.longInvoke(1, entity));

        methodAccess.longInvoke(11, entity, 0L); // setLong
        assertEquals(0L, methodAccess.longInvoke(1, entity));

        methodAccess.longInvoke(11, entity, -1L); // setLong
        assertEquals(-1L, methodAccess.longInvoke(1, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        methodAccess.floatInvoke(12, entity, Float.MIN_VALUE); // setFloat
        assertEquals(Float.MIN_VALUE, methodAccess.floatInvoke(2, entity));

        methodAccess.floatInvoke(12, entity, Float.MAX_VALUE); // setFloat
        assertEquals(Float.MAX_VALUE, methodAccess.floatInvoke(2, entity));

        methodAccess.floatInvoke(12, entity, Float.POSITIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodAccess.floatInvoke(2, entity)));
        assertTrue(methodAccess.floatInvoke(2, entity) > 0);

        methodAccess.floatInvoke(12, entity, Float.NEGATIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodAccess.floatInvoke(2, entity)));
        assertTrue(methodAccess.floatInvoke(2, entity) < 0);

        methodAccess.floatInvoke(12, entity, Float.NaN); // setFloat
        assertTrue(Float.isNaN(methodAccess.floatInvoke(2, entity)));

        methodAccess.floatInvoke(12, entity, 0.0f); // setFloat
        assertEquals(0.0f, methodAccess.floatInvoke(2, entity), 0.0f);

        methodAccess.floatInvoke(12, entity, -0.0f); // setFloat
        assertEquals(-0.0f, methodAccess.floatInvoke(2, entity), 0.0f);
    }

    @Test
    void testDoubleBoundaryValues() {
        methodAccess.doubleInvoke(13, entity, Double.MIN_VALUE); // setDouble
        assertEquals(Double.MIN_VALUE, methodAccess.doubleInvoke(3, entity), 0.0);

        methodAccess.doubleInvoke(13, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodAccess.doubleInvoke(3, entity), 0.0);

        methodAccess.doubleInvoke(13, entity, Double.POSITIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodAccess.doubleInvoke(3, entity)));
        assertTrue(methodAccess.doubleInvoke(3, entity) > 0);

        methodAccess.doubleInvoke(13, entity, Double.NEGATIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodAccess.doubleInvoke(3, entity)));
        assertTrue(methodAccess.doubleInvoke(3, entity) < 0);

        methodAccess.doubleInvoke(13, entity, Double.NaN); // setDouble
        assertTrue(Double.isNaN(methodAccess.doubleInvoke(3, entity)));

        methodAccess.doubleInvoke(13, entity, 0.0); // setDouble
        assertEquals(0.0, methodAccess.doubleInvoke(3, entity), 0.0);

        methodAccess.doubleInvoke(13, entity, -0.0); // setDouble
        assertEquals(-0.0, methodAccess.doubleInvoke(3, entity), 0.0);
    }

    @Test
    void testByteBoundaryValues() {
        methodAccess.byteInvoke(15, entity, Byte.MIN_VALUE); // setByte
        assertEquals(Byte.MIN_VALUE, methodAccess.byteInvoke(5, entity));

        methodAccess.byteInvoke(15, entity, Byte.MAX_VALUE); // setByte
        assertEquals(Byte.MAX_VALUE, methodAccess.byteInvoke(5, entity));

        methodAccess.byteInvoke(15, entity, (byte) 0); // setByte
        assertEquals((byte) 0, methodAccess.byteInvoke(5, entity));

        methodAccess.byteInvoke(15, entity, (byte) -1); // setByte
        assertEquals((byte) -1, methodAccess.byteInvoke(5, entity));
    }

    @Test
    void testShortBoundaryValues() {
        methodAccess.shortInvoke(16, entity, Short.MIN_VALUE); // setShort
        assertEquals(Short.MIN_VALUE, methodAccess.shortInvoke(6, entity));

        methodAccess.shortInvoke(16, entity, Short.MAX_VALUE); // setShort
        assertEquals(Short.MAX_VALUE, methodAccess.shortInvoke(6, entity));

        methodAccess.shortInvoke(16, entity, (short) 0); // setShort
        assertEquals((short) 0, methodAccess.shortInvoke(6, entity));

        methodAccess.shortInvoke(16, entity, (short) -1); // setShort
        assertEquals((short) -1, methodAccess.shortInvoke(6, entity));
    }

    @Test
    void testCharBoundaryValues() {
        methodAccess.charInvoke(17, entity, Character.MIN_VALUE); // setChar
        assertEquals(Character.MIN_VALUE, methodAccess.charInvoke(7, entity));

        methodAccess.charInvoke(17, entity, Character.MAX_VALUE); // setChar
        assertEquals(Character.MAX_VALUE, methodAccess.charInvoke(7, entity));

        methodAccess.charInvoke(17, entity, '\u0000'); // setChar
        assertEquals('\u0000', methodAccess.charInvoke(7, entity));

        methodAccess.charInvoke(17, entity, 'A'); // setChar
        assertEquals('A', methodAccess.charInvoke(7, entity));

        methodAccess.charInvoke(17, entity, '中'); // setChar
        assertEquals('中', methodAccess.charInvoke(7, entity));
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBoundsInvoke() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodAccess.invoke(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodAccess.invoke(1000, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodAccess.invoke(Integer.MIN_VALUE, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodAccess.invoke(Integer.MAX_VALUE, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsIntInvoke() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodAccess.intInvoke(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodAccess.intInvoke(1000, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveInvokes() {
        assertThrows(IllegalArgumentException.class, () -> methodAccess.longInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.floatInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.doubleInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.booleanInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.byteInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.shortInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.charInvoke(-1, entity));

        assertThrows(IllegalArgumentException.class, () -> methodAccess.longInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.floatInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.doubleInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.booleanInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.byteInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.shortInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.charInvoke(1000, entity));
    }

    @Test
    void testIndexOutOfBoundsPrimitive1Invokes() {
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeInt1(-1, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeLong1(-1, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeFloat1(-1, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeDouble1(-1, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeBoolean1(-1, entity, true));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeByte1(-1, entity, (byte) 1));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeShort1(-1, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeChar1(-1, entity, 'A'));

        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeInt1(1000, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeLong1(1000, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeFloat1(1000, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeDouble1(1000, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeBoolean1(1000, entity, true));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeByte1(1000, entity, (byte) 1));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeShort1(1000, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> methodAccess.invokeChar1(1000, entity, 'A'));
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        assertThrows(NullPointerException.class, () -> {
            methodAccess.invoke(0, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.invoke(10, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.intInvoke(0, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.intInvoke(10, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.invokeInt1(10, null, 100);
        });
    }

    @Test
    void testNullInstanceAllPrimitiveInvokes() {
        assertThrows(NullPointerException.class, () -> methodAccess.longInvoke(1, null));
        assertThrows(NullPointerException.class, () -> methodAccess.floatInvoke(2, null));
        assertThrows(NullPointerException.class, () -> methodAccess.doubleInvoke(3, null));
        assertThrows(NullPointerException.class, () -> methodAccess.booleanInvoke(4, null));
        assertThrows(NullPointerException.class, () -> methodAccess.byteInvoke(5, null));
        assertThrows(NullPointerException.class, () -> methodAccess.shortInvoke(6, null));
        assertThrows(NullPointerException.class, () -> methodAccess.charInvoke(7, null));
    }

    @Test
    void testNullArguments() {
        // 测试 null 参数（对于引用类型参数）
        methodAccess.invoke(19, entity, (String) null); // setString(null)
        assertNull(methodAccess.invoke(8, entity)); // getString() 应该返回 null

        methodAccess.invoke(20, entity, (Integer) null); // setInteger(null)
        assertNull(methodAccess.invoke(9, entity)); // getInteger() 应该返回 null
    }

    @Test
    void testNullReturnValue() {
        // void 方法返回 null
        Object result = methodAccess.invoke(9, entity); // voidMethod()
        assertNull(result);

        result = methodAccess.invoke(10, entity, 100); // setInt(int) - void
        assertNull(result);
    }

    // ==================== 组合测试 ====================

    @Test
    void testMultipleOperations() {
        // 多次调用同一方法
        for (int i = 0; i < 10; i++) {
            methodAccess.invoke(10, entity, i); // setInt
            Object result = methodAccess.invoke(0, entity); // getInt
            assertEquals(i, ((Integer) result).intValue());
        }
    }

    @Test
    void testMultipleMethods() {
        // 调用多个不同的方法
        methodAccess.invoke(10, entity, 100); // setInt
        methodAccess.invoke(11, entity, 200L); // setLong
        methodAccess.invoke(12, entity, 3.14f); // setFloat
        methodAccess.invoke(13, entity, 2.71828); // setDouble

        assertEquals(100, ((Integer) methodAccess.invoke(0, entity)).intValue());
        assertEquals(200L, ((Long) methodAccess.invoke(1, entity)).longValue());
        assertEquals(3.14f, ((Float) methodAccess.invoke(2, entity)).floatValue(), 0.0001f);
        assertEquals(2.71828, ((Double) methodAccess.invoke(3, entity)).doubleValue(), 0.000001);
    }

    @Test
    void testMixedInvokeTypes() {
        // 混合使用不同的 invoke 方法
        methodAccess.intInvoke(10, entity, 100); // setInt
        assertEquals(100, methodAccess.intInvoke(0, entity)); // getInt

        Object result = methodAccess.invoke(10, entity, 200); // setInt using invoke
        assertNull(result);
        assertEquals(200, ((Integer) methodAccess.invoke(0, entity)).intValue());

        result = methodAccess.invokeInt1(10, entity, 300); // setInt using invokeInt1
        assertNull(result);
        assertEquals(300, ((Integer) methodAccess.invoke(0, entity)).intValue());
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidInvoke() {
        // 快速连续调用
        for (int i = 0; i < 1000; i++) {
            methodAccess.invoke(10, entity, i); // setInt
            Object result = methodAccess.invoke(0, entity); // getInt
            assertEquals(i, ((Integer) result).intValue());
        }
    }

    @Test
    void testLargeArguments() {
        // 测试大参数
        methodAccess.intInvoke(10, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodAccess.intInvoke(0, entity));

        methodAccess.longInvoke(11, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodAccess.longInvoke(1, entity));

        methodAccess.doubleInvoke(13, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodAccess.doubleInvoke(3, entity), 0.0);
    }

    @Test
    void testManyArguments() {
        // 测试多参数方法
        Object result = methodAccess.invoke(24, entity, 1, 2, 3); // addThreeDoubles(double, double, double)
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        result = methodAccess.invoke(23, entity, "a", "b", "c"); // concatenateThree(String, String, String)
        assertEquals("abc", result);
    }

    @Test
    void testAllPrimitiveTypesInOneMethod() {
        // 测试包含所有基本类型参数的方法
        methodAccess.invoke(26, entity,
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A'); // setAllPrimitives
        assertEquals((byte) 1, ((Byte) methodAccess.invoke(5, entity)).byteValue());
        assertEquals((short) 2, ((Short) methodAccess.invoke(6, entity)).shortValue());
        assertEquals(3, ((Integer) methodAccess.invoke(0, entity)).intValue());
        assertEquals(4L, ((Long) methodAccess.invoke(1, entity)).longValue());
        assertEquals(5.0f, ((Float) methodAccess.invoke(2, entity)).floatValue(), 0.0001f);
        assertEquals(6.0, ((Double) methodAccess.invoke(3, entity)).doubleValue(), 0.000001);
        assertEquals(true, methodAccess.invoke(4, entity));
        assertEquals('A', ((Character) methodAccess.invoke(7, entity)).charValue());
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            methodAccess.invoke(19, entity, str); // setString
            assertEquals(str, methodAccess.invoke(8, entity)); // getString
        }
    }

    @Test
    void testPrecisionFloat() {
        // 测试 float 精度
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            methodAccess.floatInvoke(12, entity, value); // setFloat
            assertEquals(value, methodAccess.floatInvoke(2, entity), 0.0001f); // getFloat
        }
    }

    @Test
    void testPrecisionDouble() {
        // 测试 double 精度
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            methodAccess.doubleInvoke(13, entity, value); // setDouble
            assertEquals(value, methodAccess.doubleInvoke(3, entity), 0.000001); // getDouble
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            methodAccess.charInvoke(17, entity, c); // setChar
            assertEquals(c, methodAccess.charInvoke(7, entity)); // getChar
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        for (int i = 0; i < 100; i++) {
            methodAccess.invoke(10, entity, 42); // setInt
            Object result = methodAccess.invoke(0, entity); // getInt
            assertEquals(42, ((Integer) result).intValue());
        }
    }

    @Test
    void testMethodChaining() {
        // 测试方法链式调用
        methodAccess.invoke(10, entity, 10); // setInt(10)
        int result = methodAccess.intInvoke(27, entity, 20); // addInt(20) -> 返回 30
        assertEquals(30, result);

        methodAccess.invoke(11, entity, 100L); // setLong(100L)
        long longResult = methodAccess.longInvoke(28, entity, 200L); // addLong(200L) -> 返回 300L
        assertEquals(300L, longResult);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        // 第一个方法 (index 0)
        entity.setInt(1);
        assertEquals(1, ((Integer) methodAccess.invoke(0, entity)).intValue());

        // 最后一个方法需要知道总方法数，这里测试一个较大的索引（假设在有效范围内）
        // 由于我们不知道确切的方法索引，我们测试一些已知的方法
        Object result = methodAccess.invoke(9, entity); // voidMethod()
        assertNull(result);
    }

    @Test
    void testExceptionMessages() {
        // 测试异常消息
        try {
            methodAccess.invoke(-1, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }

        try {
            methodAccess.intInvoke(1000, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }
    }
}
