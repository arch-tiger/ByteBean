package com.github.archtiger.core.bytecode.method;

import com.github.archtiger.core.bytecode.AbstractInvokerAppender;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.StackUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
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
public final class PrimitiveMethod1InvokerAppender extends AbstractInvokerAppender {
    private final InvokerInfo invokerInfo;
    private final Method method;

    public PrimitiveMethod1InvokerAppender(InvokerInfo invokerInfo, Method method) {
        super(invokerInfo);
        this.invokerInfo = invokerInfo;
        this.method = method;
    }

    @Override
    public Size apply(MethodVisitor mv, Implementation.Context context, MethodDescription methodDescription) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> parameterType1 = parameterTypes[0];

        // load target object
        mv.visitVarInsn(Opcodes.ALOAD, 1); // target 是第1个局部变量

        // load primitive parameter (arg1)
        mv.visitVarInsn(AsmUtil.getLoadOpcode(parameterType1), 2);

        // invoke virtual method on target
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(invokerInfo.targetClass()),  // target 的类
                method.getName(),
                Type.getMethodDescriptor(method),
                false
        );

        if (method.getReturnType() != invokerInfo.invokerReturnType()){
            // box primitive return if needed
            AsmUtil.boxIfNeeded(mv, method.getReturnType());
        }

        // return value
        emitReturn(mv);
        return calcSize();
    }

    @Override
    protected Size calcSize() {
        return StackUtil.forUnaryMethodInvoker(method);
    }

}
