package com.github.archtiger.bytebean.api.constructor;

/**
 * 构造器访问器抽象类，提供通过索引快速调用构造器的能力。
 * <p>
 * 该接口定义了构造器调用的标准方法，支持带参数和无参数的构造器调用。
 * 实现类通常会根据构造器数量选择不同的策略（字节码生成或MethodHandle）来优化性能。
 * </p>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public abstract class ConstructorInvoker {

    /**
     * 使用指定索引和参数调用构造器创建新实例。
     *
     * @param index 构造器索引
     * @param args  构造器参数数组，可为null表示无参构造器
     * @return 新创建的对象实例
     * @throws IllegalArgumentException 如果索引超出范围或参数类型不匹配
     * @throws RuntimeException       如果构造器调用失败
     */
    public abstract Object newInstance(int index, Object... args);

    /**
     * 调用默认构造器（无参数构造器）创建新实例。
     * <p>
     * 此方法要求目标类必须存在公共的无参构造器。
     * </p>
     *
     * @return 新创建的对象实例
     * @throws IllegalStateException 如果目标类没有无参构造器
     * @throws RuntimeException     如果构造器调用失败
     */
    public abstract Object newInstance();
}
