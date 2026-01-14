package com.github.archtiger.bytebean.core.invoker.method;

/**
 * 测试方法实体类，包含各种方法用于测试 MethodAccess
 */
class TestMethodEntity {
    private int intValue;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private boolean booleanValue;
    private byte byteValue;
    private short shortValue;
    private char charValue;
    private String stringValue;
    private Integer integerValue;

    public TestMethodEntity() {
    }

    // ==================== 无参数方法 ====================

    public int getInt() {
        return intValue;
    }

    public long getLong() {
        return longValue;
    }

    public float getFloat() {
        return floatValue;
    }

    public double getDouble() {
        return doubleValue;
    }

    public boolean getBoolean() {
        return booleanValue;
    }

    public byte getByte() {
        return byteValue;
    }

    public short getShort() {
        return shortValue;
    }

    public char getChar() {
        return charValue;
    }

    public String getString() {
        return stringValue;
    }

    public Integer getInteger() {
        return integerValue;
    }

    public void voidMethod() {
        // 空方法
    }

    // ==================== 单参数方法 ====================

    public void setInt(int value) {
        this.intValue = value;
    }

    public void setLong(long value) {
        this.longValue = value;
    }

    public void setFloat(float value) {
        this.floatValue = value;
    }

    public void setDouble(double value) {
        this.doubleValue = value;
    }

    public void setBoolean(boolean value) {
        this.booleanValue = value;
    }

    public void setByte(byte value) {
        this.byteValue = value;
    }

    public void setShort(short value) {
        this.shortValue = value;
    }

    public void setChar(char value) {
        this.charValue = value;
    }

    public void setString(String value) {
        this.stringValue = value;
    }

    public void setInteger(Integer value) {
        this.integerValue = value;
    }

    public int addInt(int value) {
        return intValue + value;
    }

    public long addLong(long value) {
        return longValue + value;
    }

    public float addFloat(float value) {
        return floatValue + value;
    }

    public double addDouble(double value) {
        return doubleValue + value;
    }

    public int multiplyInt(int value) {
        return intValue * value;
    }

    public double multiplyDouble(double value) {
        return doubleValue * value;
    }

    // ==================== 多参数方法 ====================

    public int addTwoInts(int a, int b) {
        return a + b;
    }

    public long addTwoLongs(long a, long b) {
        return a + b;
    }

    public double addThreeDoubles(double a, double b, double c) {
        return a + b + c;
    }

    public String concatenate(String a, String b) {
        return a + b;
    }

    public String concatenateThree(String a, String b, String c) {
        return a + b + c;
    }

    public int addMixed(int a, long b, double c) {
        return (int) (a + b + c);
    }

    public void setAllPrimitives(byte b, short s, int i, long l, float f, double d, boolean bool, char c) {
        this.byteValue = b;
        this.shortValue = s;
        this.intValue = i;
        this.longValue = l;
        this.floatValue = f;
        this.doubleValue = d;
        this.booleanValue = bool;
        this.charValue = c;
    }

    // ==================== 返回基本类型的方法 ====================

    public int computeInt(int value) {
        return value * 2;
    }

    public long computeLong(long value) {
        return value * 2;
    }

    public float computeFloat(float value) {
        return value * 2;
    }

    public double computeDouble(double value) {
        return value * 2;
    }

    public boolean isEven(int value) {
        return value % 2 == 0;
    }

    public byte incrementByte(byte value) {
        return (byte) (value + 1);
    }

    public short incrementShort(short value) {
        return (short) (value + 1);
    }

    public char nextChar(char value) {
        return (char) (value + 1);
    }
}
