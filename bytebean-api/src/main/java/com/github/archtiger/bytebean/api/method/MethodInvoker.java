package com.github.archtiger.bytebean.api.method;

/**
 * 方法访问器，定义了对类方法的通用访问方法。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 18:18
 */
public abstract class MethodInvoker {
    /**
     * 调用指定索引的方法
     *
     * @param index     方法索引
     * @param instance  目标实例
     * @param arguments 方法参数
     * @return 方法返回值
     */
    public abstract Object invoke(int index, Object instance, Object... arguments);

    public abstract Object invoke(int index, Object instance, Object arg);

    public abstract Object invoke(int index, Object instance);

    /**
     * 调用指定索引的方法，返回 int 类型结果
     *
     * @param index     方法索引
     * @param instance  目标实例
     * @param arguments 方法参数
     * @return 方法返回值（int 类型）
     */
    public abstract int intInvoke(int index, Object instance, Object... arguments);

    public abstract long longInvoke(int index, Object instance, Object... arguments);

    public abstract float floatInvoke(int index, Object instance, Object... arguments);

    public abstract double doubleInvoke(int index, Object instance, Object... arguments);

    public abstract boolean booleanInvoke(int index, Object instance, Object... arguments);

    public abstract byte byteInvoke(int index, Object instance, Object... arguments);

    public abstract short shortInvoke(int index, Object instance, Object... arguments);

    public abstract char charInvoke(int index, Object instance, Object... arguments);

    public abstract Object invokeInt1(int index, Object instance, int arg);

    public abstract Object invokeLong1(int index, Object instance, long arg);

    public abstract Object invokeFloat1(int index, Object instance, float arg);

    public abstract Object invokeDouble1(int index, Object instance, double arg);

    public abstract Object invokeBoolean1(int index, Object instance, boolean arg);

    public abstract Object invokeByte1(int index, Object instance, byte arg);

    public abstract Object invokeShort1(int index, Object instance, short arg);

    public abstract Object invokeChar1(int index, Object instance, char arg);

}

