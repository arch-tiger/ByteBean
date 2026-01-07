package com.github.archtiger.core.bytecode;

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
 * 方法调用器字节码追加器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 23:24
 */
public final class MethodInvokerAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Method method;

    public MethodInvokerAppender(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        Class<?>[] paramTypes = method.getParameterTypes();
        boolean returnsVoid = method.getReturnType() == void.class;

        // 加载 target
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 加载参数 args
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

        // 如果返回 void，返回 null
        if (returnsVoid) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            AsmUtil.boxIfNeeded(methodVisitor, method.getReturnType()); // 装箱
        }

        methodVisitor.visitInsn(Opcodes.ARETURN);

        return StackUtil.forMethodInvoker(method);
    }

}
