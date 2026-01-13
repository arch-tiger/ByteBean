package com.github.archtiger.core.invoker.field;

import com.github.archtiger.core.model.FieldInvokerResult;
import com.github.archtiger.definition.field.FieldInvoker;
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

    private FieldInvoker fieldInvoker;
    private TestEntity entity;

    @BeforeEach
    void setUp() throws Exception {
        FieldInvokerResult accessInfo = FieldInvokerGenerator.generate(TestEntity.class);
        fieldInvoker = accessInfo.fieldAccessClass().getDeclaredConstructor().newInstance();
        entity = new TestEntity();
    }

    // ==================== 通用 get/set 方法测试 ====================

    @Test
    void testGetSetObject() {
        // 测试 String 字段
        fieldInvoker.set(8, entity, "testString");
        assertEquals("testString", fieldInvoker.get(8, entity));

        // 测试 Integer 字段
        fieldInvoker.set(9, entity, 42);
        assertEquals(42, fieldInvoker.get(9, entity));
    }

    @Test
    void testGetSetPrimitiveAsObject() {
        // 测试基本类型字段通过 Object 方法访问（会装箱）
        fieldInvoker.set(2, entity, 123);
        Object value = fieldInvoker.get(2, entity);
        assertNotNull(value);
        assertEquals(123, ((Integer) value).intValue());
    }

    @Test
    void testGetSetAllFields() {
        // 测试所有字段的 get/set
        fieldInvoker.set(0, entity, (byte) 10);
        fieldInvoker.set(1, entity, (short) 20);
        fieldInvoker.set(2, entity, 30);
        fieldInvoker.set(3, entity, 40L);
        fieldInvoker.set(4, entity, 50.0f);
        fieldInvoker.set(5, entity, 60.0);
        fieldInvoker.set(6, entity, true);
        fieldInvoker.set(7, entity, 'X');
        fieldInvoker.set(8, entity, "test");
        fieldInvoker.set(9, entity, 100);

        assertEquals((byte) 10, fieldInvoker.get(0, entity));
        assertEquals((short) 20, fieldInvoker.get(1, entity));
        assertEquals(30, fieldInvoker.get(2, entity));
        assertEquals(40L, fieldInvoker.get(3, entity));
        assertEquals(50.0f, ((Float) fieldInvoker.get(4, entity)).floatValue(), 0.0001f);
        assertEquals(60.0, ((Double) fieldInvoker.get(5, entity)).doubleValue(), 0.000001);
        assertEquals(true, fieldInvoker.get(6, entity));
        assertEquals('X', fieldInvoker.get(7, entity));
        assertEquals("test", fieldInvoker.get(8, entity));
        assertEquals(100, fieldInvoker.get(9, entity));
    }

    // ==================== 基本类型 getter 测试 ====================

    @Test
    void testGetByte() {
        entity.byteField = (byte) 42;
        byte value = fieldInvoker.getByte(0, entity);
        assertEquals((byte) 42, value);
    }

    @Test
    void testGetShort() {
        entity.shortField = (short) 12345;
        short value = fieldInvoker.getShort(1, entity);
        assertEquals((short) 12345, value);
    }

    @Test
    void testGetInt() {
        entity.intField = 123456;
        int value = fieldInvoker.getInt(2, entity);
        assertEquals(123456, value);
    }

    @Test
    void testGetLong() {
        entity.longField = 123456789L;
        long value = fieldInvoker.getLong(3, entity);
        assertEquals(123456789L, value);
    }

    @Test
    void testGetFloat() {
        entity.floatField = 3.14f;
        float value = fieldInvoker.getFloat(4, entity);
        assertEquals(3.14f, value, 0.0001f);
    }

    @Test
    void testGetDouble() {
        entity.doubleField = 2.71828;
        double value = fieldInvoker.getDouble(5, entity);
        assertEquals(2.71828, value, 0.000001);
    }

    @Test
    void testGetBoolean() {
        entity.booleanField = true;
        boolean value = fieldInvoker.getBoolean(6, entity);
        assertTrue(value);

        entity.booleanField = false;
        value = fieldInvoker.getBoolean(6, entity);
        assertFalse(value);
    }

    @Test
    void testGetChar() {
        entity.charField = 'A';
        char value = fieldInvoker.getChar(7, entity);
        assertEquals('A', value);
    }

    // ==================== 基本类型 setter 测试 ====================

    @Test
    void testSetByte() {
        fieldInvoker.setByte(0, entity, (byte) 42);
        assertEquals((byte) 42, entity.byteField);
    }

    @Test
    void testSetShort() {
        fieldInvoker.setShort(1, entity, (short) 12345);
        assertEquals((short) 12345, entity.shortField);
    }

    @Test
    void testSetInt() {
        fieldInvoker.setInt(2, entity, 123456);
        assertEquals(123456, entity.intField);
    }

    @Test
    void testSetLong() {
        fieldInvoker.setLong(3, entity, 123456789L);
        assertEquals(123456789L, entity.longField);
    }

    @Test
    void testSetFloat() {
        fieldInvoker.setFloat(4, entity, 3.14f);
        assertEquals(3.14f, entity.floatField, 0.0001f);
    }

    @Test
    void testSetDouble() {
        fieldInvoker.setDouble(5, entity, 2.71828);
        assertEquals(2.71828, entity.doubleField, 0.000001);
    }

    @Test
    void testSetBoolean() {
        fieldInvoker.setBoolean(6, entity, true);
        assertTrue(entity.booleanField);

        fieldInvoker.setBoolean(6, entity, false);
        assertFalse(entity.booleanField);
    }

    @Test
    void testSetChar() {
        fieldInvoker.setChar(7, entity, 'Z');
        assertEquals('Z', entity.charField);
    }

    // ==================== 边界值测试 ====================

    @Test
    void testByteBoundaryValues() {
        // byte 最小值
        fieldInvoker.setByte(0, entity, Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, fieldInvoker.getByte(0, entity));

        // byte 最大值
        fieldInvoker.setByte(0, entity, Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, fieldInvoker.getByte(0, entity));

        // byte 零值
        fieldInvoker.setByte(0, entity, (byte) 0);
        assertEquals((byte) 0, fieldInvoker.getByte(0, entity));

        // byte -1
        fieldInvoker.setByte(0, entity, (byte) -1);
        assertEquals((byte) -1, fieldInvoker.getByte(0, entity));
    }

    @Test
    void testShortBoundaryValues() {
        // short 最小值
        fieldInvoker.setShort(1, entity, Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, fieldInvoker.getShort(1, entity));

        // short 最大值
        fieldInvoker.setShort(1, entity, Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, fieldInvoker.getShort(1, entity));

        // short 零值
        fieldInvoker.setShort(1, entity, (short) 0);
        assertEquals((short) 0, fieldInvoker.getShort(1, entity));

        // short -1
        fieldInvoker.setShort(1, entity, (short) -1);
        assertEquals((short) -1, fieldInvoker.getShort(1, entity));
    }

    @Test
    void testIntBoundaryValues() {
        // int 最小值
        fieldInvoker.setInt(2, entity, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, fieldInvoker.getInt(2, entity));

        // int 最大值
        fieldInvoker.setInt(2, entity, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, fieldInvoker.getInt(2, entity));

        // int 零值
        fieldInvoker.setInt(2, entity, 0);
        assertEquals(0, fieldInvoker.getInt(2, entity));

        // int -1
        fieldInvoker.setInt(2, entity, -1);
        assertEquals(-1, fieldInvoker.getInt(2, entity));
    }

    @Test
    void testLongBoundaryValues() {
        // long 最小值
        fieldInvoker.setLong(3, entity, Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, fieldInvoker.getLong(3, entity));

        // long 最大值
        fieldInvoker.setLong(3, entity, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, fieldInvoker.getLong(3, entity));

        // long 零值
        fieldInvoker.setLong(3, entity, 0L);
        assertEquals(0L, fieldInvoker.getLong(3, entity));

        // long -1
        fieldInvoker.setLong(3, entity, -1L);
        assertEquals(-1L, fieldInvoker.getLong(3, entity));
    }

    @Test
    void testFloatBoundaryValues() {
        // float 最小值
        fieldInvoker.setFloat(4, entity, Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, fieldInvoker.getFloat(4, entity));

        // float 最大值
        fieldInvoker.setFloat(4, entity, Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, fieldInvoker.getFloat(4, entity));

        // float 正无穷
        fieldInvoker.setFloat(4, entity, Float.POSITIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldInvoker.getFloat(4, entity)));
        assertTrue(fieldInvoker.getFloat(4, entity) > 0);

        // float 负无穷
        fieldInvoker.setFloat(4, entity, Float.NEGATIVE_INFINITY);
        assertTrue(Float.isInfinite(fieldInvoker.getFloat(4, entity)));
        assertTrue(fieldInvoker.getFloat(4, entity) < 0);

        // float NaN
        fieldInvoker.setFloat(4, entity, Float.NaN);
        assertTrue(Float.isNaN(fieldInvoker.getFloat(4, entity)));

        // float 零值
        fieldInvoker.setFloat(4, entity, 0.0f);
        assertEquals(0.0f, fieldInvoker.getFloat(4, entity), 0.0f);

        // float 负零
        fieldInvoker.setFloat(4, entity, -0.0f);
        assertEquals(-0.0f, fieldInvoker.getFloat(4, entity), 0.0f);

        // float 最小正数
        fieldInvoker.setFloat(4, entity, Float.MIN_NORMAL);
        assertEquals(Float.MIN_NORMAL, fieldInvoker.getFloat(4, entity));
    }

    @Test
    void testDoubleBoundaryValues() {
        // double 最小值
        fieldInvoker.setDouble(5, entity, Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, fieldInvoker.getDouble(5, entity));

        // double 最大值
        fieldInvoker.setDouble(5, entity, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, fieldInvoker.getDouble(5, entity));

        // double 正无穷
        fieldInvoker.setDouble(5, entity, Double.POSITIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldInvoker.getDouble(5, entity)));
        assertTrue(fieldInvoker.getDouble(5, entity) > 0);

        // double 负无穷
        fieldInvoker.setDouble(5, entity, Double.NEGATIVE_INFINITY);
        assertTrue(Double.isInfinite(fieldInvoker.getDouble(5, entity)));
        assertTrue(fieldInvoker.getDouble(5, entity) < 0);

        // double NaN
        fieldInvoker.setDouble(5, entity, Double.NaN);
        assertTrue(Double.isNaN(fieldInvoker.getDouble(5, entity)));

        // double 零值
        fieldInvoker.setDouble(5, entity, 0.0);
        assertEquals(0.0, fieldInvoker.getDouble(5, entity), 0.0);

        // double 负零
        fieldInvoker.setDouble(5, entity, -0.0);
        assertEquals(-0.0, fieldInvoker.getDouble(5, entity), 0.0);

        // double 最小正数
        fieldInvoker.setDouble(5, entity, Double.MIN_NORMAL);
        assertEquals(Double.MIN_NORMAL, fieldInvoker.getDouble(5, entity));
    }

    @Test
    void testCharBoundaryValues() {
        // char 最小值
        fieldInvoker.setChar(7, entity, Character.MIN_VALUE);
        assertEquals(Character.MIN_VALUE, fieldInvoker.getChar(7, entity));

        // char 最大值
        fieldInvoker.setChar(7, entity, Character.MAX_VALUE);
        assertEquals(Character.MAX_VALUE, fieldInvoker.getChar(7, entity));

        // char 零值
        fieldInvoker.setChar(7, entity, '\u0000');
        assertEquals('\u0000', fieldInvoker.getChar(7, entity));

        // char 常用字符
        fieldInvoker.setChar(7, entity, 'A');
        assertEquals('A', fieldInvoker.getChar(7, entity));

        fieldInvoker.setChar(7, entity, '中');
        assertEquals('中', fieldInvoker.getChar(7, entity));
    }

    @Test
    void testBooleanBoundaryValues() {
        // boolean true
        fieldInvoker.setBoolean(6, entity, true);
        assertTrue(fieldInvoker.getBoolean(6, entity));

        // boolean false
        fieldInvoker.setBoolean(6, entity, false);
        assertFalse(fieldInvoker.getBoolean(6, entity));
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
        // 尝试使用 getByte 访问非 byte 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getByte(2, entity); // index 2 是 int 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getByte(8, entity); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchGetShort() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(0, entity)); // byte
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getShort(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetInt() {
        // 尝试使用 getInt 访问非 int 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getInt(0, entity); // index 0 是 byte 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getInt(8, entity); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchGetLong() {
        // 尝试使用 getLong 访问非 long 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getLong(2, entity); // index 2 是 int 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.getLong(8, entity); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchGetFloat() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getFloat(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getFloat(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetDouble() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getDouble(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getDouble(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetBoolean() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getBoolean(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getBoolean(8, entity)); // String
    }

    @Test
    void testTypeMismatchGetChar() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getChar(2, entity)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.getChar(8, entity)); // String
    }

    @Test
    void testTypeMismatchSetByte() {
        // 尝试使用 setByte 设置非 byte 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setByte(2, entity, (byte) 42); // index 2 是 int 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setByte(8, entity, (byte) 42); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchSetShort() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(0, entity, (short) 1)); // byte
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(2, entity, (short) 1)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setShort(8, entity, (short) 1)); // String
    }

    @Test
    void testTypeMismatchSetInt() {
        // 尝试使用 setInt 设置非 int 类型的字段（应该抛异常）
        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setInt(0, entity, 42); // index 0 是 byte 类型字段
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fieldInvoker.setInt(8, entity, 42); // index 8 是 String 类型字段
        });
    }

    @Test
    void testTypeMismatchSetLong() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setLong(2, entity, 1L)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setLong(8, entity, 1L)); // String
    }

    @Test
    void testTypeMismatchSetFloat() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setFloat(2, entity, 1.0f)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setFloat(8, entity, 1.0f)); // String
    }

    @Test
    void testTypeMismatchSetDouble() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setDouble(2, entity, 1.0)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setDouble(8, entity, 1.0)); // String
    }

    @Test
    void testTypeMismatchSetBoolean() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setBoolean(2, entity, true)); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setBoolean(8, entity, true)); // String
    }

    @Test
    void testTypeMismatchSetChar() {
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setChar(2, entity, 'A')); // int
        assertThrows(IllegalArgumentException.class, () -> fieldInvoker.setChar(8, entity, 'A')); // String
    }

    // ==================== null 值测试 ====================

    @Test
    void testNullInstance() {
        // 对于基本类型字段，GETFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.get(0, null); // index 0 是 byte 类型字段
        });

        // 对于引用类型字段，GETFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.get(8, null); // index 8 是 String 类型字段
        });

        // 对于基本类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        // 使用正确的类型（byte）来避免类型不匹配
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.set(0, null, (byte) 42); // index 0 是 byte 类型字段
        });

        // 对于引用类型字段，PUTFIELD 会在 null 实例上抛出 NullPointerException
        assertThrows(NullPointerException.class, () -> {
            fieldInvoker.set(8, null, "value"); // index 8 是 String 类型字段
        });
    }

    @Test
    void testNullInstanceAllPrimitiveGetters() {
        assertThrows(NullPointerException.class, () -> fieldInvoker.getByte(0, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getShort(1, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getInt(2, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getLong(3, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getFloat(4, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getDouble(5, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getBoolean(6, null));
        assertThrows(NullPointerException.class, () -> fieldInvoker.getChar(7, null));
    }

    @Test
    void testNullInstanceAllPrimitiveSetters() {
        assertThrows(NullPointerException.class, () -> fieldInvoker.setByte(0, null, (byte) 1));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setShort(1, null, (short) 1));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setInt(2, null, 1));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setLong(3, null, 1L));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setFloat(4, null, 1.0f));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setDouble(5, null, 1.0));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setBoolean(6, null, true));
        assertThrows(NullPointerException.class, () -> fieldInvoker.setChar(7, null, 'A'));
    }

    @Test
    void testNullValueSet() {
        // 对于引用类型字段，可以设置为 null
        fieldInvoker.set(8, entity, null);
        assertNull(fieldInvoker.get(8, entity));

        fieldInvoker.set(9, entity, null);
        assertNull(fieldInvoker.get(9, entity));
    }

    @Test
    void testNullValueThenSet() {
        // 设置为 null 后可以重新设置
        fieldInvoker.set(8, entity, null);
        assertNull(fieldInvoker.get(8, entity));

        fieldInvoker.set(8, entity, "newValue");
        assertEquals("newValue", fieldInvoker.get(8, entity));
    }

    // ==================== 组合测试 ====================

    @Test
    void testAllPrimitiveTypes() {
        // 测试所有基本类型的 set 和 get
        fieldInvoker.setByte(0, entity, (byte) 1);
        fieldInvoker.setShort(1, entity, (short) 2);
        fieldInvoker.setInt(2, entity, 3);
        fieldInvoker.setLong(3, entity, 4L);
        fieldInvoker.setFloat(4, entity, 5.0f);
        fieldInvoker.setDouble(5, entity, 6.0);
        fieldInvoker.setBoolean(6, entity, true);
        fieldInvoker.setChar(7, entity, '7');

        assertEquals((byte) 1, fieldInvoker.getByte(0, entity));
        assertEquals((short) 2, fieldInvoker.getShort(1, entity));
        assertEquals(3, fieldInvoker.getInt(2, entity));
        assertEquals(4L, fieldInvoker.getLong(3, entity));
        assertEquals(5.0f, fieldInvoker.getFloat(4, entity), 0.0001f);
        assertEquals(6.0, fieldInvoker.getDouble(5, entity), 0.000001);
        assertTrue(fieldInvoker.getBoolean(6, entity));
        assertEquals('7', fieldInvoker.getChar(7, entity));
    }

    @Test
    void testMultipleOperations() {
        // 多次设置和获取
        for (int i = 0; i < 10; i++) {
            fieldInvoker.setInt(2, entity, i);
            assertEquals(i, fieldInvoker.getInt(2, entity));
        }
    }

    @Test
    void testMultipleOperationsAllTypes() {
        // 对所有基本类型进行多次操作
        for (int i = 0; i < 5; i++) {
            fieldInvoker.setByte(0, entity, (byte) i);
            assertEquals((byte) i, fieldInvoker.getByte(0, entity));

            fieldInvoker.setShort(1, entity, (short) i);
            assertEquals((short) i, fieldInvoker.getShort(1, entity));

            fieldInvoker.setInt(2, entity, i);
            assertEquals(i, fieldInvoker.getInt(2, entity));

            fieldInvoker.setLong(3, entity, (long) i);
            assertEquals((long) i, fieldInvoker.getLong(3, entity));

            fieldInvoker.setFloat(4, entity, (float) i);
            assertEquals((float) i, fieldInvoker.getFloat(4, entity), 0.0001f);

            fieldInvoker.setDouble(5, entity, (double) i);
            assertEquals((double) i, fieldInvoker.getDouble(5, entity), 0.000001);

            fieldInvoker.setBoolean(6, entity, i % 2 == 0);
            assertEquals(i % 2 == 0, fieldInvoker.getBoolean(6, entity));

            fieldInvoker.setChar(7, entity, (char) ('A' + i));
            assertEquals((char) ('A' + i), fieldInvoker.getChar(7, entity));
        }
    }

    @Test
    void testMixedAccess() {
        // 混合使用 Object 方法和基本类型方法
        fieldInvoker.set(2, entity, 100);
        assertEquals(100, ((Integer) fieldInvoker.get(2, entity)).intValue());

        fieldInvoker.setInt(2, entity, 200);
        assertEquals(200, fieldInvoker.getInt(2, entity));

        fieldInvoker.set(2, entity, 300);
        assertEquals(300, ((Integer) fieldInvoker.get(2, entity)).intValue());
    }

    @Test
    void testMixedAccessAllFields() {
        // 混合使用 Object 方法和基本类型方法访问所有字段
        // byte
        fieldInvoker.set(0, entity, (byte) 10);
        assertEquals((byte) 10, ((Byte) fieldInvoker.get(0, entity)).byteValue());
        fieldInvoker.setByte(0, entity, (byte) 20);
        assertEquals((byte) 20, fieldInvoker.getByte(0, entity));

        // int
        fieldInvoker.set(2, entity, 100);
        assertEquals(100, ((Integer) fieldInvoker.get(2, entity)).intValue());
        fieldInvoker.setInt(2, entity, 200);
        assertEquals(200, fieldInvoker.getInt(2, entity));

        // long
        fieldInvoker.set(3, entity, 1000L);
        assertEquals(1000L, ((Long) fieldInvoker.get(3, entity)).longValue());
        fieldInvoker.setLong(3, entity, 2000L);
        assertEquals(2000L, fieldInvoker.getLong(3, entity));

        // String
        fieldInvoker.set(8, entity, "test1");
        assertEquals("test1", fieldInvoker.get(8, entity));
        // String 字段没有对应的基本类型方法，只能使用 Object 方法
    }

    // ==================== 极端场景测试 ====================

    @Test
    void testRapidSetGet() {
        // 快速连续设置和获取
        for (int i = 0; i < 1000; i++) {
            fieldInvoker.setInt(2, entity, i);
            int value = fieldInvoker.getInt(2, entity);
            assertEquals(i, value);
        }
    }

    @Test
    void testAllFieldsSequential() {
        // 顺序访问所有字段
        fieldInvoker.setByte(0, entity, (byte) 1);
        fieldInvoker.setShort(1, entity, (short) 2);
        fieldInvoker.setInt(2, entity, 3);
        fieldInvoker.setLong(3, entity, 4L);
        fieldInvoker.setFloat(4, entity, 5.0f);
        fieldInvoker.setDouble(5, entity, 6.0);
        fieldInvoker.setBoolean(6, entity, true);
        fieldInvoker.setChar(7, entity, 'C');
        fieldInvoker.set(8, entity, "String");
        fieldInvoker.set(9, entity, 999);

        assertEquals((byte) 1, fieldInvoker.getByte(0, entity));
        assertEquals((short) 2, fieldInvoker.getShort(1, entity));
        assertEquals(3, fieldInvoker.getInt(2, entity));
        assertEquals(4L, fieldInvoker.getLong(3, entity));
        assertEquals(5.0f, fieldInvoker.getFloat(4, entity), 0.0001f);
        assertEquals(6.0, fieldInvoker.getDouble(5, entity), 0.000001);
        assertTrue(fieldInvoker.getBoolean(6, entity));
        assertEquals('C', fieldInvoker.getChar(7, entity));
        assertEquals("String", fieldInvoker.get(8, entity));
        assertEquals(999, fieldInvoker.get(9, entity));
    }

    @Test
    void testAllFieldsReverse() {
        // 反向访问所有字段
        fieldInvoker.set(9, entity, 999);
        fieldInvoker.set(8, entity, "String");
        fieldInvoker.setChar(7, entity, 'C');
        fieldInvoker.setBoolean(6, entity, true);
        fieldInvoker.setDouble(5, entity, 6.0);
        fieldInvoker.setFloat(4, entity, 5.0f);
        fieldInvoker.setLong(3, entity, 4L);
        fieldInvoker.setInt(2, entity, 3);
        fieldInvoker.setShort(1, entity, (short) 2);
        fieldInvoker.setByte(0, entity, (byte) 1);

        assertEquals(999, fieldInvoker.get(9, entity));
        assertEquals("String", fieldInvoker.get(8, entity));
        assertEquals('C', fieldInvoker.getChar(7, entity));
        assertTrue(fieldInvoker.getBoolean(6, entity));
        assertEquals(6.0, fieldInvoker.getDouble(5, entity), 0.000001);
        assertEquals(5.0f, fieldInvoker.getFloat(4, entity), 0.0001f);
        assertEquals(4L, fieldInvoker.getLong(3, entity));
        assertEquals(3, fieldInvoker.getInt(2, entity));
        assertEquals((short) 2, fieldInvoker.getShort(1, entity));
        assertEquals((byte) 1, fieldInvoker.getByte(0, entity));
    }

    @Test
    void testAlternatingAccess() {
        // 交替访问不同字段
        fieldInvoker.setInt(2, entity, 1);
        fieldInvoker.set(8, entity, "A");
        assertEquals(1, fieldInvoker.getInt(2, entity));
        assertEquals("A", fieldInvoker.get(8, entity));

        fieldInvoker.setInt(2, entity, 2);
        fieldInvoker.set(8, entity, "B");
        assertEquals(2, fieldInvoker.getInt(2, entity));
        assertEquals("B", fieldInvoker.get(8, entity));

        fieldInvoker.setInt(2, entity, 3);
        fieldInvoker.set(8, entity, "C");
        assertEquals(3, fieldInvoker.getInt(2, entity));
        assertEquals("C", fieldInvoker.get(8, entity));
    }

    @Test
    void testFloatPrecision() {
        // 测试 float 精度
        float[] testValues = {0.0f, 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f, 1.0f, 10.0f, 100.0f, 1000.0f};
        for (float value : testValues) {
            fieldInvoker.setFloat(4, entity, value);
            assertEquals(value, fieldInvoker.getFloat(4, entity), 0.0001f);
        }
    }

    @Test
    void testDoublePrecision() {
        // 测试 double 精度
        double[] testValues = {0.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 1.0, 10.0, 100.0, 1000.0};
        for (double value : testValues) {
            fieldInvoker.setDouble(5, entity, value);
            assertEquals(value, fieldInvoker.getDouble(5, entity), 0.000001);
        }
    }

    @Test
    void testCharUnicode() {
        // 测试 Unicode 字符
        char[] testChars = {'A', 'Z', '0', '9', '中', '文', '\u0000', '\uFFFF', '\u1234'};
        for (char c : testChars) {
            fieldInvoker.setChar(7, entity, c);
            assertEquals(c, fieldInvoker.getChar(7, entity));
        }
    }

    @Test
    void testStringSpecialValues() {
        // 测试 String 特殊值
        String[] testStrings = {"", " ", "test", "中文", "null", "\n", "\t", "\\", "\""};
        for (String str : testStrings) {
            fieldInvoker.set(8, entity, str);
            assertEquals(str, fieldInvoker.get(8, entity));
        }
    }

    @Test
    void testIntegerBoxingUnboxing() {
        // 测试 Integer 装箱拆箱
        Integer[] testValues = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, 100, -100};
        for (Integer value : testValues) {
            fieldInvoker.set(9, entity, value);
            Object result = fieldInvoker.get(9, entity);
            assertNotNull(result);
            assertEquals(value, result);
            assertTrue(result instanceof Integer);
        }
    }

    @Test
    void testSameValueMultipleTimes() {
        // 多次设置相同值
        for (int i = 0; i < 100; i++) {
            fieldInvoker.setInt(2, entity, 42);
            assertEquals(42, fieldInvoker.getInt(2, entity));
        }
    }

    @Test
    void testCrossFieldInteraction() {
        // 测试字段之间的交互
        fieldInvoker.setInt(2, entity, 100);
        fieldInvoker.setLong(3, entity, 200L);
        fieldInvoker.setFloat(4, entity, 300.0f);
        fieldInvoker.setDouble(5, entity, 400.0);

        assertEquals(100, fieldInvoker.getInt(2, entity));
        assertEquals(200L, fieldInvoker.getLong(3, entity));
        assertEquals(300.0f, fieldInvoker.getFloat(4, entity), 0.0001f);
        assertEquals(400.0, fieldInvoker.getDouble(5, entity), 0.000001);

        // 再次设置
        fieldInvoker.setInt(2, entity, 500);
        fieldInvoker.setLong(3, entity, 600L);
        fieldInvoker.setFloat(4, entity, 700.0f);
        fieldInvoker.setDouble(5, entity, 800.0);

        assertEquals(500, fieldInvoker.getInt(2, entity));
        assertEquals(600L, fieldInvoker.getLong(3, entity));
        assertEquals(700.0f, fieldInvoker.getFloat(4, entity), 0.0001f);
        assertEquals(800.0, fieldInvoker.getDouble(5, entity), 0.000001);
    }

    @Test
    void testValidIndexBoundaries() {
        // 测试有效索引边界
        // 第一个字段 (index 0)
        fieldInvoker.setByte(0, entity, (byte) 1);
        assertEquals((byte) 1, fieldInvoker.getByte(0, entity));

        // 最后一个字段 (index 9)
        fieldInvoker.set(9, entity, 999);
        assertEquals(999, fieldInvoker.get(9, entity));

        // 中间字段 (index 5)
        fieldInvoker.setDouble(5, entity, 123.456);
        assertEquals(123.456, fieldInvoker.getDouble(5, entity), 0.000001);
    }

    @Test
    void testExceptionMessages() {
        // 测试异常消息
        try {
            fieldInvoker.get(-1, entity);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid field index") || e.getMessage().contains("index"));
        }

        try {
            fieldInvoker.getInt(0, entity); // index 0 是 byte，不是 int
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid field index") || e.getMessage().contains("index"));
        }
    }
}
