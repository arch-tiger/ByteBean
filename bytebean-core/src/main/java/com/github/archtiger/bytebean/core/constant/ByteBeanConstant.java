package com.github.archtiger.bytebean.core.constant;

/**
 * 常量
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public interface ByteBeanConstant {
    /**
     * 调用器类名前缀。
     */
    String INVOKER_NAME_PREFIX = "ByteBean";

    /**
     * 方法分片阈值，当方法数超过此值时使用MethodHandle而非字节码。
     */
    int METHOD_SHARDING_THRESHOLD_VALUE = 400;

    /**
     * 字段分片阈值，当字段数超过此值时使用VarHandle而非字节码。
     */
    int FIELD_SHARDING_THRESHOLD_VALUE = 500;

    /**
     * 构造器分片阈值，当构造器数超过此值时使用MethodHandle而非字节码。
     */
    int CONSTRUCTOR_SHARDING_THRESHOLD_VALUE = 20;
}
