package com.github.archtiger.core.support;

import net.bytebuddy.implementation.bytecode.ByteCodeAppender.Size;

import java.lang.reflect.Constructor;
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
        int maxStack = 1 + slotSize(type);
        if (type.isPrimitive()) {
            maxStack += 1; // 装箱临时栈
        }
        int maxLocals = 2; // this + target
        return new Size(maxStack, maxLocals);
    }

    /**
     * 获取设置器的栈大小
     *
     * @param type 字段类型
     * @return 栈大小
     */
    public static Size forSetter(Class<?> type) {
        // Setter maxStack: target + value, primitive long/double 占两个 slot
        int maxStack = 1 + slotSize(type);
        if (type.isPrimitive()) {
            maxStack += 1; // 拆箱临时栈
        }
        int maxLocals = 3; // this + target + value
        return new Size(maxStack, maxLocals);
    }

    /**
     * 获取方法的栈大小
     *
     * @param method 方法
     * @return 栈大小
     */
    public static Size forInvoker(Method method) {
        int maxStack = 1; // target
        for (Class<?> p : method.getParameterTypes()) {
            maxStack += slotSize(p);
            if (p.isPrimitive()) {
                maxStack += 1; // 拆箱/装箱临时
            }
        }
        if (method.getReturnType().isPrimitive() && method.getReturnType() != void.class) {
            maxStack += 1; // 返回值装箱
        }
        int maxLocals = 3; // this + target + Object[] args
        return new Size(maxStack, maxLocals);
    }

    /**
     * 获取构造器的栈大小
     *
     * @param constructor 构造器
     * @return 栈大小
     */
    public static Size forConstructor(Constructor<?> constructor) {
        Class<?>[] params = constructor.getParameterTypes();
        int maxStack = 1 + 1; // NEW + DUP
        for (Class<?> p : params) {
            maxStack += slotSize(p);
            if (p.isPrimitive()) {
                maxStack += 1; // 拆箱临时栈
            }
        }
        int maxLocals = 1 + params.length; // this + 参数数组
        return new Size(maxStack, maxLocals);
    }
}
