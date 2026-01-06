package com.github.archtiger.core.support;

import net.bytebuddy.implementation.bytecode.ByteCodeAppender.Size;

import java.lang.reflect.Method;

/**
 * 栈大小工具类
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
public final class StackUtil {
    /**
     * 返回字段类型在栈上占用的 slot 数量
     *
     * @param type 字段类型
     * @return 字段类型在栈上占用的 slot 数量
     */
    public static int slotSize(Class<?> type) {
        if (!type.isPrimitive()) {
            return 1;
        }
        if (type == long.class || type == double.class) {
            return 2;
        }

        return 1;
    }

    /**
     * 获取获取器的栈大小
     *
     * @param type 字段类型
     * @return 栈大小
     */
    public static Size forGetter(Class<?> type) {
        // Getter maxStack = 2 (target + long/double 1 slot counts as 2)
        final int maxStack = Math.max(2, slotSize(type));
        return new Size(maxStack, 2);
    }

    /**
     * 获取设置器的栈大小
     *
     * @param type 字段类型
     * @return 栈大小
     */
    public static Size forSetter(Class<?> type) {
        // Setter maxStack: target + value, primitive long/double 占两个 slot
        final int maxStack = 1 + slotSize(type);
        return new Size(maxStack, 3);
    }

    /**
     * 获取方法的栈大小
     *
     * @param method 方法
     * @return 栈大小
     */
    public static Size forInvoker(Method method) {
        boolean returnsVoid = method.getReturnType() == void.class;
        int maxStack = 1; // target
        for (Class<?> t : method.getParameterTypes()) {
            maxStack += slotSize(t); // long/double 占 2
        }
        if (!returnsVoid && method.getReturnType().isPrimitive()) {
            maxStack = Math.max(maxStack, 2); // 装箱需要额外栈
        }

        return new Size(maxStack, 3); // locals: target + args
    }

    /**
     * 获取构造器的栈大小
     *
     * @param params 构造器参数类型
     * @return 栈大小
     */
    public static Size forConstructor(Class<?>[] params) {
        int stack = 2;
        for (Class<?> p : params) {
            stack += (p == long.class || p == double.class) ? 2 : 1;
        }
        return new Size(stack, 2);
    }
}
