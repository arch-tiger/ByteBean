package com.github.archtiger.core.support;

import net.bytebuddy.implementation.bytecode.ByteCodeAppender.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
    public static Size forFieldGetter(Field field) {
        // Getter maxStack = 2 (target + long/double 1 slot counts as 2)
        int maxStack = 1 + slotSize(field.getType());
        if (field.getType().isPrimitive()) {
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
    public static Size forFieldSetter(Field field) {
        // Setter maxStack: target + value, primitive long/double 占两个 slot
        int maxStack = 1 + slotSize(field.getType());
        if (field.getType().isPrimitive()) {
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
    public static Size forMethodInvoker(Method method) {
        boolean returnsVoid = method.getReturnType() == void.class;
        Class<?>[] params = method.getParameterTypes();

        int maxStack = 1; // target
        for (Class<?> p : params) {
            maxStack += slotSize(p);       // 参数自身
            if (p.isPrimitive()) {
                maxStack += 1;            // 拆箱临时栈
            }
        }

        // 额外计算 Object[] args 加载栈空间
        if (params.length > 0) {
            maxStack += 2; // ALOAD args + BIPUSH index
        }

        // 返回值装箱
        if (!returnsVoid && method.getReturnType().isPrimitive()) {
            maxStack += 1;
        }

        int maxLocals = 3; // this + target + args
        return new Size(maxStack, maxLocals);
    }

    /**
     * 获取构造器的栈大小
     *
     * @param constructor 构造器
     * @return 栈大小
     */
    public static Size forConstructorInvoker(Constructor<?> constructor) {
        Class<?>[] params = constructor.getParameterTypes();
        int maxStack = 2; // NEW + DUP
        for (Class<?> p : params) {
            maxStack += slotSize(p);
            if (p.isPrimitive()) maxStack += 1; // 拆箱临时栈
        }
        int maxLocals = 2; // 0=this, 1=args
        return new Size(maxStack, maxLocals);
    }

    // ==================== Bean 方法栈大小计算 ====================

    /**
     * 获取 Bean Getter 的栈大小（通用类型，返回 Object）
     *
     * @param method 方法
     * @return 栈大小
     */
    public static Size forBeanGetter(Method method) {
        int maxStack = 1; // target
        Class<?> returnType = method.getReturnType();

        // 基本类型返回值需要额外栈空间用于装箱
        if (returnType.isPrimitive()) {
            maxStack += 1;
        }

        int maxLocals = 2; // this + target
        return new Size(maxStack, maxLocals);
    }

    /**
     * 获取 Bean Setter 的栈大小（通用类型，参数 Object）
     *
     * @param method 方法
     * @return 栈大小
     */
    public static Size forBeanSetter(Method method) {
        int maxStack = 1; // target
        Class<?> paramType = method.getParameterTypes()[0];

        // 参数需要的栈空间
        maxStack += slotSize(paramType);

        // 基本类型参数需要额外栈空间用于拆箱
        if (paramType.isPrimitive()) {
            maxStack += 1;
        }

        int maxLocals = 3; // this + target + value
        return new Size(maxStack, maxLocals);
    }

    /**
     * 获取基本类型 Bean Getter 的栈大小
     * <p>
     * 基本类型不需要装箱，栈空间较小
     *
     * @return 栈大小
     */
    public static Size forPrimitiveBeanGetter() {
        // maxStack = 1 (target)
        // maxLocals = 2 (this + target)
        return new Size(1, 2);
    }

    /**
     * 获取基本类型 Bean Setter 的栈大小
     * <p>
     * 基本类型不需要拆箱，栈空间较小
     *
     * @return 栈大小
     */
    public static Size forPrimitiveBeanSetter() {
        // maxStack = 2 (target + value)
        // maxLocals = 3 (this + target + value)
        // 对于 long/double，value 占 2 个 slot，所以需要调整
        return new Size(2, 3);
    }

}