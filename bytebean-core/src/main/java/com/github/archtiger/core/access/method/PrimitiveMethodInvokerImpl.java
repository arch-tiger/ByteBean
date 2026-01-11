package com.github.archtiger.core.access.method;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 基本类型方法调用实现类
 * <p>
 * 实现 MethodAccess 的基本类型返回方法（intInvoke, longInvoke 等）
 * 直接返回基本类型，避免装箱
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
public final class PrimitiveMethodInvokerImpl implements Implementation {
    private final Class<?> targetClass;
    private final List<Method> methods;
    private final Class<?> primitiveType;

    public PrimitiveMethodInvokerImpl(Class<?> targetClass, List<Method> methods, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.methods = methods;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

            // slot: 0=this, 1=index, 2=instance, 3=arguments, 4=castedInstance
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 4);

            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            mv.visitTableSwitchInsn(0, methods.size() - 1, defaultLabel, labels);

            // case 0..N
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                mv.visitLabel(labels[i]);
                mv.visitFrame(Opcodes.F_NEW, 5,
                        new Object[]{
                                generatedClassName,
                                Opcodes.INTEGER,
                                "java/lang/Object",
                                "[Ljava/lang/Object;",
                                owner
                        },
                        0, new Object[0]);

                // 只处理返回指定基本类型的方法，其他类型跳转到 default 分支
                if (method.getReturnType() != primitiveType) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 加载目标对象
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // 加载方法参数
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int j = 0; j < paramTypes.length; j++) {
                    mv.visitVarInsn(Opcodes.ALOAD, 3);   // Object[] arguments
                    mv.visitIntInsn(Opcodes.BIPUSH, j);
                    mv.visitInsn(Opcodes.AALOAD);         // arguments[j]
                    AsmUtil.unboxOrCast(mv, paramTypes[j]);
                }

                // 调用方法
                mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        owner,
                        method.getName(),
                        Type.getMethodDescriptor(method),
                        false
                );

                // 根据基本类型返回对应的 RETURN 指令（避免装箱）
                mv.visitInsn(AsmUtil.getReturnOpcode(primitiveType));
            }

            // default
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_NEW, 5,
                    new Object[]{
                            generatedClassName,
                            Opcodes.INTEGER,
                            "java/lang/Object",
                            "[Ljava/lang/Object;",
                            owner
                    },
                    0, new Object[0]);

            AsmUtil.throwIAEForMethod(mv);

            // 计算最大栈深度
            int maxStack = 0;
            int maxLocals = 5;

            for (Method method : methods) {
                if (method.getReturnType() == primitiveType) {
                    Class<?>[] params = method.getParameterTypes();
                    int methodStack = 1; // target
                    for (Class<?> p : params) {
                        methodStack += ByteCodeSizeUtil.slotSize(p);
                        if (p.isPrimitive()) {
                            methodStack += 1; // 拆箱临时栈
                        }
                    }
                    if (params.length > 0) {
                        methodStack += 2; // ALOAD args + BIPUSH index
                    }
                    
                    if (methodStack > maxStack) {
                        maxStack = methodStack;
                    }
                }
            }

            if (maxStack < 4) {
                maxStack = 4;
            }

            return new ByteCodeAppender.Size(maxStack, maxLocals);
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
