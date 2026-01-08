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
 * 基本类型 BeanSetter 字节码追加器
 * <p>
 * 用于生成基本类型 Bean Setter 方法调用的字节码，直接接收基本类型，避免拆箱
 * <p>
 * 支持所有基本类型：byte, short, int, long, float, double, boolean, char
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class PrimitiveBeanSetterAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Method method;

    public PrimitiveBeanSetterAppender(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 加载 target 参数
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 加载 value 参数（根据参数类型选择合适的加载指令）
        Class<?> paramType = method.getParameterTypes()[0];
        int loadOpcode = AsmUtil.getLoadOpcode(paramType);
        methodVisitor.visitVarInsn(loadOpcode, 2);

        // 调用 setter 方法
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(targetClass),
                method.getName(),
                Type.getMethodDescriptor(method),
                false
        );

        // 返回
        methodVisitor.visitInsn(Opcodes.RETURN);

        return StackUtil.forPrimitiveBeanSetter();
    }
}
