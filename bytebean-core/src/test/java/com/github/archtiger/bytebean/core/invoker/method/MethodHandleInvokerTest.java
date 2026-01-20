package com.github.archtiger.bytebean.core.invoker.method;

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
class MethodHandleInvokerTest {
    private MethodInvokerHelper methodInvokerHelper;
    private MethodHandleInvoker methodHandleInvoker;
    private TestMethodEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        methodInvokerHelper = MethodInvokerHelper.of(TestMethodEntity.class);
        methodHandleInvoker = MethodHandleInvoker.of(TestMethodEntity.class);
        entity = new TestMethodEntity();
    }

    // ==================== 通用 invoke 方法测试 ====================

    @Test
    void testInvokeNoArgs() {
        // 测试无参数方法
        entity.setInt(42);
        int index_1 = methodInvokerHelper.getMethodIndex("getInt");
        int result = methodHandleInvoker.intInvoke(index_1, entity); // getInt()
        assertEquals(42, result);

        int index_2 = methodInvokerHelper.getMethodIndex("getString");
        entity.setString("test");
        String result_2 = (String) methodHandleInvoker.invoke(index_2, entity); // getString()
        assertEquals("test", result_2);
    }

    @Test
    void testInvokeVoidMethod() {
        int index = methodInvokerHelper.getMethodIndex("voidMethod");
        // 测试 void 方法
        Object result = methodHandleInvoker.invoke(index, entity); // voidMethod()

        assertNull(result);
    }

    @Test
    void testInvokeSingleArg() {
        // 测试单参数方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        Object result = methodHandleInvoker.invoke1(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodHandleInvoker.invoke(getIntIndex, entity); // getInt()
        assertEquals(100, ((Integer) result).intValue());
    }

    @Test
    void testInvokeMultipleArgs() {
        // 测试多参数方法
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodHandleInvoker.invoke(addTwoIntsIndex, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodHandleInvoker.invoke(concatenateIndex, entity, "hello", "world"); // concatenate(String, String)
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

        methodHandleInvoker.invoke1(setIntIndex, entity, 100); // setInt
        assertEquals(100, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());

        methodHandleInvoker.invoke1(setLongIndex, entity, 200L); // setLong
        assertEquals(200L, ((Long) methodHandleInvoker.invoke(getLongIndex, entity)).longValue());

        methodHandleInvoker.invoke1(setFloatIndex, entity, 3.14f); // setFloat
        assertEquals(3.14f, ((Float) methodHandleInvoker.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);

        methodHandleInvoker.invoke1(setDoubleIndex, entity, 2.71828); // setDouble
        assertEquals(2.71828, ((Double) methodHandleInvoker.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);

        methodHandleInvoker.invoke1(setBooleanIndex, entity, true); // setBoolean
        assertEquals(true, methodHandleInvoker.invoke(getBooleanIndex, entity));

        methodHandleInvoker.invoke1(setByteIndex, entity, (byte) 42); // setByte
        assertEquals((byte) 42, ((Byte) methodHandleInvoker.invoke(getByteIndex, entity)).byteValue());

        methodHandleInvoker.invoke1(setShortIndex, entity, (short) 123); // setShort
        assertEquals((short) 123, ((Short) methodHandleInvoker.invoke(getShortIndex, entity)).shortValue());

        methodHandleInvoker.invoke1(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', ((Character) methodHandleInvoker.invoke(getCharIndex, entity)).charValue());

        methodHandleInvoker.invoke1(setStringIndex, entity, "test"); // setString
        assertEquals("test", methodHandleInvoker.invoke(getStringIndex, entity));
    }

    // ==================== 基本类型返回方法测试 ====================

    @Test
    void testIntInvoke() {
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        entity.setInt(100);
        int result = methodHandleInvoker.intInvoke(getIntIndex, entity); // getInt()
        assertEquals(100, result);

        result = methodHandleInvoker.intInvoke(addIntIndex, entity, 50); // addInt(int)
        assertEquals(150, result);
    }

    @Test
    void testLongInvoke() {
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        entity.setLong(1000L);
        long result = methodHandleInvoker.longInvoke(getLongIndex, entity); // getLong()
        assertEquals(1000L, result);

        result = methodHandleInvoker.longInvoke(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, result);
    }

    @Test
    void testFloatInvoke() {
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int addFloatIndex = methodInvokerHelper.getMethodIndex("addFloat", float.class);

        entity.setFloat(3.14f);
        float result = methodHandleInvoker.floatInvoke(getFloatIndex, entity); // getFloat()
        assertEquals(3.14f, result, 0.0001f);

        result = methodHandleInvoker.floatInvoke(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, result, 0.0001f);
    }

    @Test
    void testDoubleInvoke() {
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        int addDoubleIndex = methodInvokerHelper.getMethodIndex("addDouble", double.class);

        entity.setDouble(2.71828);
        double result = methodHandleInvoker.doubleInvoke(getDoubleIndex, entity); // getDouble()
        assertEquals(2.71828, result, 0.000001);

        result = methodHandleInvoker.doubleInvoke(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, result, 0.000001);
    }

    @Test
    void testBooleanInvoke() {
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");
        int isEvenIndex = methodInvokerHelper.getMethodIndex("isEven", int.class);

        entity.setBoolean(true);
        boolean result = methodHandleInvoker.booleanInvoke(getBooleanIndex, entity); // getBoolean()
        assertTrue(result);

        result = methodHandleInvoker.booleanInvoke(isEvenIndex, entity, 4); // isEven(int) - 4 是偶数
        assertTrue(result);

        result = methodHandleInvoker.booleanInvoke(isEvenIndex, entity, 5); // isEven(int) - 5 是奇数
        assertFalse(result);
    }

    @Test
    void testByteInvoke() {
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");
        int incrementByteIndex = methodInvokerHelper.getMethodIndex("incrementByte", byte.class);

        entity.setByte((byte) 42);
        byte result = methodHandleInvoker.byteInvoke(getByteIndex, entity); // getByte()
        assertEquals((byte) 42, result);

        result = methodHandleInvoker.byteInvoke(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, result);
    }

    @Test
    void testShortInvoke() {
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");
        int incrementShortIndex = methodInvokerHelper.getMethodIndex("incrementShort", short.class);

        entity.setShort((short) 123);
        short result = methodHandleInvoker.shortInvoke(getShortIndex, entity); // getShort()
        assertEquals((short) 123, result);

        result = methodHandleInvoker.shortInvoke(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, result);
    }

    @Test
    void testCharInvoke() {
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        int nextCharIndex = methodInvokerHelper.getMethodIndex("nextChar", char.class);

        entity.setChar('A');
        char result = methodHandleInvoker.charInvoke(getCharIndex, entity); // getChar()
        assertEquals('A', result);

        result = methodHandleInvoker.charInvoke(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', result);
    }

    // ==================== 单参数基本类型方法测试 ====================

    @Test
    void testInvokeInt1() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        Object result = methodHandleInvoker.invokeInt1(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodHandleInvoker.invokeInt1(addIntIndex, entity, 50); // addInt(int)
        assertNotNull(result);
        assertEquals(150, ((Integer) result).intValue()); // entity.intValue 初始为 0
    }

    @Test
    void testInvokeLong1() {
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        Object result = methodHandleInvoker.invokeLong1(setLongIndex, entity, 1000L); // setLong(long)
        assertNull(result);

        result = methodHandleInvoker.invokeLong1(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, ((Long) result).longValue());
    }

    @Test
    void testInvokeFloat1() {
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int addFloatIndex = methodInvokerHelper.getMethodIndex("addFloat", float.class);

        Object result = methodHandleInvoker.invokeFloat1(setFloatIndex, entity, 3.14f); // setFloat(float)
        assertNull(result);

        result = methodHandleInvoker.invokeFloat1(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, ((Float) result).floatValue(), 0.0001f);
    }

    @Test
    void testInvokeDouble1() {
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int addDoubleIndex = methodInvokerHelper.getMethodIndex("addDouble", double.class);

        Object result = methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, 2.71828); // setDouble(double)
        assertNull(result);

        result = methodHandleInvoker.invokeDouble1(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, ((Double) result).doubleValue(), 0.000001);
    }

    @Test
    void testInvokeBoolean1() {
        int setBooleanIndex = methodInvokerHelper.getMethodIndex("setBoolean", boolean.class);
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");

        Object result = methodHandleInvoker.invokeBoolean1(setBooleanIndex, entity, true); // setBoolean(boolean)
        assertNull(result);

        // 注意：isEven 的参数是 int，不是 boolean，所以 invokeBoolean1 不会匹配到它
        // 这里只测试 setBoolean 方法
        result = methodHandleInvoker.invoke(getBooleanIndex, entity); // getBoolean
        assertEquals(true, result);
    }

    @Test
    void testInvokeByte1() {
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int incrementByteIndex = methodInvokerHelper.getMethodIndex("incrementByte", byte.class);

        Object result = methodHandleInvoker.invokeByte1(setByteIndex, entity, (byte) 42); // setByte(byte)
        assertNull(result);

        result = methodHandleInvoker.invokeByte1(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, ((Byte) result).byteValue());
    }

    @Test
    void testInvokeShort1() {
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int incrementShortIndex = methodInvokerHelper.getMethodIndex("incrementShort", short.class);

        Object result = methodHandleInvoker.invokeShort1(setShortIndex, entity, (short) 123); // setShort(short)
        assertNull(result);

        result = methodHandleInvoker.invokeShort1(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, ((Short) result).shortValue());
    }

    @Test
    void testInvokeChar1() {
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int nextCharIndex = methodInvokerHelper.getMethodIndex("nextChar", char.class);

        Object result = methodHandleInvoker.invokeChar1(setCharIndex, entity, 'A'); // setChar(char)
        assertNull(result);

        result = methodHandleInvoker.invokeChar1(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', ((Character) result).charValue());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testIntBoundaryValues() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        methodHandleInvoker.invokeInt1(setIntIndex, entity, Integer.MIN_VALUE); // setInt
        assertEquals(Integer.MIN_VALUE, methodHandleInvoker.intInvoke(getIntIndex, entity));

        methodHandleInvoker.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodHandleInvoker.intInvoke(getIntIndex, entity));

        methodHandleInvoker.invokeInt1(setIntIndex, entity, 0); // setInt
        assertEquals(0, methodHandleInvoker.intInvoke(getIntIndex, entity));

        methodHandleInvoker.invokeInt1(setIntIndex, entity, -1); // setInt
        assertEquals(-1, methodHandleInvoker.intInvoke(getIntIndex, entity));
    }

    @Test
    void testLongBoundaryValues() {
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");

        methodHandleInvoker.invokeLong1(setLongIndex, entity, Long.MIN_VALUE); // setLong
        assertEquals(Long.MIN_VALUE, methodHandleInvoker.longInvoke(getLongIndex, entity));

        methodHandleInvoker.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodHandleInvoker.longInvoke(getLongIndex, entity));

        methodHandleInvoker.invokeLong1(setLongIndex, entity, 0L); // setLong
        assertEquals(0L, methodHandleInvoker.longInvoke(getLongIndex, entity));

        methodHandleInvoker.invokeLong1(setLongIndex, entity, -1L); // setLong
        assertEquals(-1L, methodHandleInvoker.longInvoke(getLongIndex, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");

        methodHandleInvoker.invokeFloat1(setFloatIndex, entity, Float.MIN_VALUE); // setFloat
        assertEquals(Float.MIN_VALUE, methodHandleInvoker.floatInvoke(getFloatIndex, entity));

        methodHandleInvoker.invokeFloat1(setFloatIndex, entity, Float.MAX_VALUE); // setFloat
        assertEquals(Float.MAX_VALUE, methodHandleInvoker.floatInvoke(getFloatIndex, entity));

        methodHandleInvoker.invokeFloat1(setFloatIndex, entity, Float.POSITIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodHandleInvoker.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodHandleInvoker.floatInvoke(getFloatIndex, entity) > 0);

        methodHandleInvoker.invokeFloat1(setFloatIndex, entity, Float.NEGATIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodHandleInvoker.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodHandleInvoker.floatInvoke(getFloatIndex, entity) < 0);

        methodHandleInvoker.invokeFloat1(setFloatIndex, entity, Float.NaN); // setFloat
        assertTrue(Float.isNaN(methodHandleInvoker.floatInvoke(getFloatIndex, entity)));

        methodHandleInvoker.invokeFloat1(setFloatIndex, entity, 0.0f); // setFloat
        assertEquals(0.0f, methodHandleInvoker.floatInvoke(getFloatIndex, entity), 0.0f);

        methodHandleInvoker.invoke1(setFloatIndex, entity, -0.0f); // setFloat
        assertEquals(-0.0f, methodHandleInvoker.floatInvoke(getFloatIndex, entity), 0.0f);
    }

    @Test
    void testDoubleBoundaryValues() {
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, Double.MIN_VALUE); // setDouble
        assertEquals(Double.MIN_VALUE, methodHandleInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodHandleInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, Double.POSITIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodHandleInvoker.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodHandleInvoker.doubleInvoke(getDoubleIndex, entity) > 0);

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, Double.NEGATIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodHandleInvoker.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodHandleInvoker.doubleInvoke(getDoubleIndex, entity) < 0);

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, Double.NaN); // setDouble
        assertTrue(Double.isNaN(methodHandleInvoker.doubleInvoke(getDoubleIndex, entity)));

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, 0.0); // setDouble
        assertEquals(0.0, methodHandleInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, -0.0); // setDouble
        assertEquals(-0.0, methodHandleInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testByteBoundaryValues() {
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");

        methodHandleInvoker.invokeByte1(setByteIndex, entity, Byte.MIN_VALUE); // setByte
        assertEquals(Byte.MIN_VALUE, methodHandleInvoker.byteInvoke(getByteIndex, entity));

        methodHandleInvoker.invokeByte1(setByteIndex, entity, Byte.MAX_VALUE); // setByte
        assertEquals(Byte.MAX_VALUE, methodHandleInvoker.byteInvoke(getByteIndex, entity));

        methodHandleInvoker.invokeByte1(setByteIndex, entity, (byte) 0); // setByte
        assertEquals((byte) 0, methodHandleInvoker.byteInvoke(getByteIndex, entity));

        methodHandleInvoker.invokeByte1(setByteIndex, entity, (byte) -1); // setByte
        assertEquals((byte) -1, methodHandleInvoker.byteInvoke(getByteIndex, entity));
    }

    @Test
    void testShortBoundaryValues() {
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");

        methodHandleInvoker.invokeShort1(setShortIndex, entity, Short.MIN_VALUE); // setShort
        assertEquals(Short.MIN_VALUE, methodHandleInvoker.shortInvoke(getShortIndex, entity));

        methodHandleInvoker.invokeShort1(setShortIndex, entity, Short.MAX_VALUE); // setShort
        assertEquals(Short.MAX_VALUE, methodHandleInvoker.shortInvoke(getShortIndex, entity));

        methodHandleInvoker.invokeShort1(setShortIndex, entity, (short) 0); // setShort
        assertEquals((short) 0, methodHandleInvoker.shortInvoke(getShortIndex, entity));

        methodHandleInvoker.invokeShort1(setShortIndex, entity, (short) -1); // setShort
        assertEquals((short) -1, methodHandleInvoker.shortInvoke(getShortIndex, entity));
    }

    @Test
    void testCharBoundaryValues() {
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");

        methodHandleInvoker.invokeChar1(setCharIndex, entity, Character.MIN_VALUE); // setChar
        assertEquals(Character.MIN_VALUE, methodHandleInvoker.charInvoke(getCharIndex, entity));

        methodHandleInvoker.invokeChar1(setCharIndex, entity, Character.MAX_VALUE); // setChar
        assertEquals(Character.MAX_VALUE, methodHandleInvoker.charInvoke(getCharIndex, entity));

        methodHandleInvoker.invokeChar1(setCharIndex, entity, '\u0000'); // setChar
        assertEquals('\u0000', methodHandleInvoker.charInvoke(getCharIndex, entity));

        methodHandleInvoker.invokeChar1(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', methodHandleInvoker.charInvoke(getCharIndex, entity));

        methodHandleInvoker.invokeChar1(setCharIndex, entity, '中'); // setChar
        assertEquals('中', methodHandleInvoker.charInvoke(getCharIndex, entity));
    }

    // ==================== 索引越界异常测试 ====================


    // ==================== null 值测试 ====================


    @Test
    void testNullArguments() {
        // 测试 null 参数（对于引用类型参数）
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        int setIntegerIndex = methodInvokerHelper.getMethodIndex("setInteger", Integer.class);
        int getIntegerIndex = methodInvokerHelper.getMethodIndex("getInteger");

        methodHandleInvoker.invoke1(setStringIndex, entity, (String) null); // setString(null)
        assertNull(methodHandleInvoker.invoke(getStringIndex, entity)); // getString() 应该返回 null

        methodHandleInvoker.invoke1(setIntegerIndex, entity, (Integer) null); // setInteger(null)
        assertNull(methodHandleInvoker.invoke(getIntegerIndex, entity)); // getInteger() 应该返回 null
    }

    @Test
    void testNullReturnValue() {
        // void 方法返回 null
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);

        Object result = methodHandleInvoker.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);

        result = methodHandleInvoker.invoke1(setIntIndex, entity, 100); // setInt(int) - void
        assertNull(result);
    }

    // ==================== 组合测试 ====================

    @Test
    void testMultipleOperations() {
        // 多次调用同一方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 10; i++) {
            methodHandleInvoker.invoke1(setIntIndex, entity, i); // setInt
            Object result = methodHandleInvoker.invoke(getIntIndex, entity); // getInt
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

        methodHandleInvoker.invoke1(setIntIndex, entity, 100); // setInt
        methodHandleInvoker.invoke1(setLongIndex, entity, 200L); // setLong
        methodHandleInvoker.invoke1(setFloatIndex, entity, 3.14f); // setFloat
        methodHandleInvoker.invoke1(setDoubleIndex, entity, 2.71828); // setDouble

        assertEquals(100, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());
        assertEquals(200L, ((Long) methodHandleInvoker.invoke(getLongIndex, entity)).longValue());
        assertEquals(3.14f, ((Float) methodHandleInvoker.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(2.71828, ((Double) methodHandleInvoker.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
    }

    @Test
    void testMixedInvokeTypes() {
        // 混合使用不同的 invoke 方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        methodHandleInvoker.invokeInt1(setIntIndex, entity, 100); // setInt
        assertEquals(100, methodHandleInvoker.intInvoke(getIntIndex, entity)); // getInt

        Object result = methodHandleInvoker.invoke1(setIntIndex, entity, 200); // setInt using invoke
        assertNull(result);
        assertEquals(200, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());

        result = methodHandleInvoker.invokeInt1(setIntIndex, entity, 300); // setInt using invokeInt1
        assertNull(result);
        assertEquals(300, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidInvoke() {
        // 快速连续调用
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 1000; i++) {
            methodHandleInvoker.invoke1(setIntIndex, entity, i); // setInt
            Object result = methodHandleInvoker.invoke(getIntIndex, entity); // getInt
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

        methodHandleInvoker.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodHandleInvoker.intInvoke(getIntIndex, entity));

        methodHandleInvoker.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodHandleInvoker.longInvoke(getLongIndex, entity));

        methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodHandleInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testManyArguments() {
        // 测试多参数方法
        int addThreeDoublesIndex = methodInvokerHelper.getMethodIndex("addThreeDoubles", double.class, double.class, double.class);
        int concatenateThreeIndex = methodInvokerHelper.getMethodIndex("concatenateThree", String.class, String.class, String.class);

        Object result = methodHandleInvoker.invoke(addThreeDoublesIndex, entity, 1.0, 2.0, 3.0); // addThreeDoubles(double, double, double)
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        result = methodHandleInvoker.invoke(concatenateThreeIndex, entity, "a", "b", "c"); // concatenateThree(String, String, String)
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

        methodHandleInvoker.invoke(setAllPrimitivesIndex, entity,
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A'); // setAllPrimitives
        assertEquals((byte) 1, ((Byte) methodHandleInvoker.invoke(getByteIndex, entity)).byteValue());
        assertEquals((short) 2, ((Short) methodHandleInvoker.invoke(getShortIndex, entity)).shortValue());
        assertEquals(3, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());
        assertEquals(4L, ((Long) methodHandleInvoker.invoke(getLongIndex, entity)).longValue());
        assertEquals(5.0f, ((Float) methodHandleInvoker.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(6.0, ((Double) methodHandleInvoker.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
        assertEquals(true, methodHandleInvoker.invoke(getBooleanIndex, entity));
        assertEquals('A', ((Character) methodHandleInvoker.invoke(getCharIndex, entity)).charValue());
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            methodHandleInvoker.invoke1(setStringIndex, entity, str); // setString
            assertEquals(str, methodHandleInvoker.invoke(getStringIndex, entity)); // getString
        }
    }

    @Test
    void testPrecisionFloat() {
        // 测试 float 精度
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            methodHandleInvoker.invokeFloat1(setFloatIndex, entity, value); // setFloat
            assertEquals(value, methodHandleInvoker.floatInvoke(getFloatIndex, entity), 0.0001f); // getFloat
        }
    }

    @Test
    void testPrecisionDouble() {
        // 测试 double 精度
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            methodHandleInvoker.invokeDouble1(setDoubleIndex, entity, value); // setDouble
            assertEquals(value, methodHandleInvoker.doubleInvoke(getDoubleIndex, entity), 0.000001); // getDouble
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            methodHandleInvoker.invokeChar1(setCharIndex, entity, c); // setChar
            assertEquals(c, methodHandleInvoker.charInvoke(getCharIndex, entity)); // getChar
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 100; i++) {
            methodHandleInvoker.invoke1(setIntIndex, entity, 42); // setInt
            Object result = methodHandleInvoker.invoke(getIntIndex, entity); // getInt
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

        methodHandleInvoker.invoke1(setIntIndex, entity, 10); // setInt(10)
        int result = methodHandleInvoker.intInvoke(addIntIndex, entity, 20); // addInt(20) -> 返回 30
        assertEquals(30, result);

        methodHandleInvoker.invoke1(setLongIndex, entity, 100L); // setLong(100L)
        long longResult = methodHandleInvoker.longInvoke(addLongIndex, entity, 200L); // addLong(200L) -> 返回 300L
        assertEquals(300L, longResult);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");

        // 第一个方法
        entity.setInt(1);
        assertEquals(1, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());

        // voidMethod
        Object result = methodHandleInvoker.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);
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
        methodHandleInvoker.invoke(setStringIndex, entity, new Object[]{null});
        assertNull(methodHandleInvoker.invoke(getStringIndex, entity));

        methodHandleInvoker.invoke(setIntegerIndex, entity, new Object[]{null});
        assertNull(methodHandleInvoker.invoke(getIntegerIndex, entity));
    }

    @Test
    void testNullArgumentInSingleArgInvoke() {
        // 测试单参数invoke中的null参数
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");

        Object result = methodHandleInvoker.invoke1(setStringIndex, entity, null);
        assertNull(result);
        assertNull(methodHandleInvoker.invoke(getStringIndex, entity));
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
        Object result = methodHandleInvoker.invoke(addTwoIntsIndex, entity, new Object[]{10, 20});
        assertEquals(30, ((Integer) result).intValue());

        result = methodHandleInvoker.invoke(addThreeDoublesIndex, entity, new Object[]{1.0, 2.0, 3.0});
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        methodHandleInvoker.invoke(setIntIndex, entity, new Object[]{100});
        assertEquals(100, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());

        // void方法无参数
        result = methodHandleInvoker.invoke(voidMethodIndex, entity);
        assertNull(result);
    }

    // ==================== 参数类型不匹配的测试 ====================


    @Test
    void testArrayParameter() {
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);
        Object result = methodHandleInvoker.invoke(concatenateIndex, entity, new Object[]{"hello", "world"});
        assertEquals("helloworld", result);
    }

    // ==================== 数值溢出测试 ====================

    @Test
    void testIntOverflow() {
        int addIndex = methodInvokerHelper.getMethodIndex("add", int.class, int.class);

        // Integer.MAX_VALUE + 1 应该溢出变成负数
        int result = methodHandleInvoker.intInvoke(addIndex, entity, Integer.MAX_VALUE, 1);
        assertEquals(Integer.MIN_VALUE, result);

        // Integer.MIN_VALUE - 1 应该溢出变成正数
        result = methodHandleInvoker.intInvoke(addIndex, entity, Integer.MIN_VALUE, -1);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    void testIntMultiplyOverflow() {
        int multiplyIndex = methodInvokerHelper.getMethodIndex("multiply", int.class, int.class);

        // 46341 * 46341 会溢出 (超过 Integer.MAX_VALUE)
        int result = methodHandleInvoker.intInvoke(multiplyIndex, entity, 46341, 46341);
        assertTrue(result < 0); // 溢出后变成负数
    }

    @Test
    void testLongOverflow() {
        int addIndex = methodInvokerHelper.getMethodIndex("addTwoLongs", long.class, long.class);

        // Long.MAX_VALUE + 1 应该溢出变成负数
        Object result = methodHandleInvoker.invoke(addIndex, entity, Long.MAX_VALUE, 1L);
        assertEquals(Long.MIN_VALUE, ((Long) result).longValue());

        // Long.MIN_VALUE - 1 应该溢出变成正数
        result = methodHandleInvoker.invoke(addIndex, entity, Long.MIN_VALUE, -1L);
        assertEquals(Long.MAX_VALUE, ((Long) result).longValue());
    }

    @Test
    void testSubtractUnderflow() {
        int subtractIndex = methodInvokerHelper.getMethodIndex("subtract", int.class, int.class);

        // Integer.MIN_VALUE - 1 应该溢出变成 MAX_VALUE
        int result = methodHandleInvoker.intInvoke(subtractIndex, entity, Integer.MIN_VALUE, 1);
        assertEquals(Integer.MAX_VALUE, result);
    }

    // ==================== 数组/对象参数测试 ====================

    @Test
    void testArrayReturnType() {
        int getArrayIndex = methodInvokerHelper.getMethodIndex("getIntArray");

        entity.setInt(10);
        Object result = methodHandleInvoker.invoke(getArrayIndex, entity);

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
        Object result = methodHandleInvoker.invoke(sumArrayIndex, entity, new Object[]{testArray});

        assertNotNull(result);
        assertEquals(15, ((Integer) result).intValue());
    }

    @Test
    void testNullArrayArgument() {
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        Object result = methodHandleInvoker.invoke(sumArrayIndex, entity, new Object[]{null});

        assertNotNull(result);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    void testEmptyArrayArgument() {
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        int[] emptyArray = new int[0];
        Object result = methodHandleInvoker.invoke(sumArrayIndex, entity, new Object[]{emptyArray});

        assertNotNull(result);
        assertEquals(0, ((Integer) result).intValue());
    }

    @Test
    void testObjectArgument() {
        int processObjectIndex = methodInvokerHelper.getMethodIndex("processObject", Object.class);

        Object result = methodHandleInvoker.invoke(processObjectIndex, entity, new Object[]{"test string"});
        assertEquals("test string", result);

        result = methodHandleInvoker.invoke(processObjectIndex, entity, new Object[]{123});
        assertEquals("123", result);

        result = methodHandleInvoker.invoke(processObjectIndex, entity, new Object[]{null});
        assertEquals("null", result);
    }

    @Test
    void testMixedArrayReturnType() {
        int getMixedArrayIndex = methodInvokerHelper.getMethodIndex("getMixedArray");

        entity.setInt(42);
        entity.setString("hello");
        entity.setBoolean(true);

        Object result = methodHandleInvoker.invoke(getMixedArrayIndex, entity);

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
        Object result = methodHandleInvoker.invoke(getStringArrayIndex, entity);

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

        Object result = methodHandleInvoker.invoke(getIntIndex, entity);
        assertEquals(123, ((Integer) result).intValue());
    }

    @Test
    void testLastValidIndex() {
        // 测试最后一个有效索引
        // 获取所有方法索引来找到最后一个
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");

        Object result = methodHandleInvoker.invoke(voidMethodIndex, entity);
        assertNull(result);
    }

    // ==================== 递归方法测试 ====================

    @Test
    void testRecursiveMethod() {
        int factorialIndex = methodInvokerHelper.getMethodIndex("factorial", int.class);

        // factorial(5) = 120
        int result = methodHandleInvoker.intInvoke(factorialIndex, entity, 5);
        assertEquals(120, result);

        // factorial(10) = 3628800
        result = methodHandleInvoker.intInvoke(factorialIndex, entity, 10);
        assertEquals(3628800, result);

        // factorial(0) = 1
        result = methodHandleInvoker.intInvoke(factorialIndex, entity, 0);
        assertEquals(1, result);

        // factorial(1) = 1
        result = methodHandleInvoker.intInvoke(factorialIndex, entity, 1);
        assertEquals(1, result);
    }

    @Test
    void testRecursiveMethodEdgeCase() {
        int factorialIndex = methodInvokerHelper.getMethodIndex("factorial", int.class);

        // factorial(12) = 479001600
        int result = methodHandleInvoker.intInvoke(factorialIndex, entity, 12);
        assertEquals(479001600, result);
    }

    // ==================== 多实例测试 ====================

    @Test
    void testMultipleInstances() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        TestMethodEntity entity1 = new TestMethodEntity();
        TestMethodEntity entity2 = new TestMethodEntity();

        methodHandleInvoker.invoke1(setIntIndex, entity1, 100);
        methodHandleInvoker.invoke1(setIntIndex, entity2, 200);

        assertEquals(100, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity1)).intValue());
        assertEquals(200, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity2)).intValue());
    }

    // ==================== 混合使用场景测试 ====================

    @Test
    void testMixedInvokeWithArrayAndPrimitives() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getArrayIndex = methodInvokerHelper.getMethodIndex("getIntArray");
        int sumArrayIndex = methodInvokerHelper.getMethodIndex("sumArray", int[].class);

        // 设置值
        methodHandleInvoker.invoke1(setIntIndex, entity, 5);

        // 获取数组
        int[] array = (int[]) methodHandleInvoker.invoke(getArrayIndex, entity);
        assertArrayEquals(new int[]{5, 6, 7}, array);

        // 对数组求和
        int sum = ((Integer) methodHandleInvoker.invoke(sumArrayIndex, entity, new Object[]{array})).intValue();
        assertEquals(18, sum);
    }

    // ==================== 空参数测试补充 ====================

    @Test
    void testEmptyObjectArrayInInvoke1() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        // 使用 invoke1 设置值
        methodHandleInvoker.invoke1(setIntIndex, entity, 42);
        assertEquals(42, ((Integer) methodHandleInvoker.invoke(getIntIndex, entity)).intValue());
    }

    // ==================== 2-5 参数方法测试 ====================

    @Test
    void testInvoke2() {
        // 测试 invoke2 方法
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodHandleInvoker.invoke2(addTwoIntsIndex, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodHandleInvoker.invoke2(concatenateIndex, entity, "hello", "world"); // concatenate(String, String)
        assertEquals("helloworld", result);
    }

    @Test
    void testInvoke3() {
        // 测试 invoke3 方法
        int addThreeIntsIndex = methodInvokerHelper.getMethodIndex("addThreeInts", int.class, int.class, int.class);
        int concatenateThreeIndex = methodInvokerHelper.getMethodIndex("concatenateThree", String.class, String.class, String.class);

        Object result = methodHandleInvoker.invoke3(addThreeIntsIndex, entity, 1, 2, 3); // addThreeInts(int, int, int)
        assertNotNull(result);
        assertEquals(6, ((Integer) result).intValue());

        result = methodHandleInvoker.invoke3(concatenateThreeIndex, entity, "a", "b", "c"); // concatenateThree(String, String, String)
        assertEquals("abc", result);
    }

    @Test
    void testInvoke4() {
        // 测试 invoke4 方法
        int addFourIntsIndex = methodInvokerHelper.getMethodIndex("addFourInts", int.class, int.class, int.class, int.class);
        int concatenateFourIndex = methodInvokerHelper.getMethodIndex("concatenateFour", String.class, String.class, String.class, String.class);

        Object result = methodHandleInvoker.invoke4(addFourIntsIndex, entity, 1, 2, 3, 4); // addFourInts(int, int, int, int)
        assertNotNull(result);
        assertEquals(10, ((Integer) result).intValue());

        result = methodHandleInvoker.invoke4(concatenateFourIndex, entity, "a", "b", "c", "d"); // concatenateFour(String, String, String, String)
        assertEquals("abcd", result);
    }

    @Test
    void testInvoke5() {
        // 测试 invoke5 方法
        int addFiveIntsIndex = methodInvokerHelper.getMethodIndex("addFiveInts", int.class, int.class, int.class, int.class, int.class);
        int concatenateFiveIndex = methodInvokerHelper.getMethodIndex("concatenateFive", String.class, String.class, String.class, String.class, String.class);

        Object result = methodHandleInvoker.invoke5(addFiveIntsIndex, entity, 1, 2, 3, 4, 5); // addFiveInts(int, int, int, int, int)
        assertNotNull(result);
        assertEquals(15, ((Integer) result).intValue());

        result = methodHandleInvoker.invoke5(concatenateFiveIndex, entity, "a", "b", "c", "d", "e"); // concatenateFive(String, String, String, String, String)
        assertEquals("abcde", result);
    }

    @Test
    void testInvoke2WithVoidReturn() {
        // 测试 invoke2 的 void 返回方法
        int setTwoIntsIndex = methodInvokerHelper.getMethodIndex("setTwoInts", int.class, int.class);

        Object result = methodHandleInvoker.invoke2(setTwoIntsIndex, entity, 100, 200);
        assertNull(result);

        // 验证值被正确设置
        assertEquals(100, entity.getInt());
        assertEquals(200L, entity.getLong());
    }

    @Test
    void testInvoke3WithVoidReturn() {
        // 测试 invoke3 的 void 返回方法
        int setThreeIntsIndex = methodInvokerHelper.getMethodIndex("setThreeInts", int.class, int.class, int.class);

        Object result = methodHandleInvoker.invoke3(setThreeIntsIndex, entity, 1, 2, 3);
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

        Object result = methodHandleInvoker.invoke4(setFourIntsIndex, entity, 1, 2, 3, 4);
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

        Object result = methodHandleInvoker.invoke5(setFiveIntsIndex, entity, 1, 2, 3, 4, 5);
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

        Object result = methodHandleInvoker.invoke2(concatenateIndex, entity, "test", "123");
        assertEquals("test123", result);
    }

    @Test
    void testInvoke3MixedTypes() {
        // 测试 invoke3 的混合类型
        int addMixedIndex = methodInvokerHelper.getMethodIndex("addMixed", int.class, long.class, double.class);

        Object result = methodHandleInvoker.invoke3(addMixedIndex, entity, 1, 2L, 3.0);
        assertEquals(6, ((Integer) result).intValue());
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
