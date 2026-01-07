package com.github.archtiger.core.bytecode.constructor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Constructor;

/**
 * 专门针对无参构造器的字节码追加器
 * 优化性能，不需要参数循环
 * 
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class NoArgConstructorAppender implements ByteCodeAppender {

    private final Class<?> targetClass;
    private final Constructor<?> ctor;

    public NoArgConstructorAppender(Class<?> targetClass, Constructor<?> ctor) {
        if (ctor.getParameterCount() != 0) {
            throw new IllegalArgumentException("Constructor must be no-arg");
        }
        this.targetClass = targetClass;
        this.ctor = ctor;
    }

    @Override
    public Size apply(MethodVisitor mv, Implementation.Context ctx, MethodDescription md) {
        // 在堆上创建新对象
        mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(targetClass));
        mv.visitInsn(Opcodes.DUP);

        // 调用无参构造器
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getInternalName(targetClass),
                "<init>",
                Type.getConstructorDescriptor(ctor),
                false);

        // 返回新对象
        mv.visitInsn(Opcodes.ARETURN);

        // maxStack = NEW + DUP + 构造器消耗(无参=0) = 2
        // maxLocals = 1 (this)
        return new Size(2, 1);
    }
}
