package com.github.archtiger.bytebean.core.invoker.field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FieldVarHandleInvoker 类的功能完整性测试
 * <p>
 * 测试包括：
 * 1. 通用 get/set 方法（Object 类型）
 * 2. 所有基本类型的 getter/setter 方法
 * 3. 索引越界异常
 * 4. null 值处理
 * 5. 边界值测试
 * 6. 极端场景测试
 * 7. VarHandle 特有行为测试
 */
class FieldVarHandleInvokerTest {

    private FieldVarHandleInvoker fieldVarHandleInvoker;
    private FieldInvokerHelper fieldInvokerHelper;
    private TestEntity entity;

    @BeforeEach
    void setUp() {
        fieldVarHandleInvoker = FieldVarHandleInvoker.of(TestEntity.class);
        fieldInvokerHelper = FieldInvokerHelper.of(TestEntity.class);
        entity = new TestEntity();
    }

    // ==================== 通用 get/set 方法测试 ====================

    @Test
    void testGetSetObject() {
        // 测试 String 字段
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "testString");
        assertEquals("testString", fieldVarHandleInvoker.get(stringFieldIndex, entity));

        // 测试 Integer 字段
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        fieldVarHandleInvoker.set(integerFieldIndex, entity, 42);
        assertEquals(42, fieldVarHandleInvoker.get(integerFieldIndex, entity));
    }

    @Test
    void testGetSetPrimitiveAsObject() {
        // 测试基本类型字段通过 Object 方法访问（会装箱）
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        fieldVarHandleInvoker.set(intFieldIndex, entity, 123);
        Object value = fieldVarHandleInvoker.get(intFieldIndex, entity);
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

        fieldVarHandleInvoker.set(booleanFieldIndex, entity, true);
        fieldVarHandleInvoker.set(byteFieldIndex, entity, (byte) 10);
        fieldVarHandleInvoker.set(charFieldIndex, entity, 'X');
        fieldVarHandleInvoker.set(doubleFieldIndex, entity, 60.0);
        fieldVarHandleInvoker.set(floatFieldIndex, entity, 50.0f);
        fieldVarHandleInvoker.set(intFieldIndex, entity, 30);
        fieldVarHandleInvoker.set(longFieldIndex, entity, 40L);
        fieldVarHandleInvoker.set(shortFieldIndex, entity, (short) 20);
        fieldVarHandleInvoker.set(integerFieldIndex, entity, 100);
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "test");

        assertEquals(true, fieldVarHandleInvoker.get(booleanFieldIndex, entity));
        assertEquals((byte) 10, fieldVarHandleInvoker.get(byteFieldIndex, entity));
        assertEquals('X', fieldVarHandleInvoker.get(charFieldIndex, entity));
        assertEquals(60.0, ((Double) fieldVarHandleInvoker.get(doubleFieldIndex, entity)).doubleValue(), 0.000001);
        assertEquals(50.0f, ((Float) fieldVarHandleInvoker.get(floatFieldIndex, entity)).floatValue(), 0.0001f);
        assertEquals(30, fieldVarHandleInvoker.get(intFieldIndex, entity));
        assertEquals(40L, fieldVarHandleInvoker.get(longFieldIndex, entity));
        assertEquals((short) 20, fieldVarHandleInvoker.get(shortFieldIndex, entity));
        assertEquals(100, fieldVarHandleInvoker.get(integerFieldIndex, entity));
        assertEquals("test", fieldVarHandleInvoker.get(stringFieldIndex, entity));
    }

    // ==================== 基本类型 getter 测试 ====================

    @Test
    void testGetByte() {
        entity.byteField = (byte) 42;
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        byte value = fieldVarHandleInvoker.getByte(byteFieldIndex, entity);
        assertEquals((byte) 42, value);
    }

    @Test
    void testGetShort() {
        entity.shortField = (short) 12345;
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        short value = fieldVarHandleInvoker.getShort(shortFieldIndex, entity);
        assertEquals((short) 12345, value);
    }

    @Test
    void testGetInt() {
        entity.intField = 123456;
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int value = fieldVarHandleInvoker.getInt(intFieldIndex, entity);
        assertEquals(123456, value);
    }

    @Test
    void testGetLong() {
        entity.longField = 123456789L;
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        long value = fieldVarHandleInvoker.getLong(longFieldIndex, entity);
        assertEquals(123456789L, value);
    }

    @Test
    void testGetFloat() {
        entity.floatField = 3.14f;
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        float value = fieldVarHandleInvoker.getFloat(floatFieldIndex, entity);
        assertEquals(3.14f, value, 0.0001f);
    }

    @Test
    void testGetDouble() {
        entity.doubleField = 2.71828;
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        double value = fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity);
        assertEquals(2.71828, value, 0.000001);
    }

    @Test
    void testGetBoolean() {
        entity.booleanField = true;
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        boolean value = fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity);
        assertTrue(value);

        entity.booleanField = false;
        value = fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity);
        assertFalse(value);
    }

    @Test
    void testGetChar() {
        entity.charField = 'A';
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        char value = fieldVarHandleInvoker.getChar(charFieldIndex, entity);
        assertEquals('A', value);
    }

    // ==================== 基本类型 setter 测试 ====================

    @Test
    void testSetByte() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) 42);
        assertEquals((byte) 42, entity.byteField);
    }

    @Test
    void testSetShort() {
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) 12345);
        assertEquals((short) 12345, entity.shortField);
    }

    @Test
    void testSetInt() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 123456);
        assertEquals(123456, entity.intField);
    }

    @Test
    void testSetLong() {
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 123456789L);
        assertEquals(123456789L, entity.longField);
    }

    @Test
    void testSetFloat() {
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 3.14f);
        assertEquals(3.14f, entity.floatField, 0.0001f);
    }

    @Test
    void testSetDouble() {
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 2.71828);
        assertEquals(2.71828, entity.doubleField, 0.000001);
    }

    @Test
    void testSetBoolean() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);
        assertTrue(entity.booleanField);

        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, false);
        assertFalse(entity.booleanField);
    }

    @Test
    void testSetChar() {
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, 'Z');
        assertEquals('Z', entity.charField);
    }

    // ==================== 边界值测试 ====================

    @Test
    void testByteBoundaryValues() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        // byte 最小值
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));

        // byte 最大值
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));

        // byte 零值
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) 0);
        assertEquals((byte) 0, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));

        // byte -1
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) -1);
        assertEquals((byte) -1, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));
    }

    @Test
    void testShortBoundaryValues() {
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        // short 最小值
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));

        // short 最大值
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));

        // short 零值
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) 0);
        assertEquals((short) 0, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));

        // short -1
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) -1);
        assertEquals((short) -1, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));
    }

    @Test
    void testIntBoundaryValues() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // int 最小值
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

        // int 最大值
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

        // int 零值
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 0);
        assertEquals(0, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

        // int -1
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, -1);
        assertEquals(-1, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
    }

    @Test
    void testLongBoundaryValues() {
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        // long 最小值
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, fieldVarHandleInvoker.getLong(longFieldIndex, entity));

        // long 最大值
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, fieldVarHandleInvoker.getLong(longFieldIndex, entity));

        // long 零值
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 0L);
        assertEquals(0L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));

        // long -1
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, -1L);
        assertEquals(-1L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        // float 最小值
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity));

        // float 最大值
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity));

        // float 正无穷
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.POSITIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldVarHandleInvoker.getFloat(floatFieldIndex, entity)));
        assertTrue(fieldVarHandleInvoker.getFloat(floatFieldIndex, entity) > 0);

        // float 负无穷
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.NEGATIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldVarHandleInvoker.getFloat(floatFieldIndex, entity)));
        assertTrue(fieldVarHandleInvoker.getFloat(floatFieldIndex, entity) < 0);

        // float NaN
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.NaN);
        assertTrue(Float.isNaN(fieldVarHandleInvoker.getFloat(floatFieldIndex, entity)));

        // float 零值
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 0.0f);
        assertEquals(0.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0f);

        // float 负零
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, -0.0f);
        assertEquals(-0.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0f);

        // float 最小正数
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.MIN_NORMAL);
        assertEquals(Float.MIN_NORMAL, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity));
    }

    @Test
    void testDoubleBoundaryValues() {
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        // double 最小值
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity));

        // double 最大值
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity));

        // double 正无穷
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity)));
        assertTrue(fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity) > 0);

        // double 负无穷
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.NEGATIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity)));
        assertTrue(fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity) < 0);

        // double NaN
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.NaN);
        assertTrue(Double.isNaN(fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity)));

        // double 零值
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 0.0);
        assertEquals(0.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.0);

        // double 负零
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, -0.0);
        assertEquals(-0.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.0);

        // double 最小正数
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.MIN_NORMAL);
        assertEquals(Double.MIN_NORMAL, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity));
    }

    @Test
    void testCharBoundaryValues() {
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        // char 最小值
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, Character.MIN_VALUE);
        assertEquals(Character.MIN_VALUE, fieldVarHandleInvoker.getChar(charFieldIndex, entity));

        // char 最大值
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, Character.MAX_VALUE);
        assertEquals(Character.MAX_VALUE, fieldVarHandleInvoker.getChar(charFieldIndex, entity));

        // char 零值
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, '\u0000');
        assertEquals('\u0000', fieldVarHandleInvoker.getChar(charFieldIndex, entity));

        // char 常用字符
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, 'A');
        assertEquals('A', fieldVarHandleInvoker.getChar(charFieldIndex, entity));

        fieldVarHandleInvoker.setChar(charFieldIndex, entity, '中');
        assertEquals('中', fieldVarHandleInvoker.getChar(charFieldIndex, entity));
    }

    @Test
    void testBooleanBoundaryValues() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        // boolean true
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);
        assertTrue(fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));

        // boolean false
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, false);
        assertFalse(fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBoundsGet() {
        // VarHandle 对于越界索引会抛出 IndexOutOfBoundsException
        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.get(-1, entity);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.get(100, entity);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.get(Integer.MIN_VALUE, entity);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.get(Integer.MAX_VALUE, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsSet() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.set(-1, entity, "value");
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.set(100, entity, "value");
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.set(Integer.MIN_VALUE, entity, "value");
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.set(Integer.MAX_VALUE, entity, "value");
        });
    }

    @Test
    void testIndexOutOfBoundsGetByte() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.getByte(-1, entity);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.getByte(100, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsSetByte() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.setByte(-1, entity, (byte) 42);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            fieldVarHandleInvoker.setByte(100, entity, (byte) 42);
        });
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveGetters() {
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getShort(-1, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getInt(-1, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getLong(-1, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getFloat(-1, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getDouble(-1, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getBoolean(-1, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getChar(-1, entity));

        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getShort(100, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getInt(100, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getLong(100, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getFloat(100, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getDouble(100, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getBoolean(100, entity));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.getChar(100, entity));
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveSetters() {
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setShort(-1, entity, (short) 1));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setInt(-1, entity, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setLong(-1, entity, 1L));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setFloat(-1, entity, 1.0f));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setDouble(-1, entity, 1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setBoolean(-1, entity, true));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setChar(-1, entity, 'A'));

        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setShort(100, entity, (short) 1));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setInt(100, entity, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setLong(100, entity, 1L));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setFloat(100, entity, 1.0f));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setDouble(100, entity, 1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setBoolean(100, entity, true));
        assertThrows(IndexOutOfBoundsException.class, () -> fieldVarHandleInvoker.setChar(100, entity, 'A'));
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 对于基本类型字段，VarHandle 在 null 实例上会抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldVarHandleInvoker.get(byteFieldIndex, null);
        });

        // 对于引用类型字段，VarHandle 在 null 实例上会抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldVarHandleInvoker.get(stringFieldIndex, null);
        });

        // 对于基本类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldVarHandleInvoker.set(byteFieldIndex, null, (byte) 42);
        });

        // 对于引用类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldVarHandleInvoker.set(stringFieldIndex, null, "value");
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

        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getByte(byteFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getShort(shortFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getInt(intFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getLong(longFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getFloat(floatFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getDouble(doubleFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getBoolean(booleanFieldIndex, null));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.getChar(charFieldIndex, null));
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

        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setByte(byteFieldIndex, null, (byte) 1));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setShort(shortFieldIndex, null, (short) 1));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setInt(intFieldIndex, null, 1));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setLong(longFieldIndex, null, 1L));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setFloat(floatFieldIndex, null, 1.0f));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setDouble(doubleFieldIndex, null, 1.0));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setBoolean(booleanFieldIndex, null, true));
        assertThrows(NullPointerException.class, () -> fieldVarHandleInvoker.setChar(charFieldIndex, null, 'A'));
    }

    @Test
    void testNullValueSet() {
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 对于引用类型字段，可以设置为 null
        fieldVarHandleInvoker.set(integerFieldIndex, entity, null);
        assertNull(fieldVarHandleInvoker.get(integerFieldIndex, entity));

        fieldVarHandleInvoker.set(stringFieldIndex, entity, null);
        assertNull(fieldVarHandleInvoker.get(stringFieldIndex, entity));
    }

    @Test
    void testNullValueThenSet() {
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 设置为 null 后可以重新设置
        fieldVarHandleInvoker.set(stringFieldIndex, entity, null);
        assertNull(fieldVarHandleInvoker.get(stringFieldIndex, entity));

        fieldVarHandleInvoker.set(stringFieldIndex, entity, "newValue");
        assertEquals("newValue", fieldVarHandleInvoker.get(stringFieldIndex, entity));
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
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) 1);
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) 2);
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 3);
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 4L);
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 5.0f);
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 6.0);
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, '7');

        assertEquals((byte) 1, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));
        assertEquals((short) 2, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));
        assertEquals(3, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals(4L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
        assertEquals(5.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(6.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        assertTrue(fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));
        assertEquals('7', fieldVarHandleInvoker.getChar(charFieldIndex, entity));
    }

    @Test
    void testMultipleOperations() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 多次设置和获取
        for (int i = 0; i < 10; i++) {
            fieldVarHandleInvoker.setInt(intFieldIndex, entity, i);
            assertEquals(i, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
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
            fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) i);
            assertEquals((byte) i, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));

            fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) i);
            assertEquals((short) i, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));

            fieldVarHandleInvoker.setInt(intFieldIndex, entity, i);
            assertEquals(i, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

            fieldVarHandleInvoker.setLong(longFieldIndex, entity, (long) i);
            assertEquals((long) i, fieldVarHandleInvoker.getLong(longFieldIndex, entity));

            fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, (float) i);
            assertEquals((float) i, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);

            fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, (double) i);
            assertEquals((double) i, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);

            fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, i % 2 == 0);
            assertEquals(i % 2 == 0, fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));

            fieldVarHandleInvoker.setChar(charFieldIndex, entity, (char) ('A' + i));
            assertEquals((char) ('A' + i), fieldVarHandleInvoker.getChar(charFieldIndex, entity));
        }
    }

    @Test
    void testMixedAccess() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 混合使用 Object 方法和基本类型方法
        fieldVarHandleInvoker.set(intFieldIndex, entity, 100);
        assertEquals(100, ((Integer) fieldVarHandleInvoker.get(intFieldIndex, entity)).intValue());

        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 200);
        assertEquals(200, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

        fieldVarHandleInvoker.set(intFieldIndex, entity, 300);
        assertEquals(300, ((Integer) fieldVarHandleInvoker.get(intFieldIndex, entity)).intValue());
    }

    @Test
    void testMixedAccessAllFields() {
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 混合使用 Object 方法和基本类型方法访问所有字段
        // byte
        fieldVarHandleInvoker.set(byteFieldIndex, entity, (byte) 10);
        assertEquals((byte) 10, ((Byte) fieldVarHandleInvoker.get(byteFieldIndex, entity)).byteValue());
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) 20);
        assertEquals((byte) 20, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));

        // int
        fieldVarHandleInvoker.set(intFieldIndex, entity, 100);
        assertEquals(100, ((Integer) fieldVarHandleInvoker.get(intFieldIndex, entity)).intValue());
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 200);
        assertEquals(200, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

        // long
        fieldVarHandleInvoker.set(longFieldIndex, entity, 1000L);
        assertEquals(1000L, ((Long) fieldVarHandleInvoker.get(longFieldIndex, entity)).longValue());
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 2000L);
        assertEquals(2000L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));

        // String
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "test1");
        assertEquals("test1", fieldVarHandleInvoker.get(stringFieldIndex, entity));
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidSetGet() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        // 快速连续设置和获取
        for (int i = 0; i < 1000; i++) {
            fieldVarHandleInvoker.setInt(intFieldIndex, entity, i);
            int value = fieldVarHandleInvoker.getInt(intFieldIndex, entity);
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
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) 1);
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, 'C');
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 6.0);
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 5.0f);
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 3);
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 4L);
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) 2);
        fieldVarHandleInvoker.set(integerFieldIndex, entity, 999);
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "String");

        assertTrue(fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));
        assertEquals((byte) 1, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));
        assertEquals('C', fieldVarHandleInvoker.getChar(charFieldIndex, entity));
        assertEquals(6.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        assertEquals(5.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(3, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals(4L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
        assertEquals((short) 2, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));
        assertEquals(999, fieldVarHandleInvoker.get(integerFieldIndex, entity));
        assertEquals("String", fieldVarHandleInvoker.get(stringFieldIndex, entity));
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
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "String");
        fieldVarHandleInvoker.set(integerFieldIndex, entity, 999);
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, (short) 2);
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 4L);
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 3);
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 5.0f);
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 6.0);
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, 'C');
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, (byte) 1);
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);

        assertEquals("String", fieldVarHandleInvoker.get(stringFieldIndex, entity));
        assertEquals(999, fieldVarHandleInvoker.get(integerFieldIndex, entity));
        assertEquals((short) 2, fieldVarHandleInvoker.getShort(shortFieldIndex, entity));
        assertEquals(4L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
        assertEquals(3, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals(5.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(6.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        assertEquals('C', fieldVarHandleInvoker.getChar(charFieldIndex, entity));
        assertEquals((byte) 1, fieldVarHandleInvoker.getByte(byteFieldIndex, entity));
        assertTrue(fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));
    }

    @Test
    void testAlternatingAccess() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");

        // 交替访问不同字段
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 1);
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "A");
        assertEquals(1, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals("A", fieldVarHandleInvoker.get(stringFieldIndex, entity));

        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 2);
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "B");
        assertEquals(2, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals("B", fieldVarHandleInvoker.get(stringFieldIndex, entity));

        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 3);
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "C");
        assertEquals(3, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals("C", fieldVarHandleInvoker.get(stringFieldIndex, entity));
    }

    @Test
    void testFloatPrecision() {
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        // 测试 float 精度
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, value);
            assertEquals(value, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        }
    }

    @Test
    void testDoublePrecision() {
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        // 测试 double 精度
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, value);
            assertEquals(value, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
        }
    }

    @Test
    void testCharUnicode() {
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");
        // 测试 Unicode 字符
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            fieldVarHandleInvoker.setChar(charFieldIndex, entity, c);
            assertEquals(c, fieldVarHandleInvoker.getChar(charFieldIndex, entity));
        }
    }

    @Test
    void testStringSpecialValues() {
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        // 测试 String 特殊值
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            fieldVarHandleInvoker.set(stringFieldIndex, entity, str);
            assertEquals(str, fieldVarHandleInvoker.get(stringFieldIndex, entity));
        }
    }

    @Test
    void testIntegerBoxingUnboxing() {
        int integerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("integerField");
        // 测试 Integer 装箱拆箱
        Integer[] testValues = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 100, -100};
        for (Integer value : testValues) {
            fieldVarHandleInvoker.set(integerFieldIndex, entity, value);
            Object result = fieldVarHandleInvoker.get(integerFieldIndex, entity);
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
            fieldVarHandleInvoker.setInt(intFieldIndex, entity, 42);
            assertEquals(42, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        }
    }

    @Test
    void testCrossFieldInteraction() {
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");

        // 测试字段之间的交互
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 100);
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 200L);
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 300.0f);
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 400.0);

        assertEquals(100, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals(200L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
        assertEquals(300.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(400.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);

        // 再次设置
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 500);
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 600L);
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, 700.0f);
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, 800.0);

        assertEquals(500, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        assertEquals(600L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
        assertEquals(700.0f, fieldVarHandleInvoker.getFloat(floatFieldIndex, entity), 0.0001f);
        assertEquals(800.0, fieldVarHandleInvoker.getDouble(doubleFieldIndex, entity), 0.000001);
    }

    @Test
    void testValidIndexBoundaries() {
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int stringFieldIndex = fieldInvokerHelper.getFieldGetterIndex("stringField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");

        // 测试有效索引边界
        // 第一个字段 (index 0 - booleanField)
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);
        assertTrue(fieldVarHandleInvoker.getBoolean(booleanFieldIndex, entity));

        // 最后一个字段 (index 9 - stringField)
        fieldVarHandleInvoker.set(stringFieldIndex, entity, "999");
        assertEquals("999", fieldVarHandleInvoker.get(stringFieldIndex, entity));

        // 中间字段 (index 5 - intField)
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 123);
        assertEquals(123, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
    }

    // ==================== VarHandle 特有测试 ====================

    @Test
    void testVarHandleCreation() {
        // 测试 VarHandle 的创建
        assertNotNull(fieldVarHandleInvoker);

        // 再次创建同一个类的 FieldVarHandleInvoker
        FieldVarHandleInvoker anotherInvoker = FieldVarHandleInvoker.of(TestEntity.class);
        assertNotNull(anotherInvoker);
        // 两个实例应该不同，因为内部有 VarHandle 数组
        assertNotSame(fieldVarHandleInvoker, anotherInvoker);
    }

    @Test
    void testVarHandlePerformanceConsistency() {
        // 测试 VarHandle 的性能一致性
        // 多次操作应该保持一致的结果
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        for (int i = 0; i < 10000; i++) {
            fieldVarHandleInvoker.setInt(intFieldIndex, entity, i);
            assertEquals(i, fieldVarHandleInvoker.getInt(intFieldIndex, entity));
        }
    }

    @Test
    void testVarHandleWithFinalField() {
        // 创建一个包含 final 字段的测试类
        class FinalFieldEntity {
            final int finalField = 100;
            int normalField;
        }

        FieldVarHandleInvoker finalInvoker = FieldVarHandleInvoker.of(FinalFieldEntity.class);
        FinalFieldEntity finalEntity = new FinalFieldEntity();

        // final 字段可以被读取
        assertEquals(100, finalInvoker.getInt(0, finalEntity));

        // 普通字段可以被读写
        finalInvoker.setInt(1, finalEntity, 200);
        assertEquals(200, finalInvoker.getInt(1, finalEntity));
    }

    @Test
    void testVarHandleWithMultipleInstances() {
        // 测试多个实例之间的独立性
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();

        fieldVarHandleInvoker.setInt(intFieldIndex, entity1, 100);
        fieldVarHandleInvoker.setInt(intFieldIndex, entity2, 200);

        assertEquals(100, fieldVarHandleInvoker.getInt(intFieldIndex, entity1));
        assertEquals(200, fieldVarHandleInvoker.getInt(intFieldIndex, entity2));
    }

    @Test
    void testVarHandleWithVolatileSemantics() {
        // 虽然 VarHandle 本身支持 volatile 语义，但我们的实现使用默认访问模式
        // 这里主要测试基本功能
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");

        fieldVarHandleInvoker.setInt(intFieldIndex, entity, 12345);
        assertEquals(12345, fieldVarHandleInvoker.getInt(intFieldIndex, entity));

        fieldVarHandleInvoker.setLong(longFieldIndex, entity, 67890L);
        assertEquals(67890L, fieldVarHandleInvoker.getLong(longFieldIndex, entity));
    }

    @Test
    void testVarHandleTypeConsistency() {
        // 测试 VarHandle 的类型一致性
        int byteFieldIndex = fieldInvokerHelper.getFieldGetterIndex("byteField");
        int shortFieldIndex = fieldInvokerHelper.getFieldGetterIndex("shortField");
        int intFieldIndex = fieldInvokerHelper.getFieldGetterIndex("intField");
        int longFieldIndex = fieldInvokerHelper.getFieldGetterIndex("longField");
        int floatFieldIndex = fieldInvokerHelper.getFieldGetterIndex("floatField");
        int doubleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("doubleField");
        int booleanFieldIndex = fieldInvokerHelper.getFieldGetterIndex("booleanField");
        int charFieldIndex = fieldInvokerHelper.getFieldGetterIndex("charField");

        // 设置值
        fieldVarHandleInvoker.setByte(byteFieldIndex, entity, Byte.MAX_VALUE);
        fieldVarHandleInvoker.setShort(shortFieldIndex, entity, Short.MAX_VALUE);
        fieldVarHandleInvoker.setInt(intFieldIndex, entity, Integer.MAX_VALUE);
        fieldVarHandleInvoker.setLong(longFieldIndex, entity, Long.MAX_VALUE);
        fieldVarHandleInvoker.setFloat(floatFieldIndex, entity, Float.MAX_VALUE);
        fieldVarHandleInvoker.setDouble(doubleFieldIndex, entity, Double.MAX_VALUE);
        fieldVarHandleInvoker.setBoolean(booleanFieldIndex, entity, true);
        fieldVarHandleInvoker.setChar(charFieldIndex, entity, Character.MAX_VALUE);

        // 获取值并验证类型和值
        Object byteValue = fieldVarHandleInvoker.get(byteFieldIndex, entity);
        Object shortValue = fieldVarHandleInvoker.get(shortFieldIndex, entity);
        Object intValue = fieldVarHandleInvoker.get(intFieldIndex, entity);
        Object longValue = fieldVarHandleInvoker.get(longFieldIndex, entity);
        Object floatValue = fieldVarHandleInvoker.get(floatFieldIndex, entity);
        Object doubleValue = fieldVarHandleInvoker.get(doubleFieldIndex, entity);
        Object booleanValue = fieldVarHandleInvoker.get(booleanFieldIndex, entity);
        Object charValue = fieldVarHandleInvoker.get(charFieldIndex, entity);

        assertTrue(byteValue instanceof Byte);
        assertTrue(shortValue instanceof Short);
        assertTrue(intValue instanceof Integer);
        assertTrue(longValue instanceof Long);
        assertTrue(floatValue instanceof Float);
        assertTrue(doubleValue instanceof Double);
        assertTrue(booleanValue instanceof Boolean);
        assertTrue(charValue instanceof Character);
    }

}
