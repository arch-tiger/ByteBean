package com.github.archtiger.core.invoker.constructor;

/**
 * 测试构造器实体类，包含多个构造器用于测试 ConstructorAccess
 */
class TestConstructorEntity {
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

    // 无参构造器
    public TestConstructorEntity() {
    }

    // 单参数构造器
    public TestConstructorEntity(int intValue) {
        this.intValue = intValue;
    }

    public TestConstructorEntity(long longValue) {
        this.longValue = longValue;
    }

    public TestConstructorEntity(float floatValue) {
        this.floatValue = floatValue;
    }

    public TestConstructorEntity(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public TestConstructorEntity(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public TestConstructorEntity(byte byteValue) {
        this.byteValue = byteValue;
    }

    public TestConstructorEntity(short shortValue) {
        this.shortValue = shortValue;
    }

    public TestConstructorEntity(char charValue) {
        this.charValue = charValue;
    }

    public TestConstructorEntity(String stringValue) {
        this.stringValue = stringValue;
    }

    public TestConstructorEntity(Integer integerValue) {
        this.integerValue = integerValue;
    }

    // 多参数构造器
    public TestConstructorEntity(int intValue, long longValue) {
        this.intValue = intValue;
        this.longValue = longValue;
    }

    public TestConstructorEntity(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    public TestConstructorEntity(String stringValue, int intValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public TestConstructorEntity(int intValue, long longValue, double doubleValue) {
        this.intValue = intValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
    }

    public TestConstructorEntity(byte byteValue, short shortValue, int intValue, long longValue) {
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.intValue = intValue;
        this.longValue = longValue;
    }

    public TestConstructorEntity(float floatValue, double doubleValue, boolean booleanValue, char charValue) {
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.booleanValue = booleanValue;
        this.charValue = charValue;
    }

    public TestConstructorEntity(byte byteValue, short shortValue, int intValue, long longValue, 
                                 float floatValue, double doubleValue, boolean booleanValue, char charValue) {
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.intValue = intValue;
        this.longValue = longValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.booleanValue = booleanValue;
        this.charValue = charValue;
    }

    // Getter 方法
    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    public char getCharValue() {
        return charValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }
}
