package com.github.archtiger.core.support;

import com.github.archtiger.core.engine.Setter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;

/**
 * Setter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */

public class SetterFactory {

    /**
     * 创建 Setter
     *
     * @param targetClass 目标类
     * @param fieldName   字段名
     * @return Setter 实例
     */
    public static Setter createSetter(Class<?> targetClass, String fieldName) {

        try {
            Field field = targetClass.getDeclaredField(fieldName);

            Class<? extends Setter> setterClass = new ByteBuddy()
                    .subclass(Setter.class)
                    .name(targetClass.getName() + "$$" + fieldName + "Setter")
                    .method(m -> m.getName().equals("set"))
                    .intercept(new Implementation.Simple(new ByteCodeAppender() {
                        @Override
                        public Size apply(MethodVisitor mv, Implementation.Context context, MethodDescription method) {
                            // 将 target 参数加载到栈上
                            mv.visitVarInsn(Opcodes.ALOAD, 1);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));

                            // 将 value 参数加载到栈上
                            mv.visitVarInsn(Opcodes.ALOAD, 2);

                            // 如果是 primitive 字段，拆箱
                            AsmUtil.unboxIfNeeded(mv, field.getType());

                            // 执行 PUTFIELD
                            mv.visitFieldInsn(Opcodes.PUTFIELD,
                                    Type.getInternalName(targetClass),
                                    field.getName(),
                                    Type.getDescriptor(field.getType())
                            );

                            mv.visitInsn(Opcodes.RETURN);

                            // maxStack: target + value, primitive long/double 占两个 slot
                            return new Size(2, 2);
                        }
                    }))
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return setterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
