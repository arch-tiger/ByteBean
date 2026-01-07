package com.github.archtiger.core.bytecode;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.StackUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Constructor;

/**
 * 构造器调用字节码生成器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class ConstructorAppender implements ByteCodeAppender {

    private final Class<?> targetClass;
    private final Constructor<?> ctor;

    public ConstructorAppender(Class<?> targetClass, Constructor<?> ctor) {
        this.targetClass = targetClass;
        this.ctor = ctor;
    }

    @Override
    public Size apply(MethodVisitor mv, Implementation.Context ctx, MethodDescription md) {

        Class<?>[] params = ctor.getParameterTypes();

        // 在堆上分配一个新对象，类型为 owner（目标类）
        mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(targetClass));
        // 复制栈顶的对象引用
        // 结果栈上有两个对象引用：一个用于调用 <init>，一个用于返回
        mv.visitInsn(Opcodes.DUP);

        // 构造器传参
        for (int i = 0; i < params.length; i++) {
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            mv.visitInsn(Opcodes.AALOAD);
            AsmUtil.unboxOrCast(mv, params[i]);
        }

        // 调用构造器 <init>
        // 参数顺序依次从栈顶取（由上面的循环生成）
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getInternalName(targetClass),
                "<init>",
                Type.getConstructorDescriptor(ctor),
                false);

        mv.visitInsn(Opcodes.ARETURN);

        return StackUtil.forConstructorInvoker(ctor);
    }
}
