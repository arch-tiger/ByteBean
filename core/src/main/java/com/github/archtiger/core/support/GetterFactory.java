package com.github.archtiger.core.support;

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
 * Getter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public class GetterFactory {


    /**
     * 创建 Getter
     *
     * @param targetClass 目标类
     * @param fieldName   字段名
     * @return Getter 实例
     * @throws Exception 如果创建失败
     */
    public static Getter createGetter(Class<?> targetClass, String fieldName) throws Exception {

        try {
            Field field = targetClass.getDeclaredField(fieldName);

            Class<? extends Getter> getterClass = new ByteBuddy()
                    .subclass(Getter.class)
                    .name(targetClass.getName() + "$$" + fieldName + "Getter")
                    .method(m -> m.getName().equals("get"))
                    .intercept(new Implementation.Simple(new ByteCodeAppender() {
                        @Override
                        public Size apply(MethodVisitor mv, Implementation.Context context, MethodDescription method) {
                            // 将 target 参数加载到栈上
                            mv.visitVarInsn(Opcodes.ALOAD, 1);
                            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetClass));
                            // 访问字段
                            mv.visitFieldInsn(Opcodes.GETFIELD,
                                    Type.getInternalName(targetClass),
                                    field.getName(),
                                    Type.getDescriptor(field.getType())
                            );
                            // 装箱 primitive
                            AsmUtil.boxIfNeeded(mv, field.getType());
                            // 返回
                            mv.visitInsn(Opcodes.ARETURN);
                            // maxStack = 2 (target + long/double 1 slot counts as 2)
                            return new Size(2, 2);
                        }
                    }))
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return getterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
