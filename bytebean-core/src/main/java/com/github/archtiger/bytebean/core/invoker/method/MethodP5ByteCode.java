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
 * 五参数方法调用字节码实现
 * API: Object invoke(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5)
 *
 * @author ZIJIDELU
 * @datetime 2026/1/19
 */
public class MethodP5ByteCode implements Implementation {

    private final Class<?> targetClass;
    private final List<Method> methods;

    public MethodP5ByteCode(Class<?> targetClass, List<Method> methods) {
        this.targetClass = targetClass;
        this.methods = methods;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, implementationContext, instrumentedMethod) -> {

            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 【关键优化】0. 预加载并强转 instance
            // 注意：Slot 3, 4, 5, 6, 7 被 arg1, arg2, arg3, arg4, arg5 占用，所以这里必须存入 Slot 8
            // ============================================================
            mv.visitVarInsn(Opcodes.ALOAD, 2); // Slot 2: Object instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 8); // Slot 8: TargetClass instance (Casted)

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

                if (method.getParameterCount() != 5) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                Class<?>[] paramTypes = method.getParameterTypes();

                // A: 【优化】直接从 Slot 8 加载已强转的 instance
                mv.visitVarInsn(Opcodes.ALOAD, 8);

                // B: 加载 arg1 (Slot 3)
                mv.visitVarInsn(Opcodes.ALOAD, 3);
                // C: 参数适配 (必须在 Case 内部，因为类型不同)
                AsmUtil.unboxOrCast(mv, paramTypes[0]);

                // D: 加载 arg2 (Slot 4)
                mv.visitVarInsn(Opcodes.ALOAD, 4);
                // E: 参数适配
                AsmUtil.unboxOrCast(mv, paramTypes[1]);

                // F: 加载 arg3 (Slot 5)
                mv.visitVarInsn(Opcodes.ALOAD, 5);
                // G: 参数适配
                AsmUtil.unboxOrCast(mv, paramTypes[2]);

                // H: 加载 arg4 (Slot 6)
                mv.visitVarInsn(Opcodes.ALOAD, 6);
                // I: 参数适配
                AsmUtil.unboxOrCast(mv, paramTypes[3]);

                // J: 加载 arg5 (Slot 7)
                mv.visitVarInsn(Opcodes.ALOAD, 7);
                // K: 参数适配
                AsmUtil.unboxOrCast(mv, paramTypes[4]);

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
