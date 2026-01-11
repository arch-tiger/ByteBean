package com.github.archtiger.core.bytecode.method;

import com.github.archtiger.core.bytecode.AbstractInvokerAppender;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.definition.invoker.method.MethodInvoker;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
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
public final class MethodInvokerAppender extends AbstractInvokerAppender<MethodInvoker> {
    private final Method method;

    public MethodInvokerAppender(InvokerInfo<MethodInvoker> invokerInfo, Method method) {
        super(invokerInfo);
        this.method = method;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        Class<?>[] paramTypes = method.getParameterTypes();
        boolean returnsVoid = method.getReturnType() == void.class;

        // 加载 target
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(getInvokerInfo().targetClass()));

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
                Type.getInternalName(getInvokerInfo().targetClass()),
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

        return calcSize();
    }

    @Override
    protected Size calcSize() {
        // 1. 计算 maxLocals
        // 局部变量表索引分配：
        // 0 -> this
        // 1 -> target (方法参数)
        // 2 -> args   (方法参数)
        int maxLocals = 3;

        // 2. 计算 maxStack
        Class<?>[] paramTypes = method.getParameterTypes();

        // 特殊情况处理：无参数
        if (paramTypes.length == 0) {
            // 栈上只有 target (1)。
            // 调用后如果有返回值（long/double 为 2），栈深度可能变为 2。
            int returnSize = Type.getType(method.getReturnType()).getSize();
            int maxStack = Math.max(1, returnSize);
            return new Size(maxStack, maxLocals);
        }

        // 通用情况：有参数
        // 公式：MaxStack = 1(target) + TotalParamSlots + 2(loopTemp) - LastParamSize

        int totalParamSlots = 0;
        for (Class<?> paramType : paramTypes) {
            totalParamSlots += Type.getType(paramType).getSize();
        }

        int lastParamSize = Type.getType(paramTypes[paramTypes.length - 1]).getSize();

        int maxStack = 1 + totalParamSlots + 2 - lastParamSize;

        return new Size(maxStack, maxLocals);
    }
}
