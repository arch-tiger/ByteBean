package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.core.support.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 单参数方法调用字节码实现
 * API: Object invoke(int index, Object instance, Object arg)
 *
 * @author ZIJIDELU
 * @datetime 2026/1/16 12:11
 */
public class MethodP1ByteCode implements Implementation {

    private final Class<?> targetClass;
    private final List<Method> methods;

    public MethodP1ByteCode(Class<?> targetClass, List<Method> methods) {
        this.targetClass = targetClass;
        this.methods = methods;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, implementationContext, instrumentedMethod) -> {

            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 【关键优化】0. 预加载并强转 instance
            // 注意：Slot 3 被 arg 占用，所以这里必须存入 Slot 4
            // ============================================================
            mv.visitVarInsn(Opcodes.ALOAD, 2); // Slot 2: Object instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 4); // Slot 4: TargetClass instance (Casted)

            // --- 1. 加载 index 并构建 TableSwitch ---
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            if (!methods.isEmpty()) {
                mv.visitTableSwitchInsn(0, methods.size() - 1, defaultLabel, labels);
            } else {
                mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
            }

            // --- 2. 生成 Case 逻辑 ---
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);

                mv.visitLabel(labels[i]);

                if (method.getParameterCount() != 1) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                Class<?> paramType = method.getParameterTypes()[0];

                // A: 【优化】直接从 Slot 4 加载已强转的 instance
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // B: 加载 arg (Slot 3)
                mv.visitVarInsn(Opcodes.ALOAD, 3);

                // C: 参数适配 (必须在 Case 内部，因为类型不同)
                AsmUtil.unboxOrCast(mv, paramType);

                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner,
                        method.getName(),
                        Type.getMethodDescriptor(method), false);

                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }

                mv.visitInsn(Opcodes.ARETURN);
            }

            // --- 3. Default Case ---
            mv.visitLabel(defaultLabel);
            AsmUtil.throwIAEForMethod(mv);

            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
