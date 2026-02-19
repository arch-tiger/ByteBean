package com.github.archtiger.bytebean.api.method;

/**
 * 方法访问器抽象类，提供高性能的方法调用能力。
 * <p>
 * 该接口定义了对类方法的通用访问方法，支持多参数、基本类型参数和返回值的调用。
 * 提供了多个特化方法（如 invoke1、invoke2 等）以减少参数数组创建开销，
 * 以及基本类型特化方法（如 intInvoke、longInvoke 等）避免装箱拆箱。
 * </p>
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * MethodInvoker invoker = MethodInvokerHelper.of(MyClass.class);
 * int addIndex = invoker.getMethodIndexOrThrow("add", int.class, int.class);
 *
 * // 使用可变参数方法
 * int result = (int) invoker.invoke(addIndex, instance, 10, 20);
 *
 * // 使用特化方法（性能更优）
 * int result = (int) invoker.invoke2(addIndex, instance, 10, 20);
 *
 * // 使用基本类型返回值方法
 * int sum = invoker.intInvoke(addIndex, instance, 10, 20);
 * }</pre>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public abstract class MethodInvoker {
    /**
     * 调用指定索引的方法。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组，可为null表示无参方法
     * @return 方法返回值，void方法返回null
     * @throws IllegalArgumentException 如果索引超出范围或参数类型不匹配
     * @throws RuntimeException       如果方法调用失败
     */
    public abstract Object invoke(int index, Object instance, Object... arguments);

    /**
     * 调用无参方法。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @return 方法返回值，void方法返回null
     * @throws IllegalArgumentException 如果索引超出范围
     */
    public abstract Object invoke(int index, Object instance);

    /**
     * 调用单参数方法（特化版本，避免创建参数数组）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg1     方法参数1
     * @return 方法返回值
     */
    public abstract Object invoke1(int index, Object instance, Object arg1);

    /**
     * 调用双参数方法（特化版本，避免创建参数数组）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg1     方法参数1
     * @param arg2     方法参数2
     * @return 方法返回值
     */
    public abstract Object invoke2(int index, Object instance, Object arg1, Object arg2);

    /**
     * 调用三参数方法（特化版本，避免创建参数数组）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg1     方法参数1
     * @param arg2     方法参数2
     * @param arg3     方法参数3
     * @return 方法返回值
     */
    public abstract Object invoke3(int index, Object instance, Object arg1, Object arg2, Object arg3);

    /**
     * 调用四参数方法（特化版本，避免创建参数数组）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg1     方法参数1
     * @param arg2     方法参数2
     * @param arg3     方法参数3
     * @param arg4     方法参数4
     * @return 方法返回值
     */
    public abstract Object invoke4(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4);

    /**
     * 调用五参数方法（特化版本，避免创建参数数组）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg1     方法参数1
     * @param arg2     方法参数2
     * @param arg3     方法参数3
     * @param arg4     方法参数4
     * @param arg5     方法参数5
     * @return 方法返回值
     */
    public abstract Object invoke5(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

    /**
     * 调用指定索引的方法，返回 int 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（int 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 int
     */
    public abstract int intInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 long 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（long 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 long
     */
    public abstract long longInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 float 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（float 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 float
     */
    public abstract float floatInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 double 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（double 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 double
     */
    public abstract double doubleInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 boolean 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（boolean 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 boolean
     */
    public abstract boolean booleanInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 byte 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（byte 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 byte
     */
    public abstract byte byteInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 short 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（short 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 short
     */
    public abstract short shortInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用指定索引的方法，返回 char 类型结果（避免装箱拆箱）。
     *
     * @param index     方法索引
     * @param instance  目标实例，非null
     * @param arguments 方法参数数组
     * @return 方法返回值（char 类型）
     * @throws IllegalArgumentException 如果方法返回类型不是 char
     */
    public abstract char charInvoke(int index, Object instance, Object... arguments);

    /**
     * 调用单参数方法，参数类型为 int（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      int 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeInt1(int index, Object instance, int arg);

    /**
     * 调用单参数方法，参数类型为 long（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      long 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeLong1(int index, Object instance, long arg);

    /**
     * 调用单参数方法，参数类型为 float（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      float 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeFloat1(int index, Object instance, float arg);

    /**
     * 调用单参数方法，参数类型为 double（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      double 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeDouble1(int index, Object instance, double arg);

    /**
     * 调用单参数方法，参数类型为 boolean（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      boolean 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeBoolean1(int index, Object instance, boolean arg);

    /**
     * 调用单参数方法，参数类型为 byte（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      byte 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeByte1(int index, Object instance, byte arg);

    /**
     * 调用单参数方法，参数类型为 short（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      short 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeShort1(int index, Object instance, short arg);

    /**
     * 调用单参数方法，参数类型为 char（避免自动装箱）。
     *
     * @param index    方法索引
     * @param instance 目标实例，非null
     * @param arg      char 类型参数
     * @return 方法返回值
     */
    public abstract Object invokeChar1(int index, Object instance, char arg);

}

