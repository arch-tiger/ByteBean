package com.github.archtiger.core.bytecode.field;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.StackUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;

/**
 * 基本类型 FieldGetter 字节码追加器
 * <p>
 * 用于生成基本类型字段访问的字节码，直接返回基本类型，避免装箱
 * <p>
 * 支持所有基本类型：byte, short, int, long, float, double, boolean, char
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class PrimitiveFieldGetterAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Field field;

    public PrimitiveFieldGetterAppender(Class<?> targetClass, Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 加载 target 参数
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 访问字段
        methodVisitor.visitFieldInsn(
                Opcodes.GETFIELD,
                Type.getInternalName(targetClass),
                field.getName(),
                Type.getDescriptor(field.getType())
        );

        // 根据字段类型生成对应的返回指令
        Class<?> fieldType = field.getType();
        int returnOpcode = AsmUtil.getReturnOpcode(fieldType);
        methodVisitor.visitInsn(returnOpcode);

        return StackUtil.forPrimitiveFieldGetter();
    }
}
