package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.core.invoker.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.entity.Field300Entity;
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
class MethodInvokerTest {
    private MethodInvokerHelper methodInvokerHelper;
    private TestMethodEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        methodInvokerHelper = MethodInvokerHelper.of(TestMethodEntity.class);
        entity = new TestMethodEntity();
    }

    // ==================== 通用 invoke 方法测试 ====================

    @Test
    void testInvokeNoArgs() {
        // 测试无参数方法
        entity.setInt(42);
        int index_1 = methodInvokerHelper.getMethodIndex("getInt");
        int result = methodInvokerHelper.intInvoke(index_1, entity); // getInt()
        assertEquals(42, result);

        int index_2 = methodInvokerHelper.getMethodIndex("getString");
        entity.setString("test");
        String result_2 = (String) methodInvokerHelper.invoke(index_2, entity); // getString()
        assertEquals("test", result_2);
    }

    @Test
    void testInvokeVoidMethod() {
        int index = methodInvokerHelper.getMethodIndex("voidMethod");
        // 测试 void 方法
        Object result = methodInvokerHelper.invoke(index, entity); // voidMethod()

        assertNull(result);
    }

    @Test
    void testInvokeSingleArg() {
        // 测试单参数方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        Object result = methodInvokerHelper.invoke1(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodInvokerHelper.invoke(getIntIndex, entity); // getInt()
        assertEquals(100, ((Integer) result).intValue());
    }

    @Test
    void testInvokeMultipleArgs() {
        // 测试多参数方法
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodInvokerHelper.invoke(addTwoIntsIndex, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke(concatenateIndex, entity, "hello", "world"); // concatenate(String, String)
        assertEquals("helloworld", result);
    }

    @Test
    void testInvokeAllMethods() {
        // 测试调用所有方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        int setBooleanIndex = methodInvokerHelper.getMethodIndex("setBoolean", boolean.class);
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");

        methodInvokerHelper.invoke1(setIntIndex, entity, 100); // setInt
        assertEquals(100, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());

        methodInvokerHelper.invoke1(setLongIndex, entity, 200L); // setLong
        assertEquals(200L, ((Long) methodInvokerHelper.invoke(getLongIndex, entity)).longValue());

        methodInvokerHelper.invoke1(setFloatIndex, entity, 3.14f); // setFloat
        assertEquals(3.14f, ((Float) methodInvokerHelper.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);

        methodInvokerHelper.invoke1(setDoubleIndex, entity, 2.71828); // setDouble
        assertEquals(2.71828, ((Double) methodInvokerHelper.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);

        methodInvokerHelper.invoke1(setBooleanIndex, entity, true); // setBoolean
        assertEquals(true, methodInvokerHelper.invoke(getBooleanIndex, entity));

        methodInvokerHelper.invoke1(setByteIndex, entity, (byte) 42); // setByte
        assertEquals((byte) 42, ((Byte) methodInvokerHelper.invoke(getByteIndex, entity)).byteValue());

        methodInvokerHelper.invoke1(setShortIndex, entity, (short) 123); // setShort
        assertEquals((short) 123, ((Short) methodInvokerHelper.invoke(getShortIndex, entity)).shortValue());

        methodInvokerHelper.invoke1(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', ((Character) methodInvokerHelper.invoke(getCharIndex, entity)).charValue());

        methodInvokerHelper.invoke1(setStringIndex, entity, "test"); // setString
        assertEquals("test", methodInvokerHelper.invoke(getStringIndex, entity));
    }

    // ==================== 基本类型返回方法测试 ====================

    @Test
    void testIntInvoke() {
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        entity.setInt(100);
        int result = methodInvokerHelper.intInvoke(getIntIndex, entity); // getInt()
        assertEquals(100, result);

        result = methodInvokerHelper.intInvoke(addIntIndex, entity, 50); // addInt(int)
        assertEquals(150, result);
    }

    @Test
    void testLongInvoke() {
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        entity.setLong(1000L);
        long result = methodInvokerHelper.longInvoke(getLongIndex, entity); // getLong()
        assertEquals(1000L, result);

        result = methodInvokerHelper.longInvoke(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, result);
    }

    @Test
    void testFloatInvoke() {
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int addFloatIndex = methodInvokerHelper.getMethodIndex("addFloat", float.class);

        entity.setFloat(3.14f);
        float result = methodInvokerHelper.floatInvoke(getFloatIndex, entity); // getFloat()
        assertEquals(3.14f, result, 0.0001f);

        result = methodInvokerHelper.floatInvoke(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, result, 0.0001f);
    }

    @Test
    void testDoubleInvoke() {
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        int addDoubleIndex = methodInvokerHelper.getMethodIndex("addDouble", double.class);

        entity.setDouble(2.71828);
        double result = methodInvokerHelper.doubleInvoke(getDoubleIndex, entity); // getDouble()
        assertEquals(2.71828, result, 0.000001);

        result = methodInvokerHelper.doubleInvoke(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, result, 0.000001);
    }

    @Test
    void testBooleanInvoke() {
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");
        int isEvenIndex = methodInvokerHelper.getMethodIndex("isEven", int.class);

        entity.setBoolean(true);
        boolean result = methodInvokerHelper.booleanInvoke(getBooleanIndex, entity); // getBoolean()
        assertTrue(result);

        result = methodInvokerHelper.booleanInvoke(isEvenIndex, entity, 4); // isEven(int) - 4 是偶数
        assertTrue(result);

        result = methodInvokerHelper.booleanInvoke(isEvenIndex, entity, 5); // isEven(int) - 5 是奇数
        assertFalse(result);
    }

    @Test
    void testByteInvoke() {
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");
        int incrementByteIndex = methodInvokerHelper.getMethodIndex("incrementByte", byte.class);

        entity.setByte((byte) 42);
        byte result = methodInvokerHelper.byteInvoke(getByteIndex, entity); // getByte()
        assertEquals((byte) 42, result);

        result = methodInvokerHelper.byteInvoke(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, result);
    }

    @Test
    void testShortInvoke() {
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");
        int incrementShortIndex = methodInvokerHelper.getMethodIndex("incrementShort", short.class);

        entity.setShort((short) 123);
        short result = methodInvokerHelper.shortInvoke(getShortIndex, entity); // getShort()
        assertEquals((short) 123, result);

        result = methodInvokerHelper.shortInvoke(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, result);
    }

    @Test
    void testCharInvoke() {
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        int nextCharIndex = methodInvokerHelper.getMethodIndex("nextChar", char.class);

        entity.setChar('A');
        char result = methodInvokerHelper.charInvoke(getCharIndex, entity); // getChar()
        assertEquals('A', result);

        result = methodInvokerHelper.charInvoke(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', result);
    }

    // ==================== 单参数基本类型方法测试 ====================

    @Test
    void testInvokeInt1() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        Object result = methodInvokerHelper.invokeInt1(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodInvokerHelper.invokeInt1(addIntIndex, entity, 50); // addInt(int)
        assertNotNull(result);
        assertEquals(150, ((Integer) result).intValue()); // entity.intValue 初始为 0
    }

    @Test
    void testInvokeLong1() {
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        Object result = methodInvokerHelper.invokeLong1(setLongIndex, entity, 1000L); // setLong(long)
        assertNull(result);

        result = methodInvokerHelper.invokeLong1(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, ((Long) result).longValue());
    }

    @Test
    void testInvokeFloat1() {
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int addFloatIndex = methodInvokerHelper.getMethodIndex("addFloat", float.class);

        Object result = methodInvokerHelper.invokeFloat1(setFloatIndex, entity, 3.14f); // setFloat(float)
        assertNull(result);

        result = methodInvokerHelper.invokeFloat1(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, ((Float) result).floatValue(), 0.0001f);
    }

    @Test
    void testInvokeDouble1() {
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int addDoubleIndex = methodInvokerHelper.getMethodIndex("addDouble", double.class);

        Object result = methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, 2.71828); // setDouble(double)
        assertNull(result);

        result = methodInvokerHelper.invokeDouble1(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, ((Double) result).doubleValue(), 0.000001);
    }

    @Test
    void testInvokeBoolean1() {
        int setBooleanIndex = methodInvokerHelper.getMethodIndex("setBoolean", boolean.class);
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");

        Object result = methodInvokerHelper.invokeBoolean1(setBooleanIndex, entity, true); // setBoolean(boolean)
        assertNull(result);

        // 注意：isEven 的参数是 int，不是 boolean，所以 invokeBoolean1 不会匹配到它
        // 这里只测试 setBoolean 方法
        result = methodInvokerHelper.invoke(getBooleanIndex, entity); // getBoolean
        assertEquals(true, result);
    }

    @Test
    void testInvokeByte1() {
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int incrementByteIndex = methodInvokerHelper.getMethodIndex("incrementByte", byte.class);

        Object result = methodInvokerHelper.invokeByte1(setByteIndex, entity, (byte) 42); // setByte(byte)
        assertNull(result);

        result = methodInvokerHelper.invokeByte1(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, ((Byte) result).byteValue());
    }

    @Test
    void testInvokeShort1() {
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int incrementShortIndex = methodInvokerHelper.getMethodIndex("incrementShort", short.class);

        Object result = methodInvokerHelper.invokeShort1(setShortIndex, entity, (short) 123); // setShort(short)
        assertNull(result);

        result = methodInvokerHelper.invokeShort1(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, ((Short) result).shortValue());
    }

    @Test
    void testInvokeChar1() {
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int nextCharIndex = methodInvokerHelper.getMethodIndex("nextChar", char.class);

        Object result = methodInvokerHelper.invokeChar1(setCharIndex, entity, 'A'); // setChar(char)
        assertNull(result);

        result = methodInvokerHelper.invokeChar1(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', ((Character) result).charValue());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testIntBoundaryValues() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        methodInvokerHelper.invokeInt1(setIntIndex, entity, Integer.MIN_VALUE); // setInt
        assertEquals(Integer.MIN_VALUE, methodInvokerHelper.intInvoke(getIntIndex, entity));

        methodInvokerHelper.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodInvokerHelper.intInvoke(getIntIndex, entity));

        methodInvokerHelper.invokeInt1(setIntIndex, entity, 0); // setInt
        assertEquals(0, methodInvokerHelper.intInvoke(getIntIndex, entity));

        methodInvokerHelper.invokeInt1(setIntIndex, entity, -1); // setInt
        assertEquals(-1, methodInvokerHelper.intInvoke(getIntIndex, entity));
    }

    @Test
    void testLongBoundaryValues() {
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");

        methodInvokerHelper.invokeLong1(setLongIndex, entity, Long.MIN_VALUE); // setLong
        assertEquals(Long.MIN_VALUE, methodInvokerHelper.longInvoke(getLongIndex, entity));

        methodInvokerHelper.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodInvokerHelper.longInvoke(getLongIndex, entity));

        methodInvokerHelper.invokeLong1(setLongIndex, entity, 0L); // setLong
        assertEquals(0L, methodInvokerHelper.longInvoke(getLongIndex, entity));

        methodInvokerHelper.invokeLong1(setLongIndex, entity, -1L); // setLong
        assertEquals(-1L, methodInvokerHelper.longInvoke(getLongIndex, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");

        methodInvokerHelper.invokeFloat1(setFloatIndex, entity, Float.MIN_VALUE); // setFloat
        assertEquals(Float.MIN_VALUE, methodInvokerHelper.floatInvoke(getFloatIndex, entity));

        methodInvokerHelper.invokeFloat1(setFloatIndex, entity, Float.MAX_VALUE); // setFloat
        assertEquals(Float.MAX_VALUE, methodInvokerHelper.floatInvoke(getFloatIndex, entity));

        methodInvokerHelper.invokeFloat1(setFloatIndex, entity, Float.POSITIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodInvokerHelper.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodInvokerHelper.floatInvoke(getFloatIndex, entity) > 0);

        methodInvokerHelper.invokeFloat1(setFloatIndex, entity, Float.NEGATIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodInvokerHelper.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodInvokerHelper.floatInvoke(getFloatIndex, entity) < 0);

        methodInvokerHelper.invokeFloat1(setFloatIndex, entity, Float.NaN); // setFloat
        assertTrue(Float.isNaN(methodInvokerHelper.floatInvoke(getFloatIndex, entity)));

        methodInvokerHelper.invokeFloat1(setFloatIndex, entity, 0.0f); // setFloat
        assertEquals(0.0f, methodInvokerHelper.floatInvoke(getFloatIndex, entity), 0.0f);

        methodInvokerHelper.invoke1(setFloatIndex, entity, -0.0f); // setFloat
        assertEquals(-0.0f, methodInvokerHelper.floatInvoke(getFloatIndex, entity), 0.0f);
    }

    @Test
    void testDoubleBoundaryValues() {
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, Double.MIN_VALUE); // setDouble
        assertEquals(Double.MIN_VALUE, methodInvokerHelper.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodInvokerHelper.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, Double.POSITIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodInvokerHelper.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodInvokerHelper.doubleInvoke(getDoubleIndex, entity) > 0);

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, Double.NEGATIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodInvokerHelper.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodInvokerHelper.doubleInvoke(getDoubleIndex, entity) < 0);

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, Double.NaN); // setDouble
        assertTrue(Double.isNaN(methodInvokerHelper.doubleInvoke(getDoubleIndex, entity)));

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, 0.0); // setDouble
        assertEquals(0.0, methodInvokerHelper.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, -0.0); // setDouble
        assertEquals(-0.0, methodInvokerHelper.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testByteBoundaryValues() {
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");

        methodInvokerHelper.invokeByte1(setByteIndex, entity, Byte.MIN_VALUE); // setByte
        assertEquals(Byte.MIN_VALUE, methodInvokerHelper.byteInvoke(getByteIndex, entity));

        methodInvokerHelper.invokeByte1(setByteIndex, entity, Byte.MAX_VALUE); // setByte
        assertEquals(Byte.MAX_VALUE, methodInvokerHelper.byteInvoke(getByteIndex, entity));

        methodInvokerHelper.invokeByte1(setByteIndex, entity, (byte) 0); // setByte
        assertEquals((byte) 0, methodInvokerHelper.byteInvoke(getByteIndex, entity));

        methodInvokerHelper.invokeByte1(setByteIndex, entity, (byte) -1); // setByte
        assertEquals((byte) -1, methodInvokerHelper.byteInvoke(getByteIndex, entity));
    }

    @Test
    void testShortBoundaryValues() {
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");

        methodInvokerHelper.invokeShort1(setShortIndex, entity, Short.MIN_VALUE); // setShort
        assertEquals(Short.MIN_VALUE, methodInvokerHelper.shortInvoke(getShortIndex, entity));

        methodInvokerHelper.invokeShort1(setShortIndex, entity, Short.MAX_VALUE); // setShort
        assertEquals(Short.MAX_VALUE, methodInvokerHelper.shortInvoke(getShortIndex, entity));

        methodInvokerHelper.invokeShort1(setShortIndex, entity, (short) 0); // setShort
        assertEquals((short) 0, methodInvokerHelper.shortInvoke(getShortIndex, entity));

        methodInvokerHelper.invokeShort1(setShortIndex, entity, (short) -1); // setShort
        assertEquals((short) -1, methodInvokerHelper.shortInvoke(getShortIndex, entity));
    }

    @Test
    void testCharBoundaryValues() {
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");

        methodInvokerHelper.invokeChar1(setCharIndex, entity, Character.MIN_VALUE); // setChar
        assertEquals(Character.MIN_VALUE, methodInvokerHelper.charInvoke(getCharIndex, entity));

        methodInvokerHelper.invokeChar1(setCharIndex, entity, Character.MAX_VALUE); // setChar
        assertEquals(Character.MAX_VALUE, methodInvokerHelper.charInvoke(getCharIndex, entity));

        methodInvokerHelper.invokeChar1(setCharIndex, entity, '\u0000'); // setChar
        assertEquals('\u0000', methodInvokerHelper.charInvoke(getCharIndex, entity));

        methodInvokerHelper.invokeChar1(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', methodInvokerHelper.charInvoke(getCharIndex, entity));

        methodInvokerHelper.invokeChar1(setCharIndex, entity, '中'); // setChar
        assertEquals('中', methodInvokerHelper.charInvoke(getCharIndex, entity));
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBoundsInvoke() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodInvokerHelper.invoke(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvokerHelper.invoke(1000, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvokerHelper.invoke(Integer.MIN_VALUE, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvokerHelper.invoke(Integer.MAX_VALUE, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsIntInvoke() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodInvokerHelper.intInvoke(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvokerHelper.intInvoke(1000, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveInvokes() {
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.longInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.floatInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.doubleInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.booleanInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.byteInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.shortInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.charInvoke(-1, entity));

        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.longInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.floatInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.doubleInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.booleanInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.byteInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.shortInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.charInvoke(1000, entity));
    }

    @Test
    void testIndexOutOfBoundsPrimitive1Invokes() {
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeInt1(-1, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeLong1(-1, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeFloat1(-1, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeDouble1(-1, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeBoolean1(-1, entity, true));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeByte1(-1, entity, (byte) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeShort1(-1, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeChar1(-1, entity, 'A'));

        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeInt1(1000, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeLong1(1000, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeFloat1(1000, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeDouble1(1000, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeBoolean1(1000, entity, true));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeByte1(1000, entity, (byte) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeShort1(1000, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invokeChar1(1000, entity, 'A'));
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(getIntIndex, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke1(setIntIndex, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.intInvoke(getIntIndex, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invokeInt1(setIntIndex, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invokeInt1(setIntIndex, null, 100);
        });
    }

    @Test
    void testNullInstanceAllPrimitiveInvokes() {
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");

        assertThrows(NullPointerException.class, () -> methodInvokerHelper.longInvoke(getLongIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvokerHelper.floatInvoke(getFloatIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvokerHelper.doubleInvoke(getDoubleIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvokerHelper.booleanInvoke(getBooleanIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvokerHelper.byteInvoke(getByteIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvokerHelper.shortInvoke(getShortIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvokerHelper.charInvoke(getCharIndex, null));
    }

    @Test
    void testNullArguments() {
        // 测试 null 参数（对于引用类型参数）
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        int setIntegerIndex = methodInvokerHelper.getMethodIndex("setInteger", Integer.class);
        int getIntegerIndex = methodInvokerHelper.getMethodIndex("getInteger");

        methodInvokerHelper.invoke1(setStringIndex, entity, (String) null); // setString(null)
        assertNull(methodInvokerHelper.invoke(getStringIndex, entity)); // getString() 应该返回 null

        methodInvokerHelper.invoke1(setIntegerIndex, entity, (Integer) null); // setInteger(null)
        assertNull(methodInvokerHelper.invoke(getIntegerIndex, entity)); // getInteger() 应该返回 null
    }

    @Test
    void testNullReturnValue() {
        // void 方法返回 null
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);

        Object result = methodInvokerHelper.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);

        result = methodInvokerHelper.invoke1(setIntIndex, entity, 100); // setInt(int) - void
        assertNull(result);
    }

    // ==================== 组合测试 ====================

    @Test
    void testMultipleOperations() {
        // 多次调用同一方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 10; i++) {
            methodInvokerHelper.invoke1(setIntIndex, entity, i); // setInt
            Object result = methodInvokerHelper.invoke(getIntIndex, entity); // getInt
            assertEquals(i, ((Integer) result).intValue());
        }
    }

    @Test
    void testMultipleMethods() {
        // 调用多个不同的方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");

        methodInvokerHelper.invoke1(setIntIndex, entity, 100); // setInt
        methodInvokerHelper.invoke1(setLongIndex, entity, 200L); // setLong
        methodInvokerHelper.invoke1(setFloatIndex, entity, 3.14f); // setFloat
        methodInvokerHelper.invoke1(setDoubleIndex, entity, 2.71828); // setDouble

        assertEquals(100, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());
        assertEquals(200L, ((Long) methodInvokerHelper.invoke(getLongIndex, entity)).longValue());
        assertEquals(3.14f, ((Float) methodInvokerHelper.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(2.71828, ((Double) methodInvokerHelper.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
    }

    @Test
    void testMixedInvokeTypes() {
        // 混合使用不同的 invoke 方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        methodInvokerHelper.invokeInt1(setIntIndex, entity, 100); // setInt
        assertEquals(100, methodInvokerHelper.intInvoke(getIntIndex, entity)); // getInt

        Object result = methodInvokerHelper.invoke1(setIntIndex, entity, 200); // setInt using invoke
        assertNull(result);
        assertEquals(200, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());

        result = methodInvokerHelper.invokeInt1(setIntIndex, entity, 300); // setInt using invokeInt1
        assertNull(result);
        assertEquals(300, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidInvoke() {
        // 快速连续调用
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 1000; i++) {
            methodInvokerHelper.invoke1(setIntIndex, entity, i); // setInt
            Object result = methodInvokerHelper.invoke(getIntIndex, entity); // getInt
            assertEquals(i, ((Integer) result).intValue());
        }
    }

    @Test
    void testLargeArguments() {
        // 测试大参数
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");

        methodInvokerHelper.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodInvokerHelper.intInvoke(getIntIndex, entity));

        methodInvokerHelper.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodInvokerHelper.longInvoke(getLongIndex, entity));

        methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodInvokerHelper.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testManyArguments() {
        // 测试多参数方法
        int addThreeDoublesIndex = methodInvokerHelper.getMethodIndex("addThreeDoubles", double.class, double.class, double.class);
        int concatenateThreeIndex = methodInvokerHelper.getMethodIndex("concatenateThree", String.class, String.class, String.class);

        Object result = methodInvokerHelper.invoke(addThreeDoublesIndex, entity, 1.0, 2.0, 3.0); // addThreeDoubles(double, double, double)
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        result = methodInvokerHelper.invoke(concatenateThreeIndex, entity, "a", "b", "c"); // concatenateThree(String, String, String)
        assertEquals("abc", result);
    }

    @Test
    void testAllPrimitiveTypesInOneMethod() {
        // 测试包含所有基本类型参数的方法
        int setAllPrimitivesIndex = methodInvokerHelper.getMethodIndex("setAllPrimitives",
                byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");

        methodInvokerHelper.invoke(setAllPrimitivesIndex, entity,
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A'); // setAllPrimitives
        assertEquals((byte) 1, ((Byte) methodInvokerHelper.invoke(getByteIndex, entity)).byteValue());
        assertEquals((short) 2, ((Short) methodInvokerHelper.invoke(getShortIndex, entity)).shortValue());
        assertEquals(3, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());
        assertEquals(4L, ((Long) methodInvokerHelper.invoke(getLongIndex, entity)).longValue());
        assertEquals(5.0f, ((Float) methodInvokerHelper.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(6.0, ((Double) methodInvokerHelper.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
        assertEquals(true, methodInvokerHelper.invoke(getBooleanIndex, entity));
        assertEquals('A', ((Character) methodInvokerHelper.invoke(getCharIndex, entity)).charValue());
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            methodInvokerHelper.invoke1(setStringIndex, entity, str); // setString
            assertEquals(str, methodInvokerHelper.invoke(getStringIndex, entity)); // getString
        }
    }

    @Test
    void testPrecisionFloat() {
        // 测试 float 精度
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            methodInvokerHelper.invokeFloat1(setFloatIndex, entity, value); // setFloat
            assertEquals(value, methodInvokerHelper.floatInvoke(getFloatIndex, entity), 0.0001f); // getFloat
        }
    }

    @Test
    void testPrecisionDouble() {
        // 测试 double 精度
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            methodInvokerHelper.invokeDouble1(setDoubleIndex, entity, value); // setDouble
            assertEquals(value, methodInvokerHelper.doubleInvoke(getDoubleIndex, entity), 0.000001); // getDouble
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            methodInvokerHelper.invokeChar1(setCharIndex, entity, c); // setChar
            assertEquals(c, methodInvokerHelper.charInvoke(getCharIndex, entity)); // getChar
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 100; i++) {
            methodInvokerHelper.invoke1(setIntIndex, entity, 42); // setInt
            Object result = methodInvokerHelper.invoke(getIntIndex, entity); // getInt
            assertEquals(42, ((Integer) result).intValue());
        }
    }

    @Test
    void testMethodChaining() {
        // 测试方法链式调用
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        methodInvokerHelper.invoke1(setIntIndex, entity, 10); // setInt(10)
        int result = methodInvokerHelper.intInvoke(addIntIndex, entity, 20); // addInt(20) -> 返回 30
        assertEquals(30, result);

        methodInvokerHelper.invoke1(setLongIndex, entity, 100L); // setLong(100L)
        long longResult = methodInvokerHelper.longInvoke(addLongIndex, entity, 200L); // addLong(200L) -> 返回 300L
        assertEquals(300L, longResult);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");

        // 第一个方法
        entity.setInt(1);
        assertEquals(1, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());

        // voidMethod
        Object result = methodInvokerHelper.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);
    }

    @Test
    void testExceptionMessages() {
        // 测试异常消息
        try {
            methodInvokerHelper.invoke(-1, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }

        try {
            methodInvokerHelper.intInvoke(1000, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }
    }

    // ==================== 参数为null的测试 ====================

    @Test
    void testNullArgumentsInVarargsInvoke() {
        // 测试可变参数invoke中的null参数
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        int setIntegerIndex = methodInvokerHelper.getMethodIndex("setInteger", Integer.class);
        int getIntegerIndex = methodInvokerHelper.getMethodIndex("getInteger");

        // 使用varargs设置null
        methodInvokerHelper.invoke(setStringIndex, entity, new Object[]{null});
        assertNull(methodInvokerHelper.invoke(getStringIndex, entity));

        methodInvokerHelper.invoke(setIntegerIndex, entity, new Object[]{null});
        assertNull(methodInvokerHelper.invoke(getIntegerIndex, entity));
    }

    @Test
    void testNullArgumentInSingleArgInvoke() {
        // 测试单参数invoke中的null参数
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");

        Object result = methodInvokerHelper.invoke1(setStringIndex, entity, null);
        assertNull(result);
        assertNull(methodInvokerHelper.invoke(getStringIndex, entity));
    }

    @Test
    void testNullArrayInVarargsInvoke() {
        // 测试varargs invoke中传入null数组
        // 注意：传入null数组会抛出NullPointerException
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setStringIndex, entity, (Object[]) null);
        });
    }

    @Test
    void testNullArgumentsForPrimitiveParams() {
        // 测试基本类型参数传入null
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，传入null会抛出NullPointerException
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int setBooleanIndex = methodInvokerHelper.getMethodIndex("setBoolean", boolean.class);
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);

        // 基本类型参数不能为null，自动拆箱时会抛出NullPointerException
        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setLongIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setFloatIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setDoubleIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setBooleanIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setByteIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setShortIndex, entity, new Object[]{null});
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke(setCharIndex, entity, new Object[]{null});
        });
    }

    @Test
    void testNullArgumentForPrimitiveParamsSingleArgInvoke() {
        // 测试单参数invoke中基本类型参数传入null
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，传入null会抛出NullPointerException
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke1(setIntIndex, entity, (Object) null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke1(setLongIndex, entity, (Object) null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke1(setFloatIndex, entity, (Object) null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvokerHelper.invoke1(setDoubleIndex, entity, (Object) null);
        });
    }

    // ==================== 参数数量不匹配的测试 ====================
    // 注意：参数数量不匹配时，MethodInvokerHelper的getMethodIndex方法会抛出IllegalArgumentException
    // 这些测试被移除，因为测试的是MethodInvokerHelper的行为而不是MethodInvoker本身

    @Test
    void testCorrectArgumentCountEdgeCases() {
        // 测试正确参数数量的边界情况
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int addThreeDoublesIndex = methodInvokerHelper.getMethodIndex("addThreeDoubles", double.class, double.class, double.class);
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");

        // 正确的参数数量应该正常工作
        Object result = methodInvokerHelper.invoke(addTwoIntsIndex, entity, new Object[]{10, 20});
        assertEquals(30, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke(addThreeDoublesIndex, entity, new Object[]{1.0, 2.0, 3.0});
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{100});
        assertEquals(100, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());

        // void方法无参数
        result = methodInvokerHelper.invoke(voidMethodIndex, entity);
        assertNull(result);
    }

    // ==================== 参数类型不匹配的测试 ====================

    @Test
    void testWrongArgumentTypeForInt() {
        // 测试int参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        // int参数不能接收String - 会抛出ClassCastException
        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{"not a number"});
        });

        // int参数不能接收long - 会抛出ClassCastException
        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{100L});
        });

        // int参数不能接收double - 会抛出ClassCastException
        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{1.5});
        });

        // int参数不能接收boolean - 会抛出ClassCastException
        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{true});
        });

        // 正确的int类型应该工作
        methodInvokerHelper.invoke(setIntIndex, entity, new Object[]{42});
        assertEquals(42, ((Integer) methodInvokerHelper.invoke(addIntIndex, entity, new Object[]{0})).intValue());
    }

    @Test
    void testWrongArgumentTypeForLong() {
        // 测试long参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setLongIndex, entity, new Object[]{"not a number"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setLongIndex, entity, new Object[]{1.5});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setLongIndex, entity, new Object[]{true});
        });

        // 正确的long类型应该工作
        methodInvokerHelper.invoke(setLongIndex, entity, new Object[]{1000L});
        assertEquals(1000L, ((Long) methodInvokerHelper.invoke(addLongIndex, entity, new Object[]{0L})).longValue());
    }

    @Test
    void testWrongArgumentTypeForFloat() {
        // 测试float参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setFloatIndex, entity, new Object[]{"not a number"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setFloatIndex, entity, new Object[]{true});
        });

        // 正确的float类型应该工作
        methodInvokerHelper.invoke(setFloatIndex, entity, new Object[]{3.14f});
        assertEquals(3.14f, ((Float) methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getFloat"), entity)).floatValue(), 0.0001f);
    }

    @Test
    void testWrongArgumentTypeForDouble() {
        // 测试double参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setDoubleIndex, entity, new Object[]{"not a number"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setDoubleIndex, entity, new Object[]{true});
        });

        // 正确的double类型应该工作
        methodInvokerHelper.invoke(setDoubleIndex, entity, new Object[]{2.71828});
        assertEquals(2.71828, ((Double) methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getDouble"), entity)).doubleValue(), 0.000001);
    }

    @Test
    void testWrongArgumentTypeForBoolean() {
        // 测试boolean参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setBooleanIndex = methodInvokerHelper.getMethodIndex("setBoolean", boolean.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setBooleanIndex, entity, new Object[]{"not a boolean"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setBooleanIndex, entity, new Object[]{1});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setBooleanIndex, entity, new Object[]{1.0});
        });

        // 正确的boolean类型应该工作
        methodInvokerHelper.invoke(setBooleanIndex, entity, new Object[]{true});
        assertEquals(true, methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getBoolean"), entity));
    }

    @Test
    void testWrongArgumentTypeForByte() {
        // 测试byte参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setByteIndex, entity, new Object[]{"not a number"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setByteIndex, entity, new Object[]{true});
        });

        // 正确的byte类型应该工作
        methodInvokerHelper.invoke(setByteIndex, entity, new Object[]{(byte) 42});
        assertEquals((byte) 42, ((Byte) methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getByte"), entity)).byteValue());
    }

    @Test
    void testWrongArgumentTypeForShort() {
        // 测试short参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setShortIndex, entity, new Object[]{"not a number"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setShortIndex, entity, new Object[]{true});
        });

        // 正确的short类型应该工作
        methodInvokerHelper.invoke(setShortIndex, entity, new Object[]{(short) 123});
        assertEquals((short) 123, ((Short) methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getShort"), entity)).shortValue());
    }

    @Test
    void testWrongArgumentTypeForChar() {
        // 测试char参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setCharIndex, entity, new Object[]{"not a char"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setCharIndex, entity, new Object[]{65});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setCharIndex, entity, new Object[]{true});
        });

        // 正确的char类型应该工作
        methodInvokerHelper.invoke(setCharIndex, entity, new Object[]{'A'});
        assertEquals('A', ((Character) methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getChar"), entity)).charValue());
    }

    @Test
    void testWrongArgumentTypeForString() {
        // 测试String参数传入错误类型
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setStringIndex, entity, new Object[]{123});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(setStringIndex, entity, new Object[]{true});
        });

        // 正确的String类型应该工作
        methodInvokerHelper.invoke(setStringIndex, entity, new Object[]{"test"});
        assertEquals("test", methodInvokerHelper.invoke(methodInvokerHelper.getMethodIndex("getString"), entity));
    }

    @Test
    void testMixedWrongArgumentTypes() {
        // 测试多参数方法中的类型不匹配
        // 注意：MethodInvoker不做类型检查，直接进行类型转换，会抛出ClassCastException
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int addThreeDoublesIndex = methodInvokerHelper.getMethodIndex("addThreeDoubles", double.class, double.class, double.class);
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        // 多参数方法中部分参数类型错误
        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(addTwoIntsIndex, entity, new Object[]{10, "20"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(addTwoIntsIndex, entity, new Object[]{"10", 20});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(addThreeDoublesIndex, entity, new Object[]{1.0, 2.0, "3.0"});
        });

        assertThrows(ClassCastException.class, () -> {
            methodInvokerHelper.invoke(concatenateIndex, entity, new Object[]{"a", 123});
        });

        // 正确的参数类型应该工作
        Object result = methodInvokerHelper.invoke(addTwoIntsIndex, entity, new Object[]{10, 20});
        assertEquals(30, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke(concatenateIndex, entity, new Object[]{"hello", "world"});
        assertEquals("helloworld", result);
    }

    @Test
    void testArrayParameter() {
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);
        Object result = methodInvokerHelper.invoke(concatenateIndex, entity, new Object[]{"hello", "world"});
        assertEquals("helloworld", result);
    }

    // ==================== 数值溢出测试 ====================

    @Test
    void testIntOverflow() {
        int addIndex = methodInvokerHelper.getMethodIndex("add", int.class, int.class);

        // Integer.MAX_VALUE + 1 应该溢出变成负数
        int result = methodInvokerHelper.intInvoke(addIndex, entity, Integer.MAX_VALUE, 1);
        assertEquals(Integer.MIN_VALUE, result);

        // Integer.MIN_VALUE - 1 应该溢出变成正数
        result = methodInvokerHelper.intInvoke(addIndex, entity, Integer.MIN_VALUE, -1);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    void testIntMultiplyOverflow() {
        int multiplyIndex = methodInvokerHelper.getMethodIndex("multiply", int.class, int.class);

        // 46341 * 46341 会溢出 (超过 Integer.MAX_VALUE)
        int result = methodInvokerHelper.intInvoke(multiplyIndex, entity, 46341, 46341);
        assertTrue(result < 0); // 溢出后变成负数
    }

    @Test
    void testLongOverflow() {
        int addIndex = methodInvokerHelper.getMethodIndex("addTwoLongs", long.class, long.class);

        // Long.MAX_VALUE + 1 应该溢出变成负数
        Object result = methodInvokerHelper.invoke(addIndex, entity, Long.MAX_VALUE, 1L);
        assertEquals(Long.MIN_VALUE, ((Long) result).longValue());

        // Long.MIN_VALUE - 1 应该溢出变成正数
        result = methodInvokerHelper.invoke(addIndex, entity, Long.MIN_VALUE, -1L);
        assertEquals(Long.MAX_VALUE, ((Long) result).longValue());
    }

    @Test
    void testSubtractUnderflow() {
        int subtractIndex = methodInvokerHelper.getMethodIndex("subtract", int.class, int.class);

        // Integer.MIN_VALUE - 1 应该溢出变成 MAX_VALUE
        int result = methodInvokerHelper.intInvoke(subtractIndex, entity, Integer.MIN_VALUE, 1);
        assertEquals(Integer.MAX_VALUE, result);
    }

    // ==================== 数组/对象参数测试 ====================

    @Test
    void testArrayReturnType() {
        int getArrayIndex = methodInvokerHelper.getMethodIndex("getIntArray");

        entity.setInt(10);
        Object result = methodInvokerHelper.invoke(getArrayIndex, entity);

        assertNotNull(result);
        assertTrue(result instanceof int[]);
        int[] array = (int[]) result;
        assertEquals(3, array.length);
        assertEquals(10, array[0]);
        assertEquals(11, array[1]);
        assertEquals(12, array[2]);
    }

    @Test
    void testArrayArgument() {
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        int[] testArray = new int[]{1, 2, 3, 4, 5};
        Object result = methodInvokerHelper.invoke(sumArrayIndex, entity, new Object[]{testArray});

        assertNotNull(result);
        assertEquals(15, ((Integer) result).intValue());
    }

    @Test
    void testNullArrayArgument() {
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        Object result = methodInvokerHelper.invoke(sumArrayIndex, entity, new Object[]{null});

        assertNotNull(result);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    void testEmptyArrayArgument() {
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        int[] emptyArray = new int[0];
        Object result = methodInvokerHelper.invoke(sumArrayIndex, entity, new Object[]{emptyArray});

        assertNotNull(result);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    void testObjectArgument() {
        int processObjectIndex = methodInvokerHelper.getMethodIndex("processObject", Object.class);

        Object result = methodInvokerHelper.invoke(processObjectIndex, entity, new Object[]{"test string"});
        assertEquals("test string", result);

        result = methodInvokerHelper.invoke(processObjectIndex, entity, new Object[]{123});
        assertEquals("123", result);

        result = methodInvokerHelper.invoke(processObjectIndex, entity, new Object[]{null});
        assertEquals("null", result);
    }

    @Test
    void testMixedArrayReturnType() {
        int getMixedArrayIndex = methodInvokerHelper.getMethodIndex("getMixedArray");

        entity.setInt(42);
        entity.setString("hello");
        entity.setBoolean(true);

        Object result = methodInvokerHelper.invoke(getMixedArrayIndex, entity);

        assertNotNull(result);
        assertTrue(result instanceof Object[]);
        Object[] array = (Object[]) result;
        assertEquals(3, array.length);
        assertEquals(42, array[0]);
        assertEquals("hello", array[1]);
        assertEquals(true, array[2]);
    }

    @Test
    void testStringArrayReturnType() {
        int getStringArrayIndex = methodInvokerHelper.getMethodIndex("getStringArray");

        entity.setString("test");
        Object result = methodInvokerHelper.invoke(getStringArrayIndex, entity);

        assertNotNull(result);
        assertTrue(result instanceof String[]);
        String[] array = (String[]) result;
        assertEquals(2, array.length);
        assertEquals("test", array[0]);
        assertEquals("test2", array[1]);
    }

    // ==================== 边界索引测试 ====================

    @Test
    void testFirstValidIndex() {
        // 测试第一个有效索引 (获取实际的方法索引)
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        entity.setInt(123);

        Object result = methodInvokerHelper.invoke(getIntIndex, entity);
        assertEquals(123, ((Integer) result).intValue());
    }

    @Test
    void testLastValidIndex() {
        // 测试最后一个有效索引
        // 获取所有方法索引来找到最后一个
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");

        Object result = methodInvokerHelper.invoke(voidMethodIndex, entity);
        assertNull(result);
    }

    // ==================== 递归方法测试 ====================

    @Test
    void testRecursiveMethod() {
        int factorialIndex = methodInvokerHelper.getMethodIndex("factorial", int.class);

        // factorial(5) = 120
        int result = methodInvokerHelper.intInvoke(factorialIndex, entity, 5);
        assertEquals(120, result);

        // factorial(10) = 3628800
        result = methodInvokerHelper.intInvoke(factorialIndex, entity, 10);
        assertEquals(3628800, result);

        // factorial(0) = 1
        result = methodInvokerHelper.intInvoke(factorialIndex, entity, 0);
        assertEquals(1, result);

        // factorial(1) = 1
        result = methodInvokerHelper.intInvoke(factorialIndex, entity, 1);
        assertEquals(1, result);
    }

    @Test
    void testRecursiveMethodEdgeCase() {
        int factorialIndex = methodInvokerHelper.getMethodIndex("factorial", int.class);

        // factorial(12) = 479001600
        int result = methodInvokerHelper.intInvoke(factorialIndex, entity, 12);
        assertEquals(479001600, result);
    }

    // ==================== 多实例测试 ====================

    @Test
    void testMultipleInstances() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        TestMethodEntity entity1 = new TestMethodEntity();
        TestMethodEntity entity2 = new TestMethodEntity();

        methodInvokerHelper.invoke1(setIntIndex, entity1, 100);
        methodInvokerHelper.invoke1(setIntIndex, entity2, 200);

        assertEquals(100, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity1)).intValue());
        assertEquals(200, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity2)).intValue());
    }

    // ==================== 混合使用场景测试 ====================

    @Test
    void testMixedInvokeWithArrayAndPrimitives() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getArrayIndex = methodInvokerHelper.getMethodIndex("getIntArray");
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        // 设置值
        methodInvokerHelper.invoke1(setIntIndex, entity, 5);

        // 获取数组
        int[] array = (int[]) methodInvokerHelper.invoke(getArrayIndex, entity);
        assertArrayEquals(new int[]{5, 6, 7}, array);

        // 对数组求和
        int sum = ((Integer) methodInvokerHelper.invoke(sumArrayIndex, entity, new Object[]{array})).intValue();
        assertEquals(18, sum);
    }

    // ==================== 空参数测试补充 ====================

    @Test
    void testEmptyObjectArrayInInvoke1() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        // 使用 invoke1 设置值
        methodInvokerHelper.invoke1(setIntIndex, entity, 42);
        assertEquals(42, ((Integer) methodInvokerHelper.invoke(getIntIndex, entity)).intValue());
    }

    // ==================== 2-5 参数方法测试 ====================

    @Test
    void testInvoke2() {
        // 测试 invoke2 方法
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodInvokerHelper.invoke2(addTwoIntsIndex, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke2(concatenateIndex, entity, "hello", "world"); // concatenate(String, String)
        assertEquals("helloworld", result);
    }

    @Test
    void testInvoke3() {
        // 测试 invoke3 方法
        int addThreeIntsIndex = methodInvokerHelper.getMethodIndex("addThreeInts", int.class, int.class, int.class);
        int concatenateThreeIndex = methodInvokerHelper.getMethodIndex("concatenateThree", String.class, String.class, String.class);

        Object result = methodInvokerHelper.invoke3(addThreeIntsIndex, entity, 1, 2, 3); // addThreeInts(int, int, int)
        assertNotNull(result);
        assertEquals(6, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke3(concatenateThreeIndex, entity, "a", "b", "c"); // concatenateThree(String, String, String)
        assertEquals("abc", result);
    }

    @Test
    void testInvoke4() {
        // 测试 invoke4 方法
        int addFourIntsIndex = methodInvokerHelper.getMethodIndex("addFourInts", int.class, int.class, int.class, int.class);
        int concatenateFourIndex = methodInvokerHelper.getMethodIndex("concatenateFour", String.class, String.class, String.class, String.class);

        Object result = methodInvokerHelper.invoke4(addFourIntsIndex, entity, 1, 2, 3, 4); // addFourInts(int, int, int, int)
        assertNotNull(result);
        assertEquals(10, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke4(concatenateFourIndex, entity, "a", "b", "c", "d"); // concatenateFour(String, String, String, String)
        assertEquals("abcd", result);
    }

    @Test
    void testInvoke5() {
        // 测试 invoke5 方法
        int addFiveIntsIndex = methodInvokerHelper.getMethodIndex("addFiveInts", int.class, int.class, int.class, int.class, int.class);
        int concatenateFiveIndex = methodInvokerHelper.getMethodIndex("concatenateFive", String.class, String.class, String.class, String.class, String.class);

        Object result = methodInvokerHelper.invoke5(addFiveIntsIndex, entity, 1, 2, 3, 4, 5); // addFiveInts(int, int, int, int, int)
        assertNotNull(result);
        assertEquals(15, ((Integer) result).intValue());

        result = methodInvokerHelper.invoke5(concatenateFiveIndex, entity, "a", "b", "c", "d", "e"); // concatenateFive(String, String, String, String, String)
        assertEquals("abcde", result);
    }

    @Test
    void testInvoke2WithVoidReturn() {
        // 测试 invoke2 的 void 返回方法
        int setTwoIntsIndex = methodInvokerHelper.getMethodIndex("setTwoInts", int.class, int.class);

        Object result = methodInvokerHelper.invoke2(setTwoIntsIndex, entity, 100, 200);
        assertNull(result);

        // 验证值被正确设置
        assertEquals(100, entity.getInt());
        assertEquals(200L, entity.getLong());
    }

    @Test
    void testInvoke3WithVoidReturn() {
        // 测试 invoke3 的 void 返回方法
        int setThreeIntsIndex = methodInvokerHelper.getMethodIndex("setThreeInts", int.class, int.class, int.class);

        Object result = methodInvokerHelper.invoke3(setThreeIntsIndex, entity, 1, 2, 3);
        assertNull(result);

        // 验证值被正确设置
        assertEquals(1, entity.getInt());
        assertEquals(2L, entity.getLong());
        assertEquals(3.0f, entity.getFloat(), 0.0001f);
    }

    @Test
    void testInvoke4WithVoidReturn() {
        // 测试 invoke4 的 void 返回方法
        int setFourIntsIndex = methodInvokerHelper.getMethodIndex("setFourInts", int.class, int.class, int.class, int.class);

        Object result = methodInvokerHelper.invoke4(setFourIntsIndex, entity, 1, 2, 3, 4);
        assertNull(result);

        // 验证值被正确设置
        assertEquals(1, entity.getInt());
        assertEquals(2L, entity.getLong());
        assertEquals(3.0f, entity.getFloat(), 0.0001f);
        assertEquals(4.0, entity.getDouble(), 0.000001);
    }

    @Test
    void testInvoke5WithVoidReturn() {
        // 测试 invoke5 的 void 返回方法
        int setFiveIntsIndex = methodInvokerHelper.getMethodIndex("setFiveInts", int.class, int.class, int.class, int.class, int.class);

        Object result = methodInvokerHelper.invoke5(setFiveIntsIndex, entity, 1, 2, 3, 4, 5);
        assertNull(result);

        // 验证值被正确设置
        assertEquals(1, entity.getInt());
        assertEquals(2L, entity.getLong());
        assertEquals(3.0f, entity.getFloat(), 0.0001f);
        assertEquals(4.0, entity.getDouble(), 0.000001);
        assertEquals(true, entity.getBoolean()); // 5 > 0, 所以 booleanValue = true
    }

    @Test
    void testInvoke2MixedTypes() {
        // 测试 invoke2 的混合类型
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodInvokerHelper.invoke2(concatenateIndex, entity, "test", "123");
        assertEquals("test123", result);
    }

    @Test
    void testInvoke3MixedTypes() {
        // 测试 invoke3 的混合类型
        int addMixedIndex = methodInvokerHelper.getMethodIndex("addMixed", int.class, long.class, double.class);

        Object result = methodInvokerHelper.invoke3(addMixedIndex, entity, 1, 2L, 3.0);
        assertEquals(6, ((Integer) result).intValue());
    }

    @Test
    void testIndexOutOfBoundsInvoke2To5() {
        // 测试 invoke2-invoke5 的索引越界
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);

        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke2(-1, entity, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke2(1000, entity, 1, 2));

        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke3(-1, entity, 1, 2, 3));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke3(1000, entity, 1, 2, 3));

        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke4(-1, entity, 1, 2, 3, 4));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke4(1000, entity, 1, 2, 3, 4));

        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke5(-1, entity, 1, 2, 3, 4, 5));
        assertThrows(IllegalArgumentException.class, () -> methodInvokerHelper.invoke5(1000, entity, 1, 2, 3, 4, 5));
    }

    // ==================== 大量方法对象测试 ====================

    @Test
    void testEntityWithMoreThan100Methods() {
        // 测试拥有超过100个方法的实体类 (Field300Entity有600个方法：300个getter + 300个setter)
        MethodInvokerHelper field300Helper = MethodInvokerHelper.of(Field300Entity.class);
        Field300Entity entity300 = new Field300Entity();

        // 测试前10个字段的读写
        for (int i = 1; i <= 10; i++) {
            int setFieldIndex = field300Helper.getMethodIndex("setField" + i, Integer.class);
            int getFieldIndex = field300Helper.getMethodIndex("getField" + i);

            // 设置值
            field300Helper.invoke1(setFieldIndex, entity300, i * 100);

            // 获取值并验证
            Integer value = (Integer) field300Helper.invoke(getFieldIndex, entity300);
            assertEquals(i * 100, value.intValue());
        }

        // 测试中间字段的读写 (field150)
        int setField150Index = field300Helper.getMethodIndex("setField150", Integer.class);
        int getField150Index = field300Helper.getMethodIndex("getField150");
        field300Helper.invoke1(setField150Index, entity300, 15000);
        assertEquals(15000, ((Integer) field300Helper.invoke(getField150Index, entity300)).intValue());

        // 测试最后几个字段的读写
        for (int i = 295; i <= 300; i++) {
            int setFieldIndex = field300Helper.getMethodIndex("setField" + i, Integer.class);
            int getFieldIndex = field300Helper.getMethodIndex("getField" + i);

            // 设置值
            field300Helper.invoke1(setFieldIndex, entity300, i * 100);

            // 获取值并验证
            Integer value = (Integer) field300Helper.invoke(getFieldIndex, entity300);
            assertEquals(i * 100, value.intValue());
        }
    }
}
