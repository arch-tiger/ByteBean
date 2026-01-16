package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.core.invoker.MethodInvokerHelper;
import com.github.archtiger.bytebean.api.method.MethodInvoker;
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
    private MethodInvoker methodInvoker;
    private TestMethodEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        methodInvokerHelper = MethodInvokerHelper.of(TestMethodEntity.class);
        methodInvoker = methodInvokerHelper;
        entity = new TestMethodEntity();
    }

    // ==================== 通用 invoke 方法测试 ====================

    @Test
    void testInvokeNoArgs() {
        // 测试无参数方法
        entity.setInt(42);
        int result = methodInvokerHelper.intInvoke(entity, "getInt"); // getInt()
        assertEquals(42, result);

        int index_2 = methodInvokerHelper.getMethodIndex("getString");
        entity.setString("test");
        String result_2 = (String) methodInvoker.invoke(index_2, entity); // getString()
        assertEquals("test", result_2);
    }

    @Test
    void testInvokeVoidMethod() {
        int index = methodInvokerHelper.getMethodIndex("voidMethod");
        // 测试 void 方法
        Object result = methodInvoker.invoke(index, entity); // voidMethod()

        assertNull(result);
    }

    @Test
    void testInvokeSingleArg() {
        // 测试单参数方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        Object result = methodInvoker.invoke(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodInvoker.invoke(getIntIndex, entity); // getInt()
        assertEquals(100, ((Integer) result).intValue());
    }

    @Test
    void testInvokeMultipleArgs() {
        // 测试多参数方法
        int addTwoIntsIndex = methodInvokerHelper.getMethodIndex("addTwoInts", int.class, int.class);
        int concatenateIndex = methodInvokerHelper.getMethodIndex("concatenate", String.class, String.class);

        Object result = methodInvoker.invoke(addTwoIntsIndex, entity, 10, 20); // addTwoInts(int, int)
        assertNotNull(result);
        assertEquals(30, ((Integer) result).intValue());

        result = methodInvoker.invoke(concatenateIndex, entity, "hello", "world"); // concatenate(String, String)
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

        methodInvoker.invoke(setIntIndex, entity, 100); // setInt
        assertEquals(100, ((Integer) methodInvoker.invoke(getIntIndex, entity)).intValue());

        methodInvoker.invoke(setLongIndex, entity, 200L); // setLong
        assertEquals(200L, ((Long) methodInvoker.invoke(getLongIndex, entity)).longValue());

        methodInvoker.invoke(setFloatIndex, entity, 3.14f); // setFloat
        assertEquals(3.14f, ((Float) methodInvoker.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);

        methodInvoker.invoke(setDoubleIndex, entity, 2.71828); // setDouble
        assertEquals(2.71828, ((Double) methodInvoker.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);

        methodInvoker.invoke(setBooleanIndex, entity, true); // setBoolean
        assertEquals(true, methodInvoker.invoke(getBooleanIndex, entity));

        methodInvoker.invoke(setByteIndex, entity, (byte) 42); // setByte
        assertEquals((byte) 42, ((Byte) methodInvoker.invoke(getByteIndex, entity)).byteValue());

        methodInvoker.invoke(setShortIndex, entity, (short) 123); // setShort
        assertEquals((short) 123, ((Short) methodInvoker.invoke(getShortIndex, entity)).shortValue());

        methodInvoker.invoke(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', ((Character) methodInvoker.invoke(getCharIndex, entity)).charValue());

        methodInvoker.invoke(setStringIndex, entity, "test"); // setString
        assertEquals("test", methodInvoker.invoke(getStringIndex, entity));
    }

    // ==================== 基本类型返回方法测试 ====================

    @Test
    void testIntInvoke() {
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        entity.setInt(100);
        int result = methodInvoker.intInvoke(getIntIndex, entity); // getInt()
        assertEquals(100, result);

        result = methodInvoker.intInvoke(addIntIndex, entity, 50); // addInt(int)
        assertEquals(150, result);
    }

    @Test
    void testLongInvoke() {
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        entity.setLong(1000L);
        long result = methodInvoker.longInvoke(getLongIndex, entity); // getLong()
        assertEquals(1000L, result);

        result = methodInvoker.longInvoke(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, result);
    }

    @Test
    void testFloatInvoke() {
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        int addFloatIndex = methodInvokerHelper.getMethodIndex("addFloat", float.class);

        entity.setFloat(3.14f);
        float result = methodInvoker.floatInvoke(getFloatIndex, entity); // getFloat()
        assertEquals(3.14f, result, 0.0001f);

        result = methodInvoker.floatInvoke(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, result, 0.0001f);
    }

    @Test
    void testDoubleInvoke() {
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        int addDoubleIndex = methodInvokerHelper.getMethodIndex("addDouble", double.class);

        entity.setDouble(2.71828);
        double result = methodInvoker.doubleInvoke(getDoubleIndex, entity); // getDouble()
        assertEquals(2.71828, result, 0.000001);

        result = methodInvoker.doubleInvoke(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, result, 0.000001);
    }

    @Test
    void testBooleanInvoke() {
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");
        int isEvenIndex = methodInvokerHelper.getMethodIndex("isEven", int.class);

        entity.setBoolean(true);
        boolean result = methodInvoker.booleanInvoke(getBooleanIndex, entity); // getBoolean()
        assertTrue(result);

        result = methodInvoker.booleanInvoke(isEvenIndex, entity, 4); // isEven(int) - 4 是偶数
        assertTrue(result);

        result = methodInvoker.booleanInvoke(isEvenIndex, entity, 5); // isEven(int) - 5 是奇数
        assertFalse(result);
    }

    @Test
    void testByteInvoke() {
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");
        int incrementByteIndex = methodInvokerHelper.getMethodIndex("incrementByte", byte.class);

        entity.setByte((byte) 42);
        byte result = methodInvoker.byteInvoke(getByteIndex, entity); // getByte()
        assertEquals((byte) 42, result);

        result = methodInvoker.byteInvoke(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, result);
    }

    @Test
    void testShortInvoke() {
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");
        int incrementShortIndex = methodInvokerHelper.getMethodIndex("incrementShort", short.class);

        entity.setShort((short) 123);
        short result = methodInvoker.shortInvoke(getShortIndex, entity); // getShort()
        assertEquals((short) 123, result);

        result = methodInvoker.shortInvoke(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, result);
    }

    @Test
    void testCharInvoke() {
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        int nextCharIndex = methodInvokerHelper.getMethodIndex("nextChar", char.class);

        entity.setChar('A');
        char result = methodInvoker.charInvoke(getCharIndex, entity); // getChar()
        assertEquals('A', result);

        result = methodInvoker.charInvoke(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', result);
    }

    // ==================== 单参数基本类型方法测试 ====================

    @Test
    void testInvokeInt1() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int addIntIndex = methodInvokerHelper.getMethodIndex("addInt", int.class);

        Object result = methodInvoker.invokeInt1(setIntIndex, entity, 100); // setInt(int)
        assertNull(result); // void 方法返回 null

        result = methodInvoker.invokeInt1(addIntIndex, entity, 50); // addInt(int)
        assertNotNull(result);
        assertEquals(150, ((Integer) result).intValue()); // entity.intValue 初始为 0
    }

    @Test
    void testInvokeLong1() {
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int addLongIndex = methodInvokerHelper.getMethodIndex("addLong", long.class);

        Object result = methodInvoker.invokeLong1(setLongIndex, entity, 1000L); // setLong(long)
        assertNull(result);

        result = methodInvoker.invokeLong1(addLongIndex, entity, 500L); // addLong(long)
        assertEquals(1500L, ((Long) result).longValue());
    }

    @Test
    void testInvokeFloat1() {
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int addFloatIndex = methodInvokerHelper.getMethodIndex("addFloat", float.class);

        Object result = methodInvoker.invokeFloat1(setFloatIndex, entity, 3.14f); // setFloat(float)
        assertNull(result);

        result = methodInvoker.invokeFloat1(addFloatIndex, entity, 2.5f); // addFloat(float)
        assertEquals(5.64f, ((Float) result).floatValue(), 0.0001f);
    }

    @Test
    void testInvokeDouble1() {
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int addDoubleIndex = methodInvokerHelper.getMethodIndex("addDouble", double.class);

        Object result = methodInvoker.invokeDouble1(setDoubleIndex, entity, 2.71828); // setDouble(double)
        assertNull(result);

        result = methodInvoker.invokeDouble1(addDoubleIndex, entity, 1.5); // addDouble(double)
        assertEquals(4.21828, ((Double) result).doubleValue(), 0.000001);
    }

    @Test
    void testInvokeBoolean1() {
        int setBooleanIndex = methodInvokerHelper.getMethodIndex("setBoolean", boolean.class);
        int getBooleanIndex = methodInvokerHelper.getMethodIndex("getBoolean");

        Object result = methodInvoker.invokeBoolean1(setBooleanIndex, entity, true); // setBoolean(boolean)
        assertNull(result);

        // 注意：isEven 的参数是 int，不是 boolean，所以 invokeBoolean1 不会匹配到它
        // 这里只测试 setBoolean 方法
        result = methodInvoker.invoke(getBooleanIndex, entity); // getBoolean
        assertEquals(true, result);
    }

    @Test
    void testInvokeByte1() {
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int incrementByteIndex = methodInvokerHelper.getMethodIndex("incrementByte", byte.class);

        Object result = methodInvoker.invokeByte1(setByteIndex, entity, (byte) 42); // setByte(byte)
        assertNull(result);

        result = methodInvoker.invokeByte1(incrementByteIndex, entity, (byte) 10); // incrementByte(byte)
        assertEquals((byte) 11, ((Byte) result).byteValue());
    }

    @Test
    void testInvokeShort1() {
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int incrementShortIndex = methodInvokerHelper.getMethodIndex("incrementShort", short.class);

        Object result = methodInvoker.invokeShort1(setShortIndex, entity, (short) 123); // setShort(short)
        assertNull(result);

        result = methodInvoker.invokeShort1(incrementShortIndex, entity, (short) 50); // incrementShort(short)
        assertEquals((short) 51, ((Short) result).shortValue());
    }

    @Test
    void testInvokeChar1() {
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int nextCharIndex = methodInvokerHelper.getMethodIndex("nextChar", char.class);

        Object result = methodInvoker.invokeChar1(setCharIndex, entity, 'A'); // setChar(char)
        assertNull(result);

        result = methodInvoker.invokeChar1(nextCharIndex, entity, 'A'); // nextChar(char)
        assertEquals('B', ((Character) result).charValue());
    }

    // ==================== 边界值测试 ====================

    @Test
    void testIntBoundaryValues() {
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        methodInvoker.invokeInt1(setIntIndex, entity, Integer.MIN_VALUE); // setInt
        assertEquals(Integer.MIN_VALUE, methodInvoker.intInvoke(getIntIndex, entity));

        methodInvoker.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodInvoker.intInvoke(getIntIndex, entity));

        methodInvoker.invokeInt1(setIntIndex, entity, 0); // setInt
        assertEquals(0, methodInvoker.intInvoke(getIntIndex, entity));

        methodInvoker.invokeInt1(setIntIndex, entity, -1); // setInt
        assertEquals(-1, methodInvoker.intInvoke(getIntIndex, entity));
    }

    @Test
    void testLongBoundaryValues() {
        int setLongIndex = methodInvokerHelper.getMethodIndex("setLong", long.class);
        int getLongIndex = methodInvokerHelper.getMethodIndex("getLong");

        methodInvoker.invokeLong1(setLongIndex, entity, Long.MIN_VALUE); // setLong
        assertEquals(Long.MIN_VALUE, methodInvoker.longInvoke(getLongIndex, entity));

        methodInvoker.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodInvoker.longInvoke(getLongIndex, entity));

        methodInvoker.invokeLong1(setLongIndex, entity, 0L); // setLong
        assertEquals(0L, methodInvoker.longInvoke(getLongIndex, entity));

        methodInvoker.invokeLong1(setLongIndex, entity, -1L); // setLong
        assertEquals(-1L, methodInvoker.longInvoke(getLongIndex, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");

        methodInvoker.invokeFloat1(setFloatIndex, entity, Float.MIN_VALUE); // setFloat
        assertEquals(Float.MIN_VALUE, methodInvoker.floatInvoke(getFloatIndex, entity));

        methodInvoker.invokeFloat1(setFloatIndex, entity, Float.MAX_VALUE); // setFloat
        assertEquals(Float.MAX_VALUE, methodInvoker.floatInvoke(getFloatIndex, entity));

        methodInvoker.invokeFloat1(setFloatIndex, entity, Float.POSITIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodInvoker.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodInvoker.floatInvoke(getFloatIndex, entity) > 0);

        methodInvoker.invokeFloat1(setFloatIndex, entity, Float.NEGATIVE_INFINITY); // setFloat
        assertTrue(Float.isInfinite(methodInvoker.floatInvoke(getFloatIndex, entity)));
        assertTrue(methodInvoker.floatInvoke(getFloatIndex, entity) < 0);

        methodInvoker.invokeFloat1(setFloatIndex, entity, Float.NaN); // setFloat
        assertTrue(Float.isNaN(methodInvoker.floatInvoke(getFloatIndex, entity)));

        methodInvoker.invokeFloat1(setFloatIndex, entity, 0.0f); // setFloat
        assertEquals(0.0f, methodInvoker.floatInvoke(getFloatIndex, entity), 0.0f);

        methodInvoker.invoke(setFloatIndex, entity, -0.0f); // setFloat
        assertEquals(-0.0f, methodInvoker.floatInvoke(getFloatIndex, entity), 0.0f);
    }

    @Test
    void testDoubleBoundaryValues() {
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");

        methodInvoker.invokeDouble1(setDoubleIndex, entity, Double.MIN_VALUE); // setDouble
        assertEquals(Double.MIN_VALUE, methodInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodInvoker.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodInvoker.invokeDouble1(setDoubleIndex, entity, Double.POSITIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodInvoker.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodInvoker.doubleInvoke(getDoubleIndex, entity) > 0);

        methodInvoker.invokeDouble1(setDoubleIndex, entity, Double.NEGATIVE_INFINITY); // setDouble
        assertTrue(Double.isInfinite(methodInvoker.doubleInvoke(getDoubleIndex, entity)));
        assertTrue(methodInvoker.doubleInvoke(getDoubleIndex, entity) < 0);

        methodInvoker.invokeDouble1(setDoubleIndex, entity, Double.NaN); // setDouble
        assertTrue(Double.isNaN(methodInvoker.doubleInvoke(getDoubleIndex, entity)));

        methodInvoker.invokeDouble1(setDoubleIndex, entity, 0.0); // setDouble
        assertEquals(0.0, methodInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);

        methodInvoker.invokeDouble1(setDoubleIndex, entity, -0.0); // setDouble
        assertEquals(-0.0, methodInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testByteBoundaryValues() {
        int setByteIndex = methodInvokerHelper.getMethodIndex("setByte", byte.class);
        int getByteIndex = methodInvokerHelper.getMethodIndex("getByte");

        methodInvoker.invokeByte1(setByteIndex, entity, Byte.MIN_VALUE); // setByte
        assertEquals(Byte.MIN_VALUE, methodInvoker.byteInvoke(getByteIndex, entity));

        methodInvoker.invokeByte1(setByteIndex, entity, Byte.MAX_VALUE); // setByte
        assertEquals(Byte.MAX_VALUE, methodInvoker.byteInvoke(getByteIndex, entity));

        methodInvoker.invokeByte1(setByteIndex, entity, (byte) 0); // setByte
        assertEquals((byte) 0, methodInvoker.byteInvoke(getByteIndex, entity));

        methodInvoker.invokeByte1(setByteIndex, entity, (byte) -1); // setByte
        assertEquals((byte) -1, methodInvoker.byteInvoke(getByteIndex, entity));
    }

    @Test
    void testShortBoundaryValues() {
        int setShortIndex = methodInvokerHelper.getMethodIndex("setShort", short.class);
        int getShortIndex = methodInvokerHelper.getMethodIndex("getShort");

        methodInvoker.invokeShort1(setShortIndex, entity, Short.MIN_VALUE); // setShort
        assertEquals(Short.MIN_VALUE, methodInvoker.shortInvoke(getShortIndex, entity));

        methodInvoker.invokeShort1(setShortIndex, entity, Short.MAX_VALUE); // setShort
        assertEquals(Short.MAX_VALUE, methodInvoker.shortInvoke(getShortIndex, entity));

        methodInvoker.invokeShort1(setShortIndex, entity, (short) 0); // setShort
        assertEquals((short) 0, methodInvoker.shortInvoke(getShortIndex, entity));

        methodInvoker.invokeShort1(setShortIndex, entity, (short) -1); // setShort
        assertEquals((short) -1, methodInvoker.shortInvoke(getShortIndex, entity));
    }

    @Test
    void testCharBoundaryValues() {
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");

        methodInvoker.invokeChar1(setCharIndex, entity, Character.MIN_VALUE); // setChar
        assertEquals(Character.MIN_VALUE, methodInvoker.charInvoke(getCharIndex, entity));

        methodInvoker.invokeChar1(setCharIndex, entity, Character.MAX_VALUE); // setChar
        assertEquals(Character.MAX_VALUE, methodInvoker.charInvoke(getCharIndex, entity));

        methodInvoker.invokeChar1(setCharIndex, entity, '\u0000'); // setChar
        assertEquals('\u0000', methodInvoker.charInvoke(getCharIndex, entity));

        methodInvoker.invokeChar1(setCharIndex, entity, 'A'); // setChar
        assertEquals('A', methodInvoker.charInvoke(getCharIndex, entity));

        methodInvoker.invokeChar1(setCharIndex, entity, '中'); // setChar
        assertEquals('中', methodInvoker.charInvoke(getCharIndex, entity));
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBoundsInvoke() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodInvoker.invoke(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvoker.invoke(1000, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvoker.invoke(Integer.MIN_VALUE, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvoker.invoke(Integer.MAX_VALUE, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsIntInvoke() {
        assertThrows(IllegalArgumentException.class, () -> {
            methodInvoker.intInvoke(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            methodInvoker.intInvoke(1000, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveInvokes() {
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.longInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.floatInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.doubleInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.booleanInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.byteInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.shortInvoke(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.charInvoke(-1, entity));

        assertThrows(IllegalArgumentException.class, () -> methodInvoker.longInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.floatInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.doubleInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.booleanInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.byteInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.shortInvoke(1000, entity));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.charInvoke(1000, entity));
    }

    @Test
    void testIndexOutOfBoundsPrimitive1Invokes() {
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeInt1(-1, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeLong1(-1, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeFloat1(-1, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeDouble1(-1, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeBoolean1(-1, entity, true));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeByte1(-1, entity, (byte) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeShort1(-1, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeChar1(-1, entity, 'A'));

        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeInt1(1000, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeLong1(1000, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeFloat1(1000, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeDouble1(1000, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeBoolean1(1000, entity, true));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeByte1(1000, entity, (byte) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeShort1(1000, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> methodInvoker.invokeChar1(1000, entity, 'A'));
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);

        assertThrows(NullPointerException.class, () -> {
            methodInvoker.invoke(getIntIndex, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvoker.invoke(setIntIndex, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvoker.intInvoke(getIntIndex, null);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvoker.invokeInt1(setIntIndex, null, 100);
        });

        assertThrows(NullPointerException.class, () -> {
            methodInvoker.invokeInt1(setIntIndex, null, 100);
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

        assertThrows(NullPointerException.class, () -> methodInvoker.longInvoke(getLongIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvoker.floatInvoke(getFloatIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvoker.doubleInvoke(getDoubleIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvoker.booleanInvoke(getBooleanIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvoker.byteInvoke(getByteIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvoker.shortInvoke(getShortIndex, null));
        assertThrows(NullPointerException.class, () -> methodInvoker.charInvoke(getCharIndex, null));
    }

    @Test
    void testNullArguments() {
        // 测试 null 参数（对于引用类型参数）
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        int setIntegerIndex = methodInvokerHelper.getMethodIndex("setInteger", Integer.class);
        int getIntegerIndex = methodInvokerHelper.getMethodIndex("getInteger");

        methodInvoker.invoke(setStringIndex, entity, (String) null); // setString(null)
        assertNull(methodInvoker.invoke(getStringIndex, entity)); // getString() 应该返回 null

        methodInvoker.invoke(setIntegerIndex, entity, (Integer) null); // setInteger(null)
        assertNull(methodInvoker.invoke(getIntegerIndex, entity)); // getInteger() 应该返回 null
    }

    @Test
    void testNullReturnValue() {
        // void 方法返回 null
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);

        Object result = methodInvoker.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);

        result = methodInvoker.invoke(setIntIndex, entity, 100); // setInt(int) - void
        assertNull(result);
    }

    // ==================== 组合测试 ====================

    @Test
    void testMultipleOperations() {
        // 多次调用同一方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 10; i++) {
            methodInvoker.invoke(setIntIndex, entity, i); // setInt
            Object result = methodInvoker.invoke(getIntIndex, entity); // getInt
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

        methodInvoker.invoke(setIntIndex, entity, 100); // setInt
        methodInvoker.invoke(setLongIndex, entity, 200L); // setLong
        methodInvoker.invoke(setFloatIndex, entity, 3.14f); // setFloat
        methodInvoker.invoke(setDoubleIndex, entity, 2.71828); // setDouble

        assertEquals(100, ((Integer) methodInvoker.invoke(getIntIndex, entity)).intValue());
        assertEquals(200L, ((Long) methodInvoker.invoke(getLongIndex, entity)).longValue());
        assertEquals(3.14f, ((Float) methodInvoker.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(2.71828, ((Double) methodInvoker.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
    }

    @Test
    void testMixedInvokeTypes() {
        // 混合使用不同的 invoke 方法
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        methodInvoker.invokeInt1(setIntIndex, entity, 100); // setInt
        assertEquals(100, methodInvoker.intInvoke(getIntIndex, entity)); // getInt

        Object result = methodInvoker.invoke(setIntIndex, entity, 200); // setInt using invoke
        assertNull(result);
        assertEquals(200, ((Integer) methodInvoker.invoke(getIntIndex, entity)).intValue());

        result = methodInvoker.invokeInt1(setIntIndex, entity, 300); // setInt using invokeInt1
        assertNull(result);
        assertEquals(300, ((Integer) methodInvoker.invoke(getIntIndex, entity)).intValue());
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidInvoke() {
        // 快速连续调用
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 1000; i++) {
            methodInvoker.invoke(setIntIndex, entity, i); // setInt
            Object result = methodInvoker.invoke(getIntIndex, entity); // getInt
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

        methodInvoker.invokeInt1(setIntIndex, entity, Integer.MAX_VALUE); // setInt
        assertEquals(Integer.MAX_VALUE, methodInvoker.intInvoke(getIntIndex, entity));

        methodInvoker.invokeLong1(setLongIndex, entity, Long.MAX_VALUE); // setLong
        assertEquals(Long.MAX_VALUE, methodInvoker.longInvoke(getLongIndex, entity));

        methodInvoker.invokeDouble1(setDoubleIndex, entity, Double.MAX_VALUE); // setDouble
        assertEquals(Double.MAX_VALUE, methodInvoker.doubleInvoke(getDoubleIndex, entity), 0.0);
    }

    @Test
    void testManyArguments() {
        // 测试多参数方法
        int addThreeDoublesIndex = methodInvokerHelper.getMethodIndex("addThreeDoubles", double.class, double.class, double.class);
        int concatenateThreeIndex = methodInvokerHelper.getMethodIndex("concatenateThree", String.class, String.class, String.class);

        Object result = methodInvoker.invoke(addThreeDoublesIndex, entity, 1.0, 2.0, 3.0); // addThreeDoubles(double, double, double)
        assertEquals(6.0, ((Double) result).doubleValue(), 0.000001);

        result = methodInvoker.invoke(concatenateThreeIndex, entity, "a", "b", "c"); // concatenateThree(String, String, String)
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

        methodInvoker.invoke(setAllPrimitivesIndex, entity,
                (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, true, 'A'); // setAllPrimitives
        assertEquals((byte) 1, ((Byte) methodInvoker.invoke(getByteIndex, entity)).byteValue());
        assertEquals((short) 2, ((Short) methodInvoker.invoke(getShortIndex, entity)).shortValue());
        assertEquals(3, ((Integer) methodInvoker.invoke(getIntIndex, entity)).intValue());
        assertEquals(4L, ((Long) methodInvoker.invoke(getLongIndex, entity)).longValue());
        assertEquals(5.0f, ((Float) methodInvoker.invoke(getFloatIndex, entity)).floatValue(), 0.0001f);
        assertEquals(6.0, ((Double) methodInvoker.invoke(getDoubleIndex, entity)).doubleValue(), 0.000001);
        assertEquals(true, methodInvoker.invoke(getBooleanIndex, entity));
        assertEquals('A', ((Character) methodInvoker.invoke(getCharIndex, entity)).charValue());
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        int setStringIndex = methodInvokerHelper.getMethodIndex("setString", String.class);
        int getStringIndex = methodInvokerHelper.getMethodIndex("getString");
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            methodInvoker.invoke(setStringIndex, entity, str); // setString
            assertEquals(str, methodInvoker.invoke(getStringIndex, entity)); // getString
        }
    }

    @Test
    void testPrecisionFloat() {
        // 测试 float 精度
        int setFloatIndex = methodInvokerHelper.getMethodIndex("setFloat", float.class);
        int getFloatIndex = methodInvokerHelper.getMethodIndex("getFloat");
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            methodInvoker.invokeFloat1(setFloatIndex, entity, value); // setFloat
            assertEquals(value, methodInvoker.floatInvoke(getFloatIndex, entity), 0.0001f); // getFloat
        }
    }

    @Test
    void testPrecisionDouble() {
        // 测试 double 精度
        int setDoubleIndex = methodInvokerHelper.getMethodIndex("setDouble", double.class);
        int getDoubleIndex = methodInvokerHelper.getMethodIndex("getDouble");
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            methodInvoker.invokeDouble1(setDoubleIndex, entity, value); // setDouble
            assertEquals(value, methodInvoker.doubleInvoke(getDoubleIndex, entity), 0.000001); // getDouble
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        int setCharIndex = methodInvokerHelper.getMethodIndex("setChar", char.class);
        int getCharIndex = methodInvokerHelper.getMethodIndex("getChar");
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            methodInvoker.invokeChar1(setCharIndex, entity, c); // setChar
            assertEquals(c, methodInvoker.charInvoke(getCharIndex, entity)); // getChar
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        int setIntIndex = methodInvokerHelper.getMethodIndex("setInt", int.class);
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");

        for (int i = 0; i < 100; i++) {
            methodInvoker.invoke(setIntIndex, entity, 42); // setInt
            Object result = methodInvoker.invoke(getIntIndex, entity); // getInt
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

        methodInvoker.invoke(setIntIndex, entity, 10); // setInt(10)
        int result = methodInvoker.intInvoke(addIntIndex, entity, 20); // addInt(20) -> 返回 30
        assertEquals(30, result);

        methodInvoker.invoke(setLongIndex, entity, 100L); // setLong(100L)
        long longResult = methodInvoker.longInvoke(addLongIndex, entity, 200L); // addLong(200L) -> 返回 300L
        assertEquals(300L, longResult);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        int getIntIndex = methodInvokerHelper.getMethodIndex("getInt");
        int voidMethodIndex = methodInvokerHelper.getMethodIndex("voidMethod");

        // 第一个方法
        entity.setInt(1);
        assertEquals(1, ((Integer) methodInvoker.invoke(getIntIndex, entity)).intValue());

        // voidMethod
        Object result = methodInvoker.invoke(voidMethodIndex, entity); // voidMethod()
        assertNull(result);
    }

    @Test
    void testExceptionMessages() {
        // 测试异常消息
        try {
            methodInvoker.invoke(-1, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }

        try {
            methodInvoker.intInvoke(1000, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid") || e.getMessage().contains("index"));
        }
    }
}
