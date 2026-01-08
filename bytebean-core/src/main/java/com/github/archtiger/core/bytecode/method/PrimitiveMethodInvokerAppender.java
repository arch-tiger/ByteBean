package com.github.archtiger.core.bytecode.method;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.StackUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;

/**
 * 基本类型 Method Invoker 字节码追加器
 * <p>
 * 用于生成基本类型返回值的方法调用字节码，直接返回基本类型，避免装箱
 * <p>
 * 支持所有基本类型：byte, short, int, long, float, double, boolean, char
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class PrimitiveMethodInvokerAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Method method;

    public PrimitiveMethodInvokerAppender(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        Class<?>[] paramTypes = method.getParameterTypes();

        // 加载 target
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 加载参数（从 Object[] args 中加载）
        for (int i = 0; i < paramTypes.length; i++) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);   // Object[] args
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
            methodVisitor.visitInsn(Opcodes.AALOAD);       // args[i]
            AsmUtil.unboxOrCast(methodVisitor, paramTypes[i]); // 拆箱/类型转换
        }

        // 调用方法
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(targetClass),
                method.getName(),
                Type.getMethodDescriptor(method),
                false
        );

        // 根据返回类型生成对应的返回指令（避免装箱）
        Class<?> returnType = method.getReturnType();
        int returnOpcode = AsmUtil.getReturnOpcode(returnType);
        methodVisitor.visitInsn(returnOpcode);

        // 栈大小计算与 MethodInvoker 相同，因为参数处理方式相同
        return StackUtil.forMethodInvoker(method);
    }
}
