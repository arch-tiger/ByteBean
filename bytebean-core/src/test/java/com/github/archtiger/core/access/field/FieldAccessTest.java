package com.github.archtiger.core.access.field;

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
class FieldAccessTest {

    private FieldAccess fieldAccess;
    private TestEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        Class<? extends FieldAccess> accessClass = FieldAccessGenerator.generate(TestEntity.class);
        fieldAccess = accessClass.getDeclaredConstructor().newInstance();
        entity = new TestEntity();
    }

    // ==================== 通用 get/set 方法测试 ====================

    @Test
    void testGetSetObject() {
        // 测试 String 字段
        fieldAccess.set(8, entity, "testString");
        assertEquals("testString", fieldAccess.get(8, entity));

        // 测试 Integer 字段
        fieldAccess.set(9, entity, 42);
        assertEquals(42, fieldAccess.get(9, entity));
    }

    @Test
    void testGetSetPrimitiveAsObject() {
        // 测试基本类型字段通过 Object 方法访问（会装箱）
        fieldAccess.set(2, entity, 123);
        Object value = fieldAccess.get(2, entity);
        assertNotNull(value);
        assertEquals(123, ((Integer) value).intValue());
    }

    @Test
    void testGetSetAllFields() {
        // 测试所有字段的 get/set
        fieldAccess.set(0, entity, (byte) 10);
        fieldAccess.set(1, entity, (short) 20);
        fieldAccess.set(2, entity, 30);
        fieldAccess.set(3, entity, 40L);
        fieldAccess.set(4, entity, 50.0f);
        fieldAccess.set(5, entity, 60.0);
        fieldAccess.set(6, entity, true);
        fieldAccess.set(7, entity, 'X');
        fieldAccess.set(8, entity, "test");
        fieldAccess.set(9, entity, 100);

        assertEquals((byte) 10, fieldAccess.get(0, entity));
        assertEquals((short) 20, fieldAccess.get(1, entity));
        assertEquals(30, fieldAccess.get(2, entity));
        assertEquals(40L, fieldAccess.get(3, entity));
        assertEquals(50.0f, ((Float) fieldAccess.get(4, entity)).floatValue(), 0.0001f);
        assertEquals(60.0, ((Double) fieldAccess.get(5, entity)).doubleValue(), 0.000001);
        assertEquals(true, fieldAccess.get(6, entity));
        assertEquals('X', fieldAccess.get(7, entity));
        assertEquals("test", fieldAccess.get(8, entity));
        assertEquals(100, fieldAccess.get(9, entity));
    }

    // ==================== 基本类型 getter 测试 ====================

    @Test
    void testGetByte() {
        entity.byteField = (byte) 42;
        byte value = fieldAccess.getByte(0, entity);
        assertEquals((byte) 42, value);
    }

    @Test
    void testGetShort() {
        entity.shortField = (short) 12345;
        short value = fieldAccess.getShort(1, entity);
        assertEquals((short) 12345, value);
    }

    @Test
    void testGetInt() {
        entity.intField = 123456;
        int value = fieldAccess.getInt(2, entity);
        assertEquals(123456, value);
    }

    @Test
    void testGetLong() {
        entity.longField = 123456789L;
        long value = fieldAccess.getLong(3, entity);
        assertEquals(123456789L, value);
    }

    @Test
    void testGetFloat() {
        entity.floatField = 3.14f;
        float value = fieldAccess.getFloat(4, entity);
        assertEquals(3.14f, value, 0.0001f);
    }

    @Test
    void testGetDouble() {
        entity.doubleField = 2.71828;
        double value = fieldAccess.getDouble(5, entity);
        assertEquals(2.71828, value, 0.000001);
    }

    @Test
    void testGetBoolean() {
        entity.booleanField = true;
        boolean value = fieldAccess.getBoolean(6, entity);
        assertTrue(value);

        entity.booleanField = false;
        value = fieldAccess.getBoolean(6, entity);
        assertFalse(value);
    }

    @Test
    void testGetChar() {
        entity.charField = 'A';
        char value = fieldAccess.getChar(7, entity);
        assertEquals('A', value);
    }

    // ==================== 基本类型 setter 测试 ====================

    @Test
    void testSetByte() {
        fieldAccess.setByte(0, entity, (byte) 42);
        assertEquals((byte) 42, entity.byteField);
    }

    @Test
    void testSetShort() {
        fieldAccess.setShort(1, entity, (short) 12345);
        assertEquals((short) 12345, entity.shortField);
    }

    @Test
    void testSetInt() {
        fieldAccess.setInt(2, entity, 123456);
        assertEquals(123456, entity.intField);
    }

    @Test
    void testSetLong() {
        fieldAccess.setLong(3, entity, 123456789L);
        assertEquals(123456789L, entity.longField);
    }

    @Test
    void testSetFloat() {
        fieldAccess.setFloat(4, entity, 3.14f);
        assertEquals(3.14f, entity.floatField, 0.0001f);
    }

    @Test
    void testSetDouble() {
        fieldAccess.setDouble(5, entity, 2.71828);
        assertEquals(2.71828, entity.doubleField, 0.000001);
    }

    @Test
    void testSetBoolean() {
        fieldAccess.setBoolean(6, entity, true);
        assertTrue(entity.booleanField);

        fieldAccess.setBoolean(6, entity, false);
        assertFalse(entity.booleanField);
    }

    @Test
    void testSetChar() {
        fieldAccess.setChar(7, entity, 'Z');
        assertEquals('Z', entity.charField);
    }

    // ==================== 边界值测试 ====================

    @Test
    void testByteBoundaryValues() {
        // byte 最小值
        fieldAccess.setByte(0, entity, Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, fieldAccess.getByte(0, entity));

        // byte 最大值
        fieldAccess.setByte(0, entity, Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, fieldAccess.getByte(0, entity));

        // byte 零值
        fieldAccess.setByte(0, entity, (byte) 0);
        assertEquals((byte) 0, fieldAccess.getByte(0, entity));

        // byte -1
        fieldAccess.setByte(0, entity, (byte) -1);
        assertEquals((byte) -1, fieldAccess.getByte(0, entity));
    }

    @Test
    void testShortBoundaryValues() {
        // short 最小值
        fieldAccess.setShort(1, entity, Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, fieldAccess.getShort(1, entity));

        // short 最大值
        fieldAccess.setShort(1, entity, Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, fieldAccess.getShort(1, entity));

        // short 零值
        fieldAccess.setShort(1, entity, (short) 0);
        assertEquals((short) 0, fieldAccess.getShort(1, entity));

        // short -1
        fieldAccess.setShort(1, entity, (short) -1);
        assertEquals((short) -1, fieldAccess.getShort(1, entity));
    }

    @Test
    void testIntBoundaryValues() {
        // int 最小值
        fieldAccess.setInt(2, entity, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, fieldAccess.getInt(2, entity));

        // int 最大值
        fieldAccess.setInt(2, entity, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fieldAccess.getInt(2, entity));

        // int 零值
        fieldAccess.setInt(2, entity, 0);
        assertEquals(0, fieldAccess.getInt(2, entity));

        // int -1
        fieldAccess.setInt(2, entity, -1);
        assertEquals(-1, fieldAccess.getInt(2, entity));
    }

    @Test
    void testLongBoundaryValues() {
        // long 最小值
        fieldAccess.setLong(3, entity, Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, fieldAccess.getLong(3, entity));

        // long 最大值
        fieldAccess.setLong(3, entity, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, fieldAccess.getLong(3, entity));

        // long 零值
        fieldAccess.setLong(3, entity, 0L);
        assertEquals(0L, fieldAccess.getLong(3, entity));

        // long -1
        fieldAccess.setLong(3, entity, -1L);
        assertEquals(-1L, fieldAccess.getLong(3, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        // float 最小值
        fieldAccess.setFloat(4, entity, Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, fieldAccess.getFloat(4, entity));

        // float 最大值
        fieldAccess.setFloat(4, entity, Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, fieldAccess.getFloat(4, entity));

        // float 正无穷
        fieldAccess.setFloat(4, entity, Float.POSITIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldAccess.getFloat(4, entity)));
        assertTrue(fieldAccess.getFloat(4, entity) > 0);

        // float 负无穷
        fieldAccess.setFloat(4, entity, Float.NEGATIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldAccess.getFloat(4, entity)));
        assertTrue(fieldAccess.getFloat(4, entity) < 0);

        // float NaN
        fieldAccess.setFloat(4, entity, Float.NaN);
        assertTrue(Float.isNaN(fieldAccess.getFloat(4, entity)));

        // float 零值
        fieldAccess.setFloat(4, entity, 0.0f);
        assertEquals(0.0f, fieldAccess.getFloat(4, entity), 0.0f);

        // float 负零
        fieldAccess.setFloat(4, entity, -0.0f);
        assertEquals(-0.0f, fieldAccess.getFloat(4, entity), 0.0f);

        // float 最小正数
        fieldAccess.setFloat(4, entity, Float.MIN_NORMAL);
        assertEquals(Float.MIN_NORMAL, fieldAccess.getFloat(4, entity));
    }

    @Test
    void testDoubleBoundaryValues() {
        // double 最小值
        fieldAccess.setDouble(5, entity, Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, fieldAccess.getDouble(5, entity));

        // double 最大值
        fieldAccess.setDouble(5, entity, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, fieldAccess.getDouble(5, entity));

        // double 正无穷
        fieldAccess.setDouble(5, entity, Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldAccess.getDouble(5, entity)));
        assertTrue(fieldAccess.getDouble(5, entity) > 0);

        // double 负无穷
        fieldAccess.setDouble(5, entity, Double.NEGATIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldAccess.getDouble(5, entity)));
        assertTrue(fieldAccess.getDouble(5, entity) < 0);

        // double NaN
        fieldAccess.setDouble(5, entity, Double.NaN);
        assertTrue(Double.isNaN(fieldAccess.getDouble(5, entity)));

        // double 零值
        fieldAccess.setDouble(5, entity, 0.0);
        assertEquals(0.0, fieldAccess.getDouble(5, entity), 0.0);

        // double 负零
        fieldAccess.setDouble(5, entity, -0.0);
        assertEquals(-0.0, fieldAccess.getDouble(5, entity), 0.0);

        // double 最小正数
        fieldAccess.setDouble(5, entity, Double.MIN_NORMAL);
        assertEquals(Double.MIN_NORMAL, fieldAccess.getDouble(5, entity));
    }

    @Test
    void testCharBoundaryValues() {
        // char 最小值
        fieldAccess.setChar(7, entity, Character.MIN_VALUE);
        assertEquals(Character.MIN_VALUE, fieldAccess.getChar(7, entity));

        // char 最大值
        fieldAccess.setChar(7, entity, Character.MAX_VALUE);
        assertEquals(Character.MAX_VALUE, fieldAccess.getChar(7, entity));

        // char 零值
        fieldAccess.setChar(7, entity, '\u0000');
        assertEquals('\u0000', fieldAccess.getChar(7, entity));

        // char 常用字符
        fieldAccess.setChar(7, entity, 'A');
        assertEquals('A', fieldAccess.getChar(7, entity));

        fieldAccess.setChar(7, entity, '中');
        assertEquals('中', fieldAccess.getChar(7, entity));
    }

    @Test
    void testBooleanBoundaryValues() {
        // boolean true
        fieldAccess.setBoolean(6, entity, true);
        assertTrue(fieldAccess.getBoolean(6, entity));

        // boolean false
        fieldAccess.setBoolean(6, entity, false);
        assertFalse(fieldAccess.getBoolean(6, entity));
    }

    // ==================== 索引越界异常测试 ====================

    @Test
    void testIndexOutOfBoundsGet() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.get(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.get(100, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.get(Integer.MIN_VALUE, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.get(Integer.MAX_VALUE, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsSet() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.set(-1, entity, "value");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.set(100, entity, "value");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.set(Integer.MIN_VALUE, entity, "value");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.set(Integer.MAX_VALUE, entity, "value");
        });
    }

    @Test
    void testIndexOutOfBoundsGetByte() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getByte(-1, entity);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getByte(100, entity);
        });
    }

    @Test
    void testIndexOutOfBoundsSetByte() {
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.setByte(-1, entity, (byte) 42);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.setByte(100, entity, (byte) 42);
        });
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveGetters() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getShort(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getInt(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getLong(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getFloat(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getDouble(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getBoolean(-1, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getChar(-1, entity));

        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getShort(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getInt(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getLong(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getFloat(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getDouble(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getBoolean(100, entity));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getChar(100, entity));
    }

    @Test
    void testIndexOutOfBoundsAllPrimitiveSetters() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setShort(-1, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setInt(-1, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setLong(-1, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setFloat(-1, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setDouble(-1, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setBoolean(-1, entity, true));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setChar(-1, entity, 'A'));

        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setShort(100, entity, (short) 1));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setInt(100, entity, 1));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setLong(100, entity, 1L));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setFloat(100, entity, 1.0f));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setDouble(100, entity, 1.0));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setBoolean(100, entity, true));
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setChar(100, entity, 'A'));
    }

    // ==================== 类型不匹配异常测试 ====================

    @Test
    void testTypeMismatchGetByte() {
        // 尝试使用 getByte 访问非 byte 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getByte(2, entity); // index 2 是 int 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getByte(8, entity); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchGetShort() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getShort(0, entity)); // byte
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getShort(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getShort(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetInt() {
        // 尝试使用 getInt 访问非 int 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getInt(0, entity); // index 0 是 byte 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getInt(8, entity); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchGetLong() {
        // 尝试使用 getLong 访问非 long 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getLong(2, entity); // index 2 是 int 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.getLong(8, entity); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchGetFloat() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getFloat(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getFloat(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetDouble() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getDouble(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getDouble(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetBoolean() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getBoolean(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getBoolean(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetChar() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getChar(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.getChar(8, entity)); // String
    }

    @Test
    void testTypeMismatchSetByte() {
        // 尝试使用 setByte 设置非 byte 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.setByte(2, entity, (byte) 42); // index 2 是 int 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.setByte(8, entity, (byte) 42); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchSetShort() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setShort(0, entity, (short) 1)); // byte
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setShort(2, entity, (short) 1)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setShort(8, entity, (short) 1)); // String
    }

    @Test
    void testTypeMismatchSetInt() {
        // 尝试使用 setInt 设置非 int 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.setInt(0, entity, 42); // index 0 是 byte 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldAccess.setInt(8, entity, 42); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchSetLong() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setLong(2, entity, 1L)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setLong(8, entity, 1L)); // String
    }

    @Test
    void testTypeMismatchSetFloat() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setFloat(2, entity, 1.0f)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setFloat(8, entity, 1.0f)); // String
    }

    @Test
    void testTypeMismatchSetDouble() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setDouble(2, entity, 1.0)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setDouble(8, entity, 1.0)); // String
    }

    @Test
    void testTypeMismatchSetBoolean() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setBoolean(2, entity, true)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setBoolean(8, entity, true)); // String
    }

    @Test
    void testTypeMismatchSetChar() {
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setChar(2, entity, 'A')); // int
        assertThrows(IllegalArgumentException.class, () -> fieldAccess.setChar(8, entity, 'A')); // String
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        // 对于基本类型字段，GETFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldAccess.get(0, null); // index 0 是 byte 类型字段
        });

        // 对于引用类型字段，GETFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldAccess.get(8, null); // index 8 是 String 类型字段
        });

        // 对于基本类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        // 使用正确的类型（byte）来避免类型不匹配
        assertThrows(NullPointerException.class, () -> {
            fieldAccess.set(0, null, (byte) 42); // index 0 是 byte 类型字段
        });

        // 对于引用类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldAccess.set(8, null, "value"); // index 8 是 String 类型字段
        });
    }

    @Test
    void testNullInstanceAllPrimitiveGetters() {
        assertThrows(NullPointerException.class, () -> fieldAccess.getByte(0, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getShort(1, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getInt(2, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getLong(3, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getFloat(4, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getDouble(5, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getBoolean(6, null));
        assertThrows(NullPointerException.class, () -> fieldAccess.getChar(7, null));
    }

    @Test
    void testNullInstanceAllPrimitiveSetters() {
        assertThrows(NullPointerException.class, () -> fieldAccess.setByte(0, null, (byte) 1));
        assertThrows(NullPointerException.class, () -> fieldAccess.setShort(1, null, (short) 1));
        assertThrows(NullPointerException.class, () -> fieldAccess.setInt(2, null, 1));
        assertThrows(NullPointerException.class, () -> fieldAccess.setLong(3, null, 1L));
        assertThrows(NullPointerException.class, () -> fieldAccess.setFloat(4, null, 1.0f));
        assertThrows(NullPointerException.class, () -> fieldAccess.setDouble(5, null, 1.0));
        assertThrows(NullPointerException.class, () -> fieldAccess.setBoolean(6, null, true));
        assertThrows(NullPointerException.class, () -> fieldAccess.setChar(7, null, 'A'));
    }

    @Test
    void testNullValueSet() {
        // 对于引用类型字段，可以设置为 null
        fieldAccess.set(8, entity, null);
        assertNull(fieldAccess.get(8, entity));

        fieldAccess.set(9, entity, null);
        assertNull(fieldAccess.get(9, entity));
    }

    @Test
    void testNullValueThenSet() {
        // 设置为 null 后可以重新设置
        fieldAccess.set(8, entity, null);
        assertNull(fieldAccess.get(8, entity));

        fieldAccess.set(8, entity, "newValue");
        assertEquals("newValue", fieldAccess.get(8, entity));
    }

    // ==================== 组合测试 ====================

    @Test
    void testAllPrimitiveTypes() {
        // 测试所有基本类型的 set 和 get
        fieldAccess.setByte(0, entity, (byte) 1);
        fieldAccess.setShort(1, entity, (short) 2);
        fieldAccess.setInt(2, entity, 3);
        fieldAccess.setLong(3, entity, 4L);
        fieldAccess.setFloat(4, entity, 5.0f);
        fieldAccess.setDouble(5, entity, 6.0);
        fieldAccess.setBoolean(6, entity, true);
        fieldAccess.setChar(7, entity, '7');

        assertEquals((byte) 1, fieldAccess.getByte(0, entity));
        assertEquals((short) 2, fieldAccess.getShort(1, entity));
        assertEquals(3, fieldAccess.getInt(2, entity));
        assertEquals(4L, fieldAccess.getLong(3, entity));
        assertEquals(5.0f, fieldAccess.getFloat(4, entity), 0.0001f);
        assertEquals(6.0, fieldAccess.getDouble(5, entity), 0.000001);
        assertTrue(fieldAccess.getBoolean(6, entity));
        assertEquals('7', fieldAccess.getChar(7, entity));
    }

    @Test
    void testMultipleOperations() {
        // 多次设置和获取
        for (int i = 0; i < 10; i++) {
            fieldAccess.setInt(2, entity, i);
            assertEquals(i, fieldAccess.getInt(2, entity));
        }
    }

    @Test
    void testMultipleOperationsAllTypes() {
        // 对所有基本类型进行多次操作
        for (int i = 0; i < 5; i++) {
            fieldAccess.setByte(0, entity, (byte) i);
            assertEquals((byte) i, fieldAccess.getByte(0, entity));

            fieldAccess.setShort(1, entity, (short) i);
            assertEquals((short) i, fieldAccess.getShort(1, entity));

            fieldAccess.setInt(2, entity, i);
            assertEquals(i, fieldAccess.getInt(2, entity));

            fieldAccess.setLong(3, entity, (long) i);
            assertEquals((long) i, fieldAccess.getLong(3, entity));

            fieldAccess.setFloat(4, entity, (float) i);
            assertEquals((float) i, fieldAccess.getFloat(4, entity), 0.0001f);

            fieldAccess.setDouble(5, entity, (double) i);
            assertEquals((double) i, fieldAccess.getDouble(5, entity), 0.000001);

            fieldAccess.setBoolean(6, entity, i % 2 == 0);
            assertEquals(i % 2 == 0, fieldAccess.getBoolean(6, entity));

            fieldAccess.setChar(7, entity, (char) ('A' + i));
            assertEquals((char) ('A' + i), fieldAccess.getChar(7, entity));
        }
    }

    @Test
    void testMixedAccess() {
        // 混合使用 Object 方法和基本类型方法
        fieldAccess.set(2, entity, 100);
        assertEquals(100, ((Integer) fieldAccess.get(2, entity)).intValue());

        fieldAccess.setInt(2, entity, 200);
        assertEquals(200, fieldAccess.getInt(2, entity));

        fieldAccess.set(2, entity, 300);
        assertEquals(300, ((Integer) fieldAccess.get(2, entity)).intValue());
    }

    @Test
    void testMixedAccessAllFields() {
        // 混合使用 Object 方法和基本类型方法访问所有字段
        // byte
        fieldAccess.set(0, entity, (byte) 10);
        assertEquals((byte) 10, ((Byte) fieldAccess.get(0, entity)).byteValue());
        fieldAccess.setByte(0, entity, (byte) 20);
        assertEquals((byte) 20, fieldAccess.getByte(0, entity));

        // int
        fieldAccess.set(2, entity, 100);
        assertEquals(100, ((Integer) fieldAccess.get(2, entity)).intValue());
        fieldAccess.setInt(2, entity, 200);
        assertEquals(200, fieldAccess.getInt(2, entity));

        // long
        fieldAccess.set(3, entity, 1000L);
        assertEquals(1000L, ((Long) fieldAccess.get(3, entity)).longValue());
        fieldAccess.setLong(3, entity, 2000L);
        assertEquals(2000L, fieldAccess.getLong(3, entity));

        // String
        fieldAccess.set(8, entity, "test1");
        assertEquals("test1", fieldAccess.get(8, entity));
        // String 字段没有对应的基本类型方法，只能使用 Object 方法
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidSetGet() {
        // 快速连续设置和获取
        for (int i = 0; i < 1000; i++) {
            fieldAccess.setInt(2, entity, i);
            int value = fieldAccess.getInt(2, entity);
            assertEquals(i, value);
        }
    }

    @Test
    void testAllFieldsSequential() {
        // 顺序访问所有字段
        fieldAccess.setByte(0, entity, (byte) 1);
        fieldAccess.setShort(1, entity, (short) 2);
        fieldAccess.setInt(2, entity, 3);
        fieldAccess.setLong(3, entity, 4L);
        fieldAccess.setFloat(4, entity, 5.0f);
        fieldAccess.setDouble(5, entity, 6.0);
        fieldAccess.setBoolean(6, entity, true);
        fieldAccess.setChar(7, entity, 'C');
        fieldAccess.set(8, entity, "String");
        fieldAccess.set(9, entity, 999);

        assertEquals((byte) 1, fieldAccess.getByte(0, entity));
        assertEquals((short) 2, fieldAccess.getShort(1, entity));
        assertEquals(3, fieldAccess.getInt(2, entity));
        assertEquals(4L, fieldAccess.getLong(3, entity));
        assertEquals(5.0f, fieldAccess.getFloat(4, entity), 0.0001f);
        assertEquals(6.0, fieldAccess.getDouble(5, entity), 0.000001);
        assertTrue(fieldAccess.getBoolean(6, entity));
        assertEquals('C', fieldAccess.getChar(7, entity));
        assertEquals("String", fieldAccess.get(8, entity));
        assertEquals(999, fieldAccess.get(9, entity));
    }

    @Test
    void testAllFieldsReverse() {
        // 反向访问所有字段
        fieldAccess.set(9, entity, 999);
        fieldAccess.set(8, entity, "String");
        fieldAccess.setChar(7, entity, 'C');
        fieldAccess.setBoolean(6, entity, true);
        fieldAccess.setDouble(5, entity, 6.0);
        fieldAccess.setFloat(4, entity, 5.0f);
        fieldAccess.setLong(3, entity, 4L);
        fieldAccess.setInt(2, entity, 3);
        fieldAccess.setShort(1, entity, (short) 2);
        fieldAccess.setByte(0, entity, (byte) 1);

        assertEquals(999, fieldAccess.get(9, entity));
        assertEquals("String", fieldAccess.get(8, entity));
        assertEquals('C', fieldAccess.getChar(7, entity));
        assertTrue(fieldAccess.getBoolean(6, entity));
        assertEquals(6.0, fieldAccess.getDouble(5, entity), 0.000001);
        assertEquals(5.0f, fieldAccess.getFloat(4, entity), 0.0001f);
        assertEquals(4L, fieldAccess.getLong(3, entity));
        assertEquals(3, fieldAccess.getInt(2, entity));
        assertEquals((short) 2, fieldAccess.getShort(1, entity));
        assertEquals((byte) 1, fieldAccess.getByte(0, entity));
    }

    @Test
    void testAlternatingAccess() {
        // 交替访问不同字段
        fieldAccess.setInt(2, entity, 1);
        fieldAccess.set(8, entity, "A");
        assertEquals(1, fieldAccess.getInt(2, entity));
        assertEquals("A", fieldAccess.get(8, entity));

        fieldAccess.setInt(2, entity, 2);
        fieldAccess.set(8, entity, "B");
        assertEquals(2, fieldAccess.getInt(2, entity));
        assertEquals("B", fieldAccess.get(8, entity));

        fieldAccess.setInt(2, entity, 3);
        fieldAccess.set(8, entity, "C");
        assertEquals(3, fieldAccess.getInt(2, entity));
        assertEquals("C", fieldAccess.get(8, entity));
    }

    @Test
    void testFloatPrecision() {
        // 测试 float 精度
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            fieldAccess.setFloat(4, entity, value);
            assertEquals(value, fieldAccess.getFloat(4, entity), 0.0001f);
        }
    }

    @Test
    void testDoublePrecision() {
        // 测试 double 精度
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            fieldAccess.setDouble(5, entity, value);
            assertEquals(value, fieldAccess.getDouble(5, entity), 0.000001);
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            fieldAccess.setChar(7, entity, c);
            assertEquals(c, fieldAccess.getChar(7, entity));
        }
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            fieldAccess.set(8, entity, str);
            assertEquals(str, fieldAccess.get(8, entity));
        }
    }

    @Test
    void testIntegerBoxingUnboxing() {
        // 测试 Integer 装箱拆箱
        Integer[] testValues = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 100, -100};
        for (Integer value : testValues) {
            fieldAccess.set(9, entity, value);
            Object result = fieldAccess.get(9, entity);
            assertNotNull(result);
            assertEquals(value, result);
            assertTrue(result instanceof Integer);
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        for (int i = 0; i < 100; i++) {
            fieldAccess.setInt(2, entity, 42);
            assertEquals(42, fieldAccess.getInt(2, entity));
        }
    }

    @Test
    void testCrossFieldInteraction() {
        // 测试字段之间的交互
        fieldAccess.setInt(2, entity, 100);
        fieldAccess.setLong(3, entity, 200L);
        fieldAccess.setFloat(4, entity, 300.0f);
        fieldAccess.setDouble(5, entity, 400.0);

        assertEquals(100, fieldAccess.getInt(2, entity));
        assertEquals(200L, fieldAccess.getLong(3, entity));
        assertEquals(300.0f, fieldAccess.getFloat(4, entity), 0.0001f);
        assertEquals(400.0, fieldAccess.getDouble(5, entity), 0.000001);

        // 再次设置
        fieldAccess.setInt(2, entity, 500);
        fieldAccess.setLong(3, entity, 600L);
        fieldAccess.setFloat(4, entity, 700.0f);
        fieldAccess.setDouble(5, entity, 800.0);

        assertEquals(500, fieldAccess.getInt(2, entity));
        assertEquals(600L, fieldAccess.getLong(3, entity));
        assertEquals(700.0f, fieldAccess.getFloat(4, entity), 0.0001f);
        assertEquals(800.0, fieldAccess.getDouble(5, entity), 0.000001);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        // 第一个字段 (index 0)
        fieldAccess.setByte(0, entity, (byte) 1);
        assertEquals((byte) 1, fieldAccess.getByte(0, entity));

        // 最后一个字段 (index 9)
        fieldAccess.set(9, entity, 999);
        assertEquals(999, fieldAccess.get(9, entity));

        // 中间字段 (index 5)
        fieldAccess.setDouble(5, entity, 123.456);
        assertEquals(123.456, fieldAccess.getDouble(5, entity), 0.000001);
    }

    @Test
    void testExceptionMessages() {
        // 测试异常消息
        try {
            fieldAccess.get(-1, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid field index") || e.getMessage().contains("index"));
        }

        try {
            fieldAccess.getInt(0, entity); // index 0 是 byte，不是 int
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid field index") || e.getMessage().contains("index"));
        }
    }
}
