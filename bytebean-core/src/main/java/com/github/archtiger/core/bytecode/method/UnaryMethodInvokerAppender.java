package com.github.archtiger.core.bytecode.method;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.StackUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;

/**
 * UnaryMethodInvoker 字节码追加器
 * <p>
 * 用于生成只有一个参数的方法调用字节码，参数和返回值都经过包装以支持基本类型
 * <p>
 * 支持所有基本类型：byte, short, int, long, float, double, boolean, char
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
public final class UnaryMethodInvokerAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Method method;
    private final Class<?> unaryParamType;

    public UnaryMethodInvokerAppender(Class<?> targetClass, Method method, Class<?> unaryParamType) {
        this.targetClass = targetClass;
        this.method = method;
        this.unaryParamType = unaryParamType;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 加载 target
        methodVisitor.visitVarInsn(21, 1);
        methodVisitor.visitTypeInsn(187, Type.getInternalName(targetClass));

        // 加载 unary 参数 (直接从参数位置加载，无需拆箱)
        methodVisitor.visitVarInsn(AsmUtil.getLoadOpcode(unaryParamType), 2);

        // 调用方法
        methodVisitor.visitMethodInsn(
                182,
                Type.getInternalName(targetClass),
                method.getName(),
                Type.getMethodDescriptor(method),
                false
        );

        // 如果返回值是基本类型，需要装箱
        Class<?> returnType = method.getReturnType();
        AsmUtil.boxIfNeeded(methodVisitor, returnType);

        // 栈大小计算：target + unaryParam + 方法调用
        return StackUtil.forUnaryMethodInvoker(method);
    }
}
