package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.invoker.FieldInvokerHelper;
import com.github.archtiger.bytebean.api.field.FieldInvoker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FieldAccess 接口的严格测试
 * <p>
 * 测试包括：
 * 1. 通用 get/set 方法（Object 类型）
 * 2. 所有基本类型的 getter/setter 方法
 * 3. 索引越界异常
 * 4. 类型不匹配异常（使用错误的类型访问字段）
 * 5. null 值处理
 * 6. 边界值测试
 * 7. 极端场景测试
 */
class FieldInvokerTest {

    private FieldInvokerHelper fieldInvokerHelper;
    private FieldInvoker fieldInvoker;
    private TestEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        fieldInvokerHelper = FieldInvokerHelper.of(TestEntity.class);
        fieldInvoker = fieldInvokerHelper.getFieldInvoker();
        entity = new TestEntity();
    }

    // ==================== 通用 get/set 方法测试 ====================

    @Test
    void testGetSetObject() {
        // 测试 String 字段
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        fieldInvoker.set(stringFieldIndex, entity, "testString");
        assertEquals("testString", fieldInvoker.get(stringFieldIndex, entity));

        // 测试 Integer 字段
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        fieldInvoker.set(integerFieldIndex, entity, 42);
        assertEquals(42, fieldInvoker.get(integerFieldIndex, entity));
    }

    @Test
    void testGetSetPrimitiveAsObject() {
        // 测试基本类型字段通过 Object 方法访问（会装箱）
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        fieldInvoker.set(intFieldIndex, entity, 123);
        Object value = fieldInvoker.get(intFieldIndex, entity);
        assertNotNull(value);
        assertEquals(123, ((Integer) value).intValue());
    }

    @Test
    void testGetSetAllFields() {
        // 测试所有字段的 get/set
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        fieldInvoker.set(booleanFieldIndex, entity, true);
        fieldInvoker.set(byteFieldIndex, entity, (byte) 10);
        fieldInvoker.set(charFieldIndex, entity, 'X');
        fieldInvoker.set(doubleFieldIndex, entity, 60.0);
        fieldInvoker.set(floatFieldIndex, entity, 50.0f);
        fieldInvoker.set(intFieldIndex, entity, 30);
        fieldInvoker.set(longFieldIndex, entity, 40L);
        fieldInvoker.set(shortFieldIndex, entity, (short) 20);
        fieldInvoker.set(integerFieldIndex, entity, 100);
        fieldInvoker.set(stringFieldIndex, entity, "test");

        assertEquals(true, fieldInvoker.get(booleanFieldIndex, entity));
        assertEquals((byte) 10, fieldInvoker.get(byteFieldIndex, entity));
        assertEquals('X', fieldInvoker.get(charFieldIndex, entity));
        assertEquals(60.0, ((Double) fieldInvoker.get(doubleFieldIndex, entity)).doubleValue(), 0.000001);
        assertEquals(50.0f, ((Float) fieldInvoker.get(floatFieldIndex, entity)).floatValue(), 0.0001f);
        assertEquals(30, fieldInvoker.get(intFieldIndex, entity));
        assertEquals(40L, fieldInvoker.get(longFieldIndex, entity));
        assertEquals((short) 20, fieldInvoker.get(shortFieldIndex, entity));
        assertEquals(100, fieldInvoker.get(integerFieldIndex, entity));
        assertEquals("test", fieldInvoker.get(stringFieldIndex, entity));
    }

    // ==================== 基本类型 getter 测试 ====================

    @Test
    void testGetByte() {
        entity.byteField = (byte) 42;
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        byte value = fieldInvoker.getByte(byteFieldIndex, entity);
        assertEquals((byte) 42, value);
    }

    @Test
    void testGetShort() {
        entity.shortField = (short) 12345;
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        short value = fieldInvoker.getShort(shortFieldIndex, entity);
        assertEquals((short) 12345, value);
    }

    @Test
    void testGetInt() {
        entity.intField = 123456;
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int value = fieldInvoker.getInt(intFieldIndex, entity);
        assertEquals(123456, value);
    }

    @Test
    void testGetLong() {
        entity.longField = 123456789L;
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        long value = fieldInvoker.getLong(longFieldIndex, entity);
        assertEquals(123456789L, value);
    }

    @Test
    void testGetFloat() {
        entity.floatField = 3.14f;
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        float value = fieldInvoker.getFloat(floatFieldIndex, entity);
        assertEquals(3.14f, value, 0.0001f);
    }

    @Test
    void testGetDouble() {
        entity.doubleField = 2.71828;
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        double value = fieldInvoker.getDouble(doubleFieldIndex, entity);
        assertEquals(2.71828, value, 0.000001);
    }

    @Test
    void testGetBoolean() {
        entity.booleanField = true;
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        boolean value = fieldInvoker.getBoolean(booleanFieldIndex, entity);
        assertTrue(value);

        entity.booleanField = false;
        value = fieldInvoker.getBoolean(booleanFieldIndex, entity);
        assertFalse(value);
    }

    @Test
    void testGetChar() {
        entity.charField = 'A';
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        char value = fieldInvoker.getChar(charFieldIndex, entity);
        assertEquals('A', value);
    }

    // ==================== 基本类型 setter 测试 ====================

    @Test
    void testSetByte() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) 42);
        assertEquals((byte) 42, entity.byteField);
    }

    @Test
    void testSetShort() {
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        fieldInvoker.setShort(shortFieldIndex, entity, (short) 12345);
        assertEquals((short) 12345, entity.shortField);
    }

    @Test
    void testSetInt() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        fieldInvoker.setInt(intFieldIndex, entity, 123456);
        assertEquals(123456, entity.intField);
    }

    @Test
    void testSetLong() {
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        fieldInvoker.setLong(longFieldIndex, entity, 123456789L);
        assertEquals(123456789L, entity.longField);
    }

    @Test
    void testSetFloat() {
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        fieldInvoker.setFloat(floatFieldIndex, entity, 3.14f);
        assertEquals(3.14f, entity.floatField, 0.0001f);
    }

    @Test
    void testSetDouble() {
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        fieldInvoker.setDouble(doubleFieldIndex, entity, 2.71828);
        assertEquals(2.71828, entity.doubleField, 0.000001);
    }

    @Test
    void testSetBoolean() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        fieldInvoker.setBoolean(booleanFieldIndex, entity, true);
        assertTrue(entity.booleanField);

        fieldInvoker.setBoolean(booleanFieldIndex, entity, false);
        assertFalse(entity.booleanField);
    }

    @Test
    void testSetChar() {
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        fieldInvoker.setChar(charFieldIndex, entity, 'Z');
        assertEquals('Z', entity.charField);
    }

    // ==================== 边界值测试 ====================

    @Test
    void testByteBoundaryValues() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        // byte 最小值
        fieldInvoker.setByte(byteFieldIndex, entity, Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, fieldInvoker.getByte(byteFieldIndex, entity));

        // byte 最大值
        fieldInvoker.setByte(byteFieldIndex, entity, Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, fieldInvoker.getByte(byteFieldIndex, entity));

        // byte 零值
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) 0);
        assertEquals((byte) 0, fieldInvoker.getByte(byteFieldIndex, entity));

        // byte -1
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) -1);
        assertEquals((byte) -1, fieldInvoker.getByte(byteFieldIndex, entity));
    }

    @Test
    void testShortBoundaryValues() {
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        // short 最小值
        fieldInvoker.setShort(shortFieldIndex, entity, Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, fieldInvoker.getShort(shortFieldIndex, entity));

        // short 最大值
        fieldInvoker.setShort(shortFieldIndex, entity, Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, fieldInvoker.getShort(shortFieldIndex, entity));

        // short 零值
        fieldInvoker.setShort(shortFieldIndex, entity, (short) 0);
        assertEquals((short) 0, fieldInvoker.getShort(shortFieldIndex, entity));

        // short -1
        fieldInvoker.setShort(shortFieldIndex, entity, (short) -1);
        assertEquals((short) -1, fieldInvoker.getShort(shortFieldIndex, entity));
    }

    @Test
    void testIntBoundaryValues() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // int 最小值
        fieldInvoker.setInt(intFieldIndex, entity, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, fieldInvoker.getInt(intFieldIndex, entity));

        // int 最大值
        fieldInvoker.setInt(intFieldIndex, entity, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fieldInvoker.getInt(intFieldIndex, entity));

        // int 零值
        fieldInvoker.setInt(intFieldIndex, entity, 0);
        assertEquals(0, fieldInvoker.getInt(intFieldIndex, entity));

        // int -1
        fieldInvoker.setInt(intFieldIndex, entity, -1);
        assertEquals(-1, fieldInvoker.getInt(intFieldIndex, entity));
    }

    @Test
    void testLongBoundaryValues() {
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        // long 最小值
        fieldInvoker.setLong(longFieldIndex, entity, Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, fieldInvoker.getLong(longFieldIndex, entity));

        // long 最大值
        fieldInvoker.setLong(longFieldIndex, entity, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, fieldInvoker.getLong(longFieldIndex, entity));

        // long 零值
        fieldInvoker.setLong(longFieldIndex, entity, 0L);
        assertEquals(0L, fieldInvoker.getLong(longFieldIndex, entity));

        // long -1
        fieldInvoker.setLong(longFieldIndex, entity, -1L);
        assertEquals(-1L, fieldInvoker.getLong(longFieldIndex, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        // float 最小值
        fieldInvoker.setFloat(floatFieldIndex, entity, Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, fieldInvoker.getFloat(floatFieldIndex, entity));

        // float 最大值
        fieldInvoker.setFloat(floatFieldIndex, entity, Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, fieldInvoker.getFloat(floatFieldIndex, entity));

        // float 正无穷
        fieldInvoker.setFloat(floatFieldIndex, entity, Float.POSITIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldInvoker.getFloat(floatFieldIndex, entity)));
        assertTrue(fieldInvoker.getFloat(floatFieldIndex, entity) > 0);

        // float 负无穷
        fieldInvoker.setFloat(floatFieldIndex, entity, Float.NEGATIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldInvoker.getFloat(floatFieldIndex, entity)));
        assertTrue(fieldInvoker.getFloat(floatFieldIndex, entity) < 0);

        // float NaN
        fieldInvoker.setFloat(floatFieldIndex, entity, Float.NaN);
        assertTrue(Float.isNaN(fieldInvoker.getFloat(floatFieldIndex, entity)));

        // float 零值
        fieldInvoker.setFloat(floatFieldIndex, entity, 0.0f);
        assertEquals(0.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0f);

        // float 负零
        fieldInvoker.setFloat(floatFieldIndex, entity, -0.0f);
        assertEquals(-0.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0f);

        // float 最小正数
        fieldInvoker.setFloat(floatFieldIndex, entity, Float.MIN_NORMAL);
        assertEquals(Float.MIN_NORMAL, fieldInvoker.getFloat(floatFieldIndex, entity));
    }

    @Test
    void testDoubleBoundaryValues() {
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        // double 最小值
        fieldInvoker.setDouble(doubleFieldIndex, entity, Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, fieldInvoker.getDouble(doubleFieldIndex, entity));

        // double 最大值
        fieldInvoker.setDouble(doubleFieldIndex, entity, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, fieldInvoker.getDouble(doubleFieldIndex, entity));

        // double 正无穷
        fieldInvoker.setDouble(doubleFieldIndex, entity, Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldInvoker.getDouble(doubleFieldIndex, entity)));
        assertTrue(fieldInvoker.getDouble(doubleFieldIndex, entity) > 0);

        // double 负无穷
        fieldInvoker.setDouble(doubleFieldIndex, entity, Double.NEGATIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldInvoker.getDouble(doubleFieldIndex, entity)));
        assertTrue(fieldInvoker.getDouble(doubleFieldIndex, entity) < 0);

        // double NaN
        fieldInvoker.setDouble(doubleFieldIndex, entity, Double.NaN);
        assertTrue(Double.isNaN(fieldInvoker.getDouble(doubleFieldIndex, entity)));

        // double 零值
        fieldInvoker.setDouble(doubleFieldIndex, entity, 0.0);
        assertEquals(0.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.0);

        // double 负零
        fieldInvoker.setDouble(doubleFieldIndex, entity, -0.0);
        assertEquals(-0.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.0);

        // double 最小正数
        fieldInvoker.setDouble(doubleFieldIndex, entity, Double.MIN_NORMAL);
        assertEquals(Double.MIN_NORMAL, fieldInvoker.getDouble(doubleFieldIndex, entity));
    }

    @Test
    void testCharBoundaryValues() {
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        // char 最小值
        fieldInvoker.setChar(charFieldIndex, entity, Character.MIN_VALUE);
        assertEquals(Character.MIN_VALUE, fieldInvoker.getChar(charFieldIndex, entity));

        // char 最大值
        fieldInvoker.setChar(charFieldIndex, entity, Character.MAX_VALUE);
        assertEquals(Character.MAX_VALUE, fieldInvoker.getChar(charFieldIndex, entity));

        // char 零值
        fieldInvoker.setChar(charFieldIndex, entity, '\u0000');
        assertEquals('\u0000', fieldInvoker.getChar(charFieldIndex, entity));

        // char 常用字符
        fieldInvoker.setChar(charFieldIndex, entity, 'A');
        assertEquals('A', fieldInvoker.getChar(charFieldIndex, entity));

        fieldInvoker.setChar(charFieldIndex, entity, '中');
        assertEquals('中', fieldInvoker.getChar(charFieldIndex, entity));
    }

    @Test
    void testBooleanBoundaryValues() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        // boolean true
        fieldInvoker.setBoolean(booleanFieldIndex, entity, true);
        assertTrue(fieldInvoker.getBoolean(booleanFieldIndex, entity));

        // boolean false
        fieldInvoker.setBoolean(booleanFieldIndex, entity, false);
        assertFalse(fieldInvoker.getBoolean(booleanFieldIndex, entity));
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBoundsGet() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.get(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.get(100, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.get(Integer.MIN_VALUE, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.get(Integer.MAX_VALUE, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsSet() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.set(-1, entity, "value");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.set(100, entity, "value");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.set(Integer.MIN_VALUE, entity, "value");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.set(Integer.MAX_VALUE, entity, "value");
        });
    }

    @Test
    void testIndexOutOfBoundsGetByte() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getByte(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getByte(100, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsSetByte() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setByte(-1, entity, (byte) 42);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setByte(100, entity, (byte) 42);
        });
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveGetters() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getInt(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getLong(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getFloat(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getDouble(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getBoolean(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getChar(-1, entity));

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getInt(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getLong(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getFloat(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getDouble(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getBoolean(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getChar(100, entity));
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveSetters() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(-1, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setInt(-1, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setLong(-1, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setFloat(-1, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setDouble(-1, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setBoolean(-1, entity, true));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setChar(-1, entity, 'A'));

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(100, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setInt(100, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setLong(100, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setFloat(100, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setDouble(100, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setBoolean(100, entity, true));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setChar(100, entity, 'A'));
    }

    // ==================== 类型不匹配异常测试 ====================

    @Test
    void testTypeMismatchGetByte() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 尝试使用 getByte 访问非 byte 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getByte(intFieldIndex, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getByte(stringFieldIndex, entity);
        });
    }

    @Test
    void testTypeMismatchGetShort() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(byteFieldIndex, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(intFieldIndex, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(stringFieldIndex, entity));
    }

    @Test
    void testTypeMismatchGetInt() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 尝试使用 getInt 访问非 int 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getInt(byteFieldIndex, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getInt(stringFieldIndex, entity);
        });
    }

    @Test
    void testTypeMismatchGetLong() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 尝试使用 getLong 访问非 long 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getLong(intFieldIndex, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getLong(stringFieldIndex, entity);
        });
    }

    @Test
    void testTypeMismatchGetFloat() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getFloat(intFieldIndex, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getFloat(stringFieldIndex, entity));
    }

    @Test
    void testTypeMismatchGetDouble() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getDouble(intFieldIndex, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getDouble(stringFieldIndex, entity));
    }

    @Test
    void testTypeMismatchGetBoolean() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getBoolean(intFieldIndex, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getBoolean(stringFieldIndex, entity));
    }

    @Test
    void testTypeMismatchGetChar() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getChar(intFieldIndex, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getChar(stringFieldIndex, entity));
    }

    @Test
    void testTypeMismatchSetByte() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 尝试使用 setByte 设置非 byte 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setByte(intFieldIndex, entity, (byte) 42);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setByte(stringFieldIndex, entity, (byte) 42);
        });
    }

    @Test
    void testTypeMismatchSetShort() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(byteFieldIndex, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(intFieldIndex, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(stringFieldIndex, entity, (short) 1));
    }

    @Test
    void testTypeMismatchSetInt() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 尝试使用 setInt 设置非 int 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setInt(byteFieldIndex, entity, 42);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setInt(stringFieldIndex, entity, 42);
        });
    }

    @Test
    void testTypeMismatchSetLong() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setLong(intFieldIndex, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setLong(stringFieldIndex, entity, 1L));
    }

    @Test
    void testTypeMismatchSetFloat() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setFloat(intFieldIndex, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setFloat(stringFieldIndex, entity, 1.0f));
    }

    @Test
    void testTypeMismatchSetDouble() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setDouble(intFieldIndex, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setDouble(stringFieldIndex, entity, 1.0));
    }

    @Test
    void testTypeMismatchSetBoolean() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setBoolean(intFieldIndex, entity, true));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setBoolean(stringFieldIndex, entity, true));
    }

    @Test
    void testTypeMismatchSetChar() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setChar(intFieldIndex, entity, 'A'));
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setChar(stringFieldIndex, entity, 'A'));
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 对于基本类型字段，GETFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.get(byteFieldIndex, null);
        });

        // 对于引用类型字段，GETFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.get(stringFieldIndex, null);
        });

        // 对于基本类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        // 使用正确的类型（byte）来避免类型不匹配
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.set(byteFieldIndex, null, (byte) 42);
        });

        // 对于引用类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.set(stringFieldIndex, null, "value");
        });
    }

    @Test
    void testNullInstanceAllPrimitiveGetters() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");

        assertThrows(NullPointerException.class, () -> fieldInvoker.getByte(byteFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getShort(shortFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getInt(intFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getLong(longFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getFloat(floatFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getDouble(doubleFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getBoolean(booleanFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getChar(charFieldIndex, null));
    }

    @Test
    void testNullInstanceAllPrimitiveSetters() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");

        assertThrows(NullPointerException.class, () -> fieldInvoker.setByte(byteFieldIndex, null, (byte) 1));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setShort(shortFieldIndex, null, (short) 1));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setInt(intFieldIndex, null, 1));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setLong(longFieldIndex, null, 1L));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setFloat(floatFieldIndex, null, 1.0f));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setDouble(doubleFieldIndex, null, 1.0));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setBoolean(booleanFieldIndex, null, true));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setChar(charFieldIndex, null, 'A'));
    }

    @Test
    void testNullValueSet() {
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 对于引用类型字段，可以设置为 null
        fieldInvoker.set(integerFieldIndex, entity, null);
        assertNull(fieldInvoker.get(integerFieldIndex, entity));

        fieldInvoker.set(stringFieldIndex, entity, null);
        assertNull(fieldInvoker.get(stringFieldIndex, entity));
    }

    @Test
    void testNullValueThenSet() {
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 设置为 null 后可以重新设置
        fieldInvoker.set(stringFieldIndex, entity, null);
        assertNull(fieldInvoker.get(stringFieldIndex, entity));

        fieldInvoker.set(stringFieldIndex, entity, "newValue");
        assertEquals("newValue", fieldInvoker.get(stringFieldIndex, entity));
    }

    // ==================== 组合测试 ====================

    @Test
    void testAllPrimitiveTypes() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");

        // 测试所有基本类型的 set 和 get
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) 1);
        fieldInvoker.setShort(shortFieldIndex, entity, (short) 2);
        fieldInvoker.setInt(intFieldIndex, entity, 3);
        fieldInvoker.setLong(longFieldIndex, entity, 4L);
        fieldInvoker.setFloat(floatFieldIndex, entity, 5.0f);
        fieldInvoker.setDouble(doubleFieldIndex, entity, 6.0);
        fieldInvoker.setBoolean(booleanFieldIndex, entity, true);
        fieldInvoker.setChar(charFieldIndex, entity, '7');

        assertEquals((byte) 1, fieldInvoker.getByte(byteFieldIndex, entity));
        assertEquals((short) 2, fieldInvoker.getShort(shortFieldIndex, entity));
        assertEquals(3, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals(4L, fieldInvoker.getLong(longFieldIndex, entity));
        assertEquals(5.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(6.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        assertTrue(fieldInvoker.getBoolean(booleanFieldIndex, entity));
        assertEquals('7', fieldInvoker.getChar(charFieldIndex, entity));
    }

    @Test
    void testMultipleOperations() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 多次设置和获取
        for (int i = 0; i < 10; i++) {
            fieldInvoker.setInt(intFieldIndex, entity, i);
            assertEquals(i, fieldInvoker.getInt(intFieldIndex, entity));
        }
    }

    @Test
    void testMultipleOperationsAllTypes() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");

        // 对所有基本类型进行多次操作
        for (int i = 0; i < 5; i++) {
            fieldInvoker.setByte(byteFieldIndex, entity, (byte) i);
            assertEquals((byte) i, fieldInvoker.getByte(byteFieldIndex, entity));

            fieldInvoker.setShort(shortFieldIndex, entity, (short) i);
            assertEquals((short) i, fieldInvoker.getShort(shortFieldIndex, entity));

            fieldInvoker.setInt(intFieldIndex, entity, i);
            assertEquals(i, fieldInvoker.getInt(intFieldIndex, entity));

            fieldInvoker.setLong(longFieldIndex, entity, (long) i);
            assertEquals((long) i, fieldInvoker.getLong(longFieldIndex, entity));

            fieldInvoker.setFloat(floatFieldIndex, entity, (float) i);
            assertEquals((float) i, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);

            fieldInvoker.setDouble(doubleFieldIndex, entity, (double) i);
            assertEquals((double) i, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);

            fieldInvoker.setBoolean(booleanFieldIndex, entity, i % 2 == 0);
            assertEquals(i % 2 == 0, fieldInvoker.getBoolean(booleanFieldIndex, entity));

            fieldInvoker.setChar(charFieldIndex, entity, (char) ('A' + i));
            assertEquals((char) ('A' + i), fieldInvoker.getChar(charFieldIndex, entity));
        }
    }

    @Test
    void testMixedAccess() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 混合使用 Object 方法和基本类型方法
        fieldInvoker.set(intFieldIndex, entity, 100);
        assertEquals(100, ((Integer) fieldInvoker.get(intFieldIndex, entity)).intValue());

        fieldInvoker.setInt(intFieldIndex, entity, 200);
        assertEquals(200, fieldInvoker.getInt(intFieldIndex, entity));

        fieldInvoker.set(intFieldIndex, entity, 300);
        assertEquals(300, ((Integer) fieldInvoker.get(intFieldIndex, entity)).intValue());
    }

    @Test
    void testMixedAccessAllFields() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 混合使用 Object 方法和基本类型方法访问所有字段
        // byte
        fieldInvoker.set(byteFieldIndex, entity, (byte) 10);
        assertEquals((byte) 10, ((Byte) fieldInvoker.get(byteFieldIndex, entity)).byteValue());
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) 20);
        assertEquals((byte) 20, fieldInvoker.getByte(byteFieldIndex, entity));

        // int
        fieldInvoker.set(intFieldIndex, entity, 100);
        assertEquals(100, ((Integer) fieldInvoker.get(intFieldIndex, entity)).intValue());
        fieldInvoker.setInt(intFieldIndex, entity, 200);
        assertEquals(200, fieldInvoker.getInt(intFieldIndex, entity));

        // long
        fieldInvoker.set(longFieldIndex, entity, 1000L);
        assertEquals(1000L, ((Long) fieldInvoker.get(longFieldIndex, entity)).longValue());
        fieldInvoker.setLong(longFieldIndex, entity, 2000L);
        assertEquals(2000L, fieldInvoker.getLong(longFieldIndex, entity));

        // String
        fieldInvoker.set(stringFieldIndex, entity, "test1");
        assertEquals("test1", fieldInvoker.get(stringFieldIndex, entity));
        // String 字段没有对应的基本类型方法，只能使用 Object 方法
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidSetGet() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 快速连续设置和获取
        for (int i = 0; i < 1000; i++) {
            fieldInvoker.setInt(intFieldIndex, entity, i);
            int value = fieldInvoker.getInt(intFieldIndex, entity);
            assertEquals(i, value);
        }
    }

    @Test
    void testAllFieldsSequential() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 顺序访问所有字段
        fieldInvoker.setBoolean(booleanFieldIndex, entity, true);
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) 1);
        fieldInvoker.setChar(charFieldIndex, entity, 'C');
        fieldInvoker.setDouble(doubleFieldIndex, entity, 6.0);
        fieldInvoker.setFloat(floatFieldIndex, entity, 5.0f);
        fieldInvoker.setInt(intFieldIndex, entity, 3);
        fieldInvoker.setLong(longFieldIndex, entity, 4L);
        fieldInvoker.setShort(shortFieldIndex, entity, (short) 2);
        fieldInvoker.set(integerFieldIndex, entity, 999);
        fieldInvoker.set(stringFieldIndex, entity, "String");

        assertTrue(fieldInvoker.getBoolean(booleanFieldIndex, entity));
        assertEquals((byte) 1, fieldInvoker.getByte(byteFieldIndex, entity));
        assertEquals('C', fieldInvoker.getChar(charFieldIndex, entity));
        assertEquals(6.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        assertEquals(5.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(3, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals(4L, fieldInvoker.getLong(longFieldIndex, entity));
        assertEquals((short) 2, fieldInvoker.getShort(shortFieldIndex, entity));
        assertEquals(999, fieldInvoker.get(integerFieldIndex, entity));
        assertEquals("String", fieldInvoker.get(stringFieldIndex, entity));
    }

    @Test
    void testAllFieldsReverse() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 反向访问所有字段
        fieldInvoker.set(stringFieldIndex, entity, "String");
        fieldInvoker.set(integerFieldIndex, entity, 999);
        fieldInvoker.setShort(shortFieldIndex, entity, (short) 2);
        fieldInvoker.setLong(longFieldIndex, entity, 4L);
        fieldInvoker.setInt(intFieldIndex, entity, 3);
        fieldInvoker.setFloat(floatFieldIndex, entity, 5.0f);
        fieldInvoker.setDouble(doubleFieldIndex, entity, 6.0);
        fieldInvoker.setChar(charFieldIndex, entity, 'C');
        fieldInvoker.setByte(byteFieldIndex, entity, (byte) 1);
        fieldInvoker.setBoolean(booleanFieldIndex, entity, true);

        assertEquals("String", fieldInvoker.get(stringFieldIndex, entity));
        assertEquals(999, fieldInvoker.get(integerFieldIndex, entity));
        assertEquals((short) 2, fieldInvoker.getShort(shortFieldIndex, entity));
        assertEquals(4L, fieldInvoker.getLong(longFieldIndex, entity));
        assertEquals(3, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals(5.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(6.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        assertEquals('C', fieldInvoker.getChar(charFieldIndex, entity));
        assertEquals((byte) 1, fieldInvoker.getByte(byteFieldIndex, entity));
        assertTrue(fieldInvoker.getBoolean(booleanFieldIndex, entity));
    }

    @Test
    void testAlternatingAccess() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 交替访问不同字段
        fieldInvoker.setInt(intFieldIndex, entity, 1);
        fieldInvoker.set(stringFieldIndex, entity, "A");
        assertEquals(1, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals("A", fieldInvoker.get(stringFieldIndex, entity));

        fieldInvoker.setInt(intFieldIndex, entity, 2);
        fieldInvoker.set(stringFieldIndex, entity, "B");
        assertEquals(2, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals("B", fieldInvoker.get(stringFieldIndex, entity));

        fieldInvoker.setInt(intFieldIndex, entity, 3);
        fieldInvoker.set(stringFieldIndex, entity, "C");
        assertEquals(3, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals("C", fieldInvoker.get(stringFieldIndex, entity));
    }

    @Test
    void testFloatPrecision() {
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        // 测试 float 精度
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            fieldInvoker.setFloat(floatFieldIndex, entity, value);
            assertEquals(value, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        }
    }

    @Test
    void testDoublePrecision() {
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        // 测试 double 精度
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            fieldInvoker.setDouble(doubleFieldIndex, entity, value);
            assertEquals(value, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        }
    }

    @Test
    void testCharUnicode() {
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        // 测试 Unicode 字符
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            fieldInvoker.setChar(charFieldIndex, entity, c);
            assertEquals(c, fieldInvoker.getChar(charFieldIndex, entity));
        }
    }

    @Test
    void testStringSpecialValues() {
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 测试 String 特殊值
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            fieldInvoker.set(stringFieldIndex, entity, str);
            assertEquals(str, fieldInvoker.get(stringFieldIndex, entity));
        }
    }

    @Test
    void testIntegerBoxingUnboxing() {
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        // 测试 Integer 装箱拆箱
        Integer[] testValues = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 100, -100};
        for (Integer value : testValues) {
            fieldInvoker.set(integerFieldIndex, entity, value);
            Object result = fieldInvoker.get(integerFieldIndex, entity);
            assertNotNull(result);
            assertEquals(value, result);
            assertTrue(result instanceof Integer);
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 多次设置相同值
        for (int i = 0; i < 100; i++) {
            fieldInvoker.setInt(intFieldIndex, entity, 42);
            assertEquals(42, fieldInvoker.getInt(intFieldIndex, entity));
        }
    }

    @Test
    void testCrossFieldInteraction() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");

        // 测试字段之间的交互
        fieldInvoker.setInt(intFieldIndex, entity, 100);
        fieldInvoker.setLong(longFieldIndex, entity, 200L);
        fieldInvoker.setFloat(floatFieldIndex, entity, 300.0f);
        fieldInvoker.setDouble(doubleFieldIndex, entity, 400.0);

        assertEquals(100, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals(200L, fieldInvoker.getLong(longFieldIndex, entity));
        assertEquals(300.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(400.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);

        // 再次设置
        fieldInvoker.setInt(intFieldIndex, entity, 500);
        fieldInvoker.setLong(longFieldIndex, entity, 600L);
        fieldInvoker.setFloat(floatFieldIndex, entity, 700.0f);
        fieldInvoker.setDouble(doubleFieldIndex, entity, 800.0);

        assertEquals(500, fieldInvoker.getInt(intFieldIndex, entity));
        assertEquals(600L, fieldInvoker.getLong(longFieldIndex, entity));
        assertEquals(700.0f, fieldInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(800.0, fieldInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
    }

    @Test
    void testValidIndexBoundaries() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");

        // 测试有效索引边界
        // 第一个字段 (index 0)
        fieldInvoker.setBoolean(booleanFieldIndex, entity, true);
        assertTrue(fieldInvoker.getBoolean(booleanFieldIndex, entity));

        // 最后一个字段 (index 9)
        fieldInvoker.set(stringFieldIndex, entity, "999");
        assertEquals("999", fieldInvoker.get(stringFieldIndex, entity));

        // 中间字段 (index 5)
        fieldInvoker.setInt(intFieldIndex, entity, 123);
        assertEquals(123, fieldInvoker.getInt(intFieldIndex, entity));
    }

    @Test
    void testExceptionMessages() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        // 测试异常消息
        try {
            fieldInvoker.get(-1, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid field index") || e.getMessage().contains("index"));
        }

        try {
            fieldInvoker.getInt(byteFieldIndex, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid field index") || e.getMessage().contains("index"));
        }
    }
}
