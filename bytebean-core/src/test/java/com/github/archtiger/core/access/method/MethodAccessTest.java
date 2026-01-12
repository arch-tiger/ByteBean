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
    private MethodAccessHelper methodAccessHelper;
    private MethodAccess methodAccess;
    private TestMethodEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        methodAccessHelper = MethodAccessHelper.of(TestMethodEntity.class);
        methodAccess = methodAccessHelper.getMethodAccess();
        entity = new TestMethodEntity();
    }

    // ==================== 通用 invoke 方法测试 ====================

    @Test
    void testInvokeNoArgs() {
        // 测试无参数方法
        entity.setInt(42);
        int result = methodAccessHelper.intInvoke(entity, "getInt"); // getInt()
        assertEquals(42, result);

        int index_2 = methodAccessHelper.getMethodIndex("getString");
        entity.setString("test");
        String result_2 = (String) methodAccess.invoke(index_2, entity); // getString()
        assertEquals("test", result_2);
    }

    @Test
    void testInvokeVoidMethod() {
        int index = methodAccessHelper.getMethodIndex("voidMethod");
        // 测试 void 方法
        Object result = methodAccess.invoke(index, entity); // voidMethod()

        assertNull(result);
    }

    @Test
    void testInvokeSingleArg() {
        // 测试单参数方法
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");

        Object result = methodAccess.invoke(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodAccess.invoke(getIntIndex, entity); // getInt()
        assertEquals(100, ((Integer) result).intValue());
    }

    @Test
    void testInvokeMultipleArgs() {
        // 测试多参数方法
        int addTwoIntsIndex = methodAccessHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int concatenateIndex = methodAccessHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodAccess.invoke(addTwoIntsIndex, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodAccess.invoke(concatenateIndex, entity, "hello", "world"); // concatenate(String, String)
        assertEquals("helloworld", result);
    }

    @Test
    void testInvokeAllMethods() {
        // 测试调用所有方法
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int setLongIndex = methodAccessHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");
        int setFloatIndex = methodAccessHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");
        int setDoubleIndex = methodAccessHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");
        int setBooleanIndex = methodAccessHelper.getMethodIndex("setBoolean", boolean.class);
        int getBooleanIndex = methodAccessHelper.getMethodIndex("getBoolean");
        int setByteIndex = methodAccessHelper.getMethodIndex("setByte", byte.class);
        int getByteIndex = methodAccessHelper.getMethodIndex("getByte");
        int setShortIndex = methodAccessHelper.getMethodIndex("setShort", short.class);
        int getShortIndex = methodAccessHelper.getMethodIndex("getShort");
        int setCharIndex = methodAccessHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodAccessHelper.getMethodIndex("getChar");
        int setStringIndex = methodAccessHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodAccessHelper.getMethodIndex("getString");

        methodAccess.invoke(setIntIndex, entity, 100); // setInt
        assertEquals(100, ((Integer) methodAccess.invoke(getIntIndex, entity)).intValue());

        methodAccess.invoke(setLongIndex, entity, 200L); // setLong
        assertEquals(200L, ((Long) methodAccess.invoke(getLongIndex, entity)).longValue());

        methodAccess.invoke(setFloatIndex, entity, 3.14f); // setFloat
        assertEquals(3.14f, ((Float) methodAccess.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);

        methodAccess.invoke(setDoubleIndex, entity, 2.71828); // setDouble
        assertEquals(2.71828, ((Double) methodAccess.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);

        methodAccess.invoke(setBooleanIndex, entity, true); // setBoolean
        assertEquals(true, methodAccess.invoke(getBooleanIndex, entity));

        methodAccess.invoke(setByteIndex, entity, (byte) 42); // setByte
        assertEquals((byte) 42, ((Byte) methodAccess.invoke(getByteIndex, entity)).byteValue());

        methodAccess.invoke(setShortIndex, entity, (short) 123); // setShort
        assertEquals((short) 123, ((Short) methodAccess.invoke(getShortIndex, entity)).shortValue());

        methodAccess.invoke(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', ((Character) methodAccess.invoke(getCharIndex, entity)).charValue());

        methodAccess.invoke(setStringIndex, entity, "test"); // setString
        assertEquals("test", methodAccess.invoke(getStringIndex, entity));
    }

    // ==================== 基本类型返回方法测试 ====================

    @Test
    void testIntInvoke() {
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int addIntIndex = methodAccessHelper.getMethodIndex("addInt", int.class);

        entity.setInt(100);
        int result = methodAccess.intInvoke(getIntIndex, entity); // getInt()
        assertEquals(100, result);

        result = methodAccess.intInvoke(addIntIndex, entity, 50); // addInt(int)
        assertEquals(150, result);
    }

    @Test
    void testLongInvoke() {
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");
        int addLongIndex = methodAccessHelper.getMethodIndex("addLong", long.class);

        entity.setLong(1000L);
        long result = methodAccess.longInvoke(getLongIndex, entity); // getLong()
        assertEquals(1000L, result);

        result = methodAccess.longInvoke(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, result);
    }

    @Test
    void testFloatInvoke() {
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");
        int addFloatIndex = methodAccessHelper.getMethodIndex("addFloat", float.class);

        entity.setFloat(3.14f);
        float result = methodAccess.floatInvoke(getFloatIndex, entity); // getFloat()
        assertEquals(3.14f, result, 0.0001f);

        result = methodAccess.floatInvoke(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, result, 0.0001f);
    }

    @Test
    void testDoubleInvoke() {
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");
        int addDoubleIndex = methodAccessHelper.getMethodIndex("addDouble", double.class);

        entity.setDouble(2.71828);
        double result = methodAccess.doubleInvoke(getDoubleIndex, entity); // getDouble()
        assertEquals(2.71828, result, 0.000001);

        result = methodAccess.doubleInvoke(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, result, 0.000001);
    }

    @Test
    void testBooleanInvoke() {
        int getBooleanIndex = methodAccessHelper.getMethodIndex("getBoolean");
        int isEvenIndex = methodAccessHelper.getMethodIndex("isEven", int.class);

        entity.setBoolean(true);
        boolean result = methodAccess.booleanInvoke(getBooleanIndex, entity); // getBoolean()
        assertTrue(result);

        result = methodAccess.booleanInvoke(isEvenIndex, entity, 4); // isEven(int) - 4 是偶数
        assertTrue(result);

        result = methodAccess.booleanInvoke(isEvenIndex, entity, 5); // isEven(int) - 5 是奇数
        assertFalse(result);
    }

    @Test
    void testByteInvoke() {
        int getByteIndex = methodAccessHelper.getMethodIndex("getByte");
        int incrementByteIndex = methodAccessHelper.getMethodIndex("incrementByte", byte.class);

        entity.setByte((byte) 42);
        byte result = methodAccess.byteInvoke(getByteIndex, entity); // getByte()
        assertEquals((byte) 42, result);

        result = methodAccess.byteInvoke(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, result);
    }

    @Test
    void testShortInvoke() {
        int getShortIndex = methodAccessHelper.getMethodIndex("getShort");
        int incrementShortIndex = methodAccessHelper.getMethodIndex("incrementShort", short.class);

        entity.setShort((short) 123);
        short result = methodAccess.shortInvoke(getShortIndex, entity); // getShort()
        assertEquals((short) 123, result);

        result = methodAccess.shortInvoke(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, result);
    }

    @Test
    void testCharInvoke() {
        int getCharIndex = methodAccessHelper.getMethodIndex("getChar");
        int nextCharIndex = methodAccessHelper.getMethodIndex("nextChar", char.class);

        entity.setChar('A');
        char result = methodAccess.charInvoke(getCharIndex, entity); // getChar()
        assertEquals('A', result);

        result = methodAccess.charInvoke(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', result);
    }

    // ==================== 单参数基本类型方法测试 ====================

    @Test
    void testInvokeInt1() {
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodAccessHelper.getMethodIndex("addInt", int.class);

        Object result = methodAccess.invokeInt1(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodAccess.invokeInt1(addIntIndex, entity, 50); // addInt(int)
        assertNotNull(result);
        assertEquals(150, ((Integer) result).intValue()); // entity.intValue 初始为 0
    }

    @Test
    void testInvokeLong1() {
        int setLongIndex = methodAccessHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodAccessHelper.getMethodIndex("addLong", long.class);

        Object result = methodAccess.invokeLong1(setLongIndex, entity, 1000L); // setLong(long)
        assertNull(result);

        result = methodAccess.invokeLong1(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, ((Long) result).longValue());
    }

    @Test
    void testInvokeFloat1() {
        int setFloatIndex = methodAccessHelper.getMethodIndex("setFloat", float.class);
        int addFloatIndex = methodAccessHelper.getMethodIndex("addFloat", float.class);

        Object result = methodAccess.invokeFloat1(setFloatIndex, entity, 3.14f); // setFloat(float)
        assertNull(result);

        result = methodAccess.invokeFloat1(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, ((Float) result).floatValue(), 0.0001f);
    }

    @Test
    void testInvokeDouble1() {
        int setDoubleIndex = methodAccessHelper.getMethodIndex("setDouble", double.class);
        int addDoubleIndex = methodAccessHelper.getMethodIndex("addDouble", double.class);

        Object result = methodAccess.invokeDouble1(setDoubleIndex, entity, 2.71828); // setDouble(double)
        assertNull(result);

        result = methodAccess.invokeDouble1(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, ((Double) result).doubleValue(), 0.000001);
    }

    @Test
    void testInvokeBoolean1() {
        int setBooleanIndex = methodAccessHelper.getMethodIndex("setBoolean", boolean.class);
        int getBooleanIndex = methodAccessHelper.getMethodIndex("getBoolean");

        Object result = methodAccess.invokeBoolean1(setBooleanIndex, entity, true); // setBoolean(boolean)
        assertNull(result);

        // 注意：isEven 的参数是 int，不是 boolean，所以 invokeBoolean1 不会匹配到它
        // 这里只测试 setBoolean 方法
        result = methodAccess.invoke(getBooleanIndex, entity); // getBoolean
        assertEquals(true, result);
    }

    @Test
    void testInvokeByte1() {
        int setByteIndex = methodAccessHelper.getMethodIndex("setByte", byte.class);
        int incrementByteIndex = methodAccessHelper.getMethodIndex("incrementByte", byte.class);

        Object result = methodAccess.invokeByte1(setByteIndex, entity, (byte) 42); // setByte(byte)
        assertNull(result);

        result = methodAccess.invokeByte1(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, ((Byte) result).byteValue());
    }

    @Test
    void testInvokeShort1() {
        int setShortIndex = methodAccessHelper.getMethodIndex("setShort", short.class);
        int incrementShortIndex = methodAccessHelper.getMethodIndex("incrementShort", short.class);

        Object result = methodAccess.invokeShort1(setShortIndex, entity, (short) 123); // setShort(short)
        assertNull(result);

        result = methodAccess.invokeShort1(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, ((Short) result).shortValue());
    }

    @Test
    void testInvokeChar1() {
        int setCharIndex = methodAccessHelper.getMethodIndex("setChar", char.class);
        int nextCharIndex = methodAccessHelper.getMethodIndex("nextChar", char.class);

        Object result = methodAccess.invokeChar1(setCharIndex, entity, 'A'); // setChar(char)
        assertNull(result);

        result = methodAccess.invokeChar1(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', ((Character) result).charValue());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testIntBoundaryValues() {
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");

        methodAccess.invokeInt1(setIntIndex, entity, Integer.MIN_VALUE); // setInt
        assertEquals(Integer.MIN_VALUE, methodAccess.intInvoke(getIntIndex, entity));

        methodAccess.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodAccess.intInvoke(getIntIndex, entity));

        methodAccess.invokeInt1(setIntIndex, entity, 0); // setInt
        assertEquals(0, methodAccess.intInvoke(getIntIndex, entity));

        methodAccess.invokeInt1(setIntIndex, entity, -1); // setInt
        assertEquals(-1, methodAccess.intInvoke(getIntIndex, entity));
    }

    @Test
    void testLongBoundaryValues() {
        int setLongIndex = methodAccessHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");

        methodAccess.invokeLong1(setLongIndex, entity, Long.MIN_VALUE); // setLong
        assertEquals(Long.MIN_VALUE, methodAccess.longInvoke(getLongIndex, entity));

        methodAccess.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodAccess.longInvoke(getLongIndex, entity));

        methodAccess.invokeLong1(setLongIndex, entity, 0L); // setLong
        assertEquals(0L, methodAccess.longInvoke(getLongIndex, entity));

        methodAccess.invokeLong1(setLongIndex, entity, -1L); // setLong
        assertEquals(-1L, methodAccess.longInvoke(getLongIndex, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        int setFloatIndex = methodAccessHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");

        methodAccess.invokeFloat1(setFloatIndex, entity, Float.MIN_VALUE); // setFloat
        assertEquals(Float.MIN_VALUE, methodAccess.floatInvoke(getFloatIndex, entity));

        methodAccess.invokeFloat1(setFloatIndex, entity, Float.MAX_VALUE); // setFloat
        assertEquals(Float.MAX_VALUE, methodAccess.floatInvoke(getFloatIndex, entity));

        methodAccess.invokeFloat1(setFloatIndex, entity, Float.POSITIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodAccess.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodAccess.floatInvoke(getFloatIndex, entity) > 0);

        methodAccess.invokeFloat1(setFloatIndex, entity, Float.NEGATIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodAccess.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodAccess.floatInvoke(getFloatIndex, entity) < 0);

        methodAccess.invokeFloat1(setFloatIndex, entity, Float.NaN); // setFloat
        assertTrue(Float.isNaN(methodAccess.floatInvoke(getFloatIndex, entity)));

        methodAccess.invokeFloat1(setFloatIndex, entity, 0.0f); // setFloat
        assertEquals(0.0f, methodAccess.floatInvoke(getFloatIndex, entity), 0.0f);

        methodAccess.invoke(setFloatIndex, entity, -0.0f); // setFloat
        assertEquals(-0.0f, methodAccess.floatInvoke(getFloatIndex, entity), 0.0f);
    }

    @Test
    void testDoubleBoundaryValues() {
        int setDoubleIndex = methodAccessHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");

        methodAccess.invokeDouble1(setDoubleIndex, entity, Double.MIN_VALUE); // setDouble
        assertEquals(Double.MIN_VALUE, methodAccess.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodAccess.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodAccess.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodAccess.invokeDouble1(setDoubleIndex, entity, Double.POSITIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodAccess.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodAccess.doubleInvoke(getDoubleIndex, entity) > 0);

        methodAccess.invokeDouble1(setDoubleIndex, entity, Double.NEGATIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodAccess.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodAccess.doubleInvoke(getDoubleIndex, entity) < 0);

        methodAccess.invokeDouble1(setDoubleIndex, entity, Double.NaN); // setDouble
        assertTrue(Double.isNaN(methodAccess.doubleInvoke(getDoubleIndex, entity)));

        methodAccess.invokeDouble1(setDoubleIndex, entity, 0.0); // setDouble
        assertEquals(0.0, methodAccess.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodAccess.invokeDouble1(setDoubleIndex, entity, -0.0); // setDouble
        assertEquals(-0.0, methodAccess.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testByteBoundaryValues() {
        int setByteIndex = methodAccessHelper.getMethodIndex("setByte", byte.class);
        int getByteIndex = methodAccessHelper.getMethodIndex("getByte");

        methodAccess.invokeByte1(setByteIndex, entity, Byte.MIN_VALUE); // setByte
        assertEquals(Byte.MIN_VALUE, methodAccess.byteInvoke(getByteIndex, entity));

        methodAccess.invokeByte1(setByteIndex, entity, Byte.MAX_VALUE); // setByte
        assertEquals(Byte.MAX_VALUE, methodAccess.byteInvoke(getByteIndex, entity));

        methodAccess.invokeByte1(setByteIndex, entity, (byte) 0); // setByte
        assertEquals((byte) 0, methodAccess.byteInvoke(getByteIndex, entity));

        methodAccess.invokeByte1(setByteIndex, entity, (byte) -1); // setByte
        assertEquals((byte) -1, methodAccess.byteInvoke(getByteIndex, entity));
    }

    @Test
    void testShortBoundaryValues() {
        int setShortIndex = methodAccessHelper.getMethodIndex("setShort", short.class);
        int getShortIndex = methodAccessHelper.getMethodIndex("getShort");

        methodAccess.invokeShort1(setShortIndex, entity, Short.MIN_VALUE); // setShort
        assertEquals(Short.MIN_VALUE, methodAccess.shortInvoke(getShortIndex, entity));

        methodAccess.invokeShort1(setShortIndex, entity, Short.MAX_VALUE); // setShort
        assertEquals(Short.MAX_VALUE, methodAccess.shortInvoke(getShortIndex, entity));

        methodAccess.invokeShort1(setShortIndex, entity, (short) 0); // setShort
        assertEquals((short) 0, methodAccess.shortInvoke(getShortIndex, entity));

        methodAccess.invokeShort1(setShortIndex, entity, (short) -1); // setShort
        assertEquals((short) -1, methodAccess.shortInvoke(getShortIndex, entity));
    }

    @Test
    void testCharBoundaryValues() {
        int setCharIndex = methodAccessHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodAccessHelper.getMethodIndex("getChar");

        methodAccess.invokeChar1(setCharIndex, entity, Character.MIN_VALUE); // setChar
        assertEquals(Character.MIN_VALUE, methodAccess.charInvoke(getCharIndex, entity));

        methodAccess.invokeChar1(setCharIndex, entity, Character.MAX_VALUE); // setChar
        assertEquals(Character.MAX_VALUE, methodAccess.charInvoke(getCharIndex, entity));

        methodAccess.invokeChar1(setCharIndex, entity, '\u0000'); // setChar
        assertEquals('\u0000', methodAccess.charInvoke(getCharIndex, entity));

        methodAccess.invokeChar1(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', methodAccess.charInvoke(getCharIndex, entity));

        methodAccess.invokeChar1(setCharIndex, entity, '中'); // setChar
        assertEquals('中', methodAccess.charInvoke(getCharIndex, entity));
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
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);

        assertThrows(NullPointerException.class, () -> {
            methodAccess.invoke(getIntIndex, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.invoke(setIntIndex, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.intInvoke(getIntIndex, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.invokeInt1(setIntIndex, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodAccess.invokeInt1(setIntIndex, null, 100);
        });
    }

    @Test
    void testNullInstanceAllPrimitiveInvokes() {
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");
        int getBooleanIndex = methodAccessHelper.getMethodIndex("getBoolean");
        int getByteIndex = methodAccessHelper.getMethodIndex("getByte");
        int getShortIndex = methodAccessHelper.getMethodIndex("getShort");
        int getCharIndex = methodAccessHelper.getMethodIndex("getChar");

        assertThrows(NullPointerException.class, () -> methodAccess.longInvoke(getLongIndex, null));
        assertThrows(NullPointerException.class, () -> methodAccess.floatInvoke(getFloatIndex, null));
        assertThrows(NullPointerException.class, () -> methodAccess.doubleInvoke(getDoubleIndex, null));
        assertThrows(NullPointerException.class, () -> methodAccess.booleanInvoke(getBooleanIndex, null));
        assertThrows(NullPointerException.class, () -> methodAccess.byteInvoke(getByteIndex, null));
        assertThrows(NullPointerException.class, () -> methodAccess.shortInvoke(getShortIndex, null));
        assertThrows(NullPointerException.class, () -> methodAccess.charInvoke(getCharIndex, null));
    }

    @Test
    void testNullArguments() {
        // 测试 null 参数（对于引用类型参数）
        int setStringIndex = methodAccessHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodAccessHelper.getMethodIndex("getString");
        int setIntegerIndex = methodAccessHelper.getMethodIndex("setInteger", Integer.class);
        int getIntegerIndex = methodAccessHelper.getMethodIndex("getInteger");

        methodAccess.invoke(setStringIndex, entity, (String) null); // setString(null)
        assertNull(methodAccess.invoke(getStringIndex, entity)); // getString() 应该返回 null

        methodAccess.invoke(setIntegerIndex, entity, (Integer) null); // setInteger(null)
        assertNull(methodAccess.invoke(getIntegerIndex, entity)); // getInteger() 应该返回 null
    }

    @Test
    void testNullReturnValue() {
        // void 方法返回 null
        int voidMethodIndex = methodAccessHelper.getMethodIndex("voidMethod");
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);

        Object result = methodAccess.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);

        result = methodAccess.invoke(setIntIndex, entity, 100); // setInt(int) - void
        assertNull(result);
    }

    // ==================== 组合测试 ====================

    @Test
    void testMultipleOperations() {
        // 多次调用同一方法
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");

        for (int i = 0; i < 10; i++) {
            methodAccess.invoke(setIntIndex, entity, i); // setInt
            Object result = methodAccess.invoke(getIntIndex, entity); // getInt
            assertEquals(i, ((Integer) result).intValue());
        }
    }

    @Test
    void testMultipleMethods() {
        // 调用多个不同的方法
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int setLongIndex = methodAccessHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");
        int setFloatIndex = methodAccessHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");
        int setDoubleIndex = methodAccessHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");

        methodAccess.invoke(setIntIndex, entity, 100); // setInt
        methodAccess.invoke(setLongIndex, entity, 200L); // setLong
        methodAccess.invoke(setFloatIndex, entity, 3.14f); // setFloat
        methodAccess.invoke(setDoubleIndex, entity, 2.71828); // setDouble

        assertEquals(100, ((Integer) methodAccess.invoke(getIntIndex, entity)).intValue());
        assertEquals(200L, ((Long) methodAccess.invoke(getLongIndex, entity)).longValue());
        assertEquals(3.14f, ((Float) methodAccess.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(2.71828, ((Double) methodAccess.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
    }

    @Test
    void testMixedInvokeTypes() {
        // 混合使用不同的 invoke 方法
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");

        methodAccess.invokeInt1(setIntIndex, entity, 100); // setInt
        assertEquals(100, methodAccess.intInvoke(getIntIndex, entity)); // getInt

        Object result = methodAccess.invoke(setIntIndex, entity, 200); // setInt using invoke
        assertNull(result);
        assertEquals(200, ((Integer) methodAccess.invoke(getIntIndex, entity)).intValue());

        result = methodAccess.invokeInt1(setIntIndex, entity, 300); // setInt using invokeInt1
        assertNull(result);
        assertEquals(300, ((Integer) methodAccess.invoke(getIntIndex, entity)).intValue());
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidInvoke() {
        // 快速连续调用
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");

        for (int i = 0; i < 1000; i++) {
            methodAccess.invoke(setIntIndex, entity, i); // setInt
            Object result = methodAccess.invoke(getIntIndex, entity); // getInt
            assertEquals(i, ((Integer) result).intValue());
        }
    }

    @Test
    void testLargeArguments() {
        // 测试大参数
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int setLongIndex = methodAccessHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");
        int setDoubleIndex = methodAccessHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");

        methodAccess.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodAccess.intInvoke(getIntIndex, entity));

        methodAccess.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodAccess.longInvoke(getLongIndex, entity));

        methodAccess.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodAccess.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testManyArguments() {
        // 测试多参数方法
        int addThreeDoublesIndex = methodAccessHelper.getMethodIndex("addThreeDoubles", double.class, double.class, double.class);
        int concatenateThreeIndex = methodAccessHelper.getMethodIndex("concatenateThree", String.class, String.class, String.class);

        Object result = methodAccess.invoke(addThreeDoublesIndex, entity, 1.0, 2.0, 3.0); // addThreeDoubles(double, double, double)
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        result = methodAccess.invoke(concatenateThreeIndex, entity, "a", "b", "c"); // concatenateThree(String, String, String)
        assertEquals("abc", result);
    }

    @Test
    void testAllPrimitiveTypesInOneMethod() {
        // 测试包含所有基本类型参数的方法
        int setAllPrimitivesIndex = methodAccessHelper.getMethodIndex("setAllPrimitives",
                byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int getLongIndex = methodAccessHelper.getMethodIndex("getLong");
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");
        int getBooleanIndex = methodAccessHelper.getMethodIndex("getBoolean");
        int getByteIndex = methodAccessHelper.getMethodIndex("getByte");
        int getShortIndex = methodAccessHelper.getMethodIndex("getShort");
        int getCharIndex = methodAccessHelper.getMethodIndex("getChar");

        methodAccess.invoke(setAllPrimitivesIndex, entity,
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A'); // setAllPrimitives
        assertEquals((byte) 1, ((Byte) methodAccess.invoke(getByteIndex, entity)).byteValue());
        assertEquals((short) 2, ((Short) methodAccess.invoke(getShortIndex, entity)).shortValue());
        assertEquals(3, ((Integer) methodAccess.invoke(getIntIndex, entity)).intValue());
        assertEquals(4L, ((Long) methodAccess.invoke(getLongIndex, entity)).longValue());
        assertEquals(5.0f, ((Float) methodAccess.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(6.0, ((Double) methodAccess.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
        assertEquals(true, methodAccess.invoke(getBooleanIndex, entity));
        assertEquals('A', ((Character) methodAccess.invoke(getCharIndex, entity)).charValue());
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        int setStringIndex = methodAccessHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodAccessHelper.getMethodIndex("getString");
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            methodAccess.invoke(setStringIndex, entity, str); // setString
            assertEquals(str, methodAccess.invoke(getStringIndex, entity)); // getString
        }
    }

    @Test
    void testPrecisionFloat() {
        // 测试 float 精度
        int setFloatIndex = methodAccessHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodAccessHelper.getMethodIndex("getFloat");
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            methodAccess.invokeFloat1(setFloatIndex, entity, value); // setFloat
            assertEquals(value, methodAccess.floatInvoke(getFloatIndex, entity), 0.0001f); // getFloat
        }
    }

    @Test
    void testPrecisionDouble() {
        // 测试 double 精度
        int setDoubleIndex = methodAccessHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodAccessHelper.getMethodIndex("getDouble");
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            methodAccess.invokeDouble1(setDoubleIndex, entity, value); // setDouble
            assertEquals(value, methodAccess.doubleInvoke(getDoubleIndex, entity), 0.000001); // getDouble
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        int setCharIndex = methodAccessHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodAccessHelper.getMethodIndex("getChar");
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            methodAccess.invokeChar1(setCharIndex, entity, c); // setChar
            assertEquals(c, methodAccess.charInvoke(getCharIndex, entity)); // getChar
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");

        for (int i = 0; i < 100; i++) {
            methodAccess.invoke(setIntIndex, entity, 42); // setInt
            Object result = methodAccess.invoke(getIntIndex, entity); // getInt
            assertEquals(42, ((Integer) result).intValue());
        }
    }

    @Test
    void testMethodChaining() {
        // 测试方法链式调用
        int setIntIndex = methodAccessHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodAccessHelper.getMethodIndex("addInt", int.class);
        int setLongIndex = methodAccessHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodAccessHelper.getMethodIndex("addLong", long.class);

        methodAccess.invoke(setIntIndex, entity, 10); // setInt(10)
        int result = methodAccess.intInvoke(addIntIndex, entity, 20); // addInt(20) -> 返回 30
        assertEquals(30, result);

        methodAccess.invoke(setLongIndex, entity, 100L); // setLong(100L)
        long longResult = methodAccess.longInvoke(addLongIndex, entity, 200L); // addLong(200L) -> 返回 300L
        assertEquals(300L, longResult);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        int getIntIndex = methodAccessHelper.getMethodIndex("getInt");
        int voidMethodIndex = methodAccessHelper.getMethodIndex("voidMethod");

        // 第一个方法
        entity.setInt(1);
        assertEquals(1, ((Integer) methodAccess.invoke(getIntIndex, entity)).intValue());

        // voidMethod
        Object result = methodAccess.invoke(voidMethodIndex, entity); // voidMethod()
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
