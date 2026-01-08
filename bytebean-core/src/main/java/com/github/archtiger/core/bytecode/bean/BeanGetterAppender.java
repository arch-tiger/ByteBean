package com.github.archtiger.core.bytecode.bean;

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
 * BeanGetter 字节码追加器
 * <p>
 * 用于生成 Bean Getter 方法调用的字节码，优化为直接调用方法，不需要参数数组
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class BeanGetterAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Method method;

    public BeanGetterAppender(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 加载 target 参数
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 调用 getter 方法
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(targetClass),
                method.getName(),
                Type.getMethodDescriptor(method),
                false
        );

        // 装箱返回值（如果是基本类型）
        AsmUtil.boxIfNeeded(methodVisitor, method.getReturnType());

        // 返回 Object
        methodVisitor.visitInsn(Opcodes.ARETURN);

        return StackUtil.forBeanGetter(method);
    }
}
