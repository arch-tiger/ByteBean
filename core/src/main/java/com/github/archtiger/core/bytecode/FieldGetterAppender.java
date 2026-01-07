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
 * 获取器字节码追加器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
public final class FieldGetterAppender implements ByteCodeAppender {
    private final Class<?> targetClass;
    private final Field field;

    public FieldGetterAppender(Class<?> targetClass, Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 将 target 参数加载到栈上
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));
        // 访问字段
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD,
                Type.getInternalName(targetClass),
                field.getName(),
                Type.getDescriptor(field.getType())
        );
        // 装箱 primitive
        AsmUtil.boxIfNeeded(methodVisitor, field.getType());
        // 返回
        methodVisitor.visitInsn(Opcodes.ARETURN);
        // Getter maxStack = 2 (target + long/double 1 slot counts as 2)
        return StackUtil.forFieldGetter(field);
    }
}
