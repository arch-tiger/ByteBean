package com.github.archtiger.definition.invoker.method;

/**
 * 方法访问器接口，定义了对类方法的通用访问方法。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 18:18
 */
public interface MethodInvoker {
    /**
     * 调用指定索引的方法
     *
     * @param index     方法索引
     * @param instance  目标实例
     * @param arguments 方法参数
     * @return 方法返回值
     */
    Object invoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 int 类型结果
     *
     * @param index     方法索引
     * @param instance  目标实例
     * @param arguments 方法参数
     * @return 方法返回值（int 类型）
     */
    int intInvoke(int index, Object instance, Object... arguments);

    long longInvoke(int index, Object instance, Object... arguments);

    float floatInvoke(int index, Object instance, Object... arguments);

    double doubleInvoke(int index, Object instance, Object... arguments);

    boolean booleanInvoke(int index, Object instance, Object... arguments);

    byte byteInvoke(int index, Object instance, Object... arguments);

    short shortInvoke(int index, Object instance, Object... arguments);

    char charInvoke(int index, Object instance, Object... arguments);

    Object invokeInt1(int index, Object instance, int arg);

    Object invokeLong1(int index, Object instance, long arg);

    Object invokeFloat1(int index, Object instance, float arg);

    Object invokeDouble1(int index, Object instance, double arg);

    Object invokeBoolean1(int index, Object instance, boolean arg);

    Object invokeByte1(int index, Object instance, byte arg);

    Object invokeShort1(int index, Object instance, short arg);

    Object invokeChar1(int index, Object instance, char arg);

}
