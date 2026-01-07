package com.github.archtiger.core.bytecode;

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
 * 设置器字节码追加器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
public final class FieldSetterAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Field field;

    public FieldSetterAppender(Class<?> targetClass, Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 将 target 参数加载到栈上
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

        // 将 value 参数加载到栈上
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);

        // 如果是 primitive 字段，拆箱
        AsmUtil.unboxOrCast(methodVisitor, field.getType());

        // 执行 PUTFIELD 指令
        methodVisitor.visitFieldInsn(Opcodes.PUTFIELD,
                Type.getInternalName(targetClass),
                field.getName(),
                Type.getDescriptor(field.getType())
        );

        methodVisitor.visitInsn(Opcodes.RETURN);

        // Setter maxStack: target + value, primitive long/double 占两个 slot
        return StackUtil.forFieldSetter(field);
    }

}
