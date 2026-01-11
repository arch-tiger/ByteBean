package com.github.archtiger.core.bytecode.field;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;

/**
 * 基本类型 FieldSetter 字节码追加器
 * <p>
 * 用于生成基本类型字段设置的字节码，直接接收基本类型，避免拆箱
 * <p>
 * 支持所有基本类型：byte, short, int, long, float, double, boolean, char
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class PrimitiveFieldSetterAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Field field;

    public PrimitiveFieldSetterAppender(Class<?> targetClass, Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 加载 target 参数
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 加载 value 参数（根据参数类型选择合适的加载指令）
        Class<?> fieldType = field.getType();
        int loadOpcode = AsmUtil.getLoadOpcode(fieldType);
        methodVisitor.visitVarInsn(loadOpcode, 2);

        // 执行 PUTFIELD 指令
        methodVisitor.visitFieldInsn(
                Opcodes.PUTFIELD,
                Type.getInternalName(targetClass),
                field.getName(),
                Type.getDescriptor(field.getType())
        );

        // 返回
        methodVisitor.visitInsn(Opcodes.RETURN);

        return ByteCodeSizeUtil.forPrimitiveFieldSetter();
    }
}
