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
 * 针对无参方法的极速字节码实现（极致优化版）
 * API: Object invoke(int index, Object instance)
 *
 * @author ZIJIDELU
 * @datetime 2026/1/15 18:18
 */
public class MethodP0ByteCode implements Implementation {

    private final Class<?> targetClass;
    private final List<Method> methods;

    public MethodP0ByteCode(Class<?> targetClass, List<Method> methods) {
        this.targetClass = targetClass;
        this.methods = methods;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, implementationContext, instrumentedMethod) -> {

            String owner = Type.getInternalName(targetClass);

            // --- 1. 加载 index 参数 ---
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            // --- 2. 构建 TableSwitch ---
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

            // --- 3. 生成 Case 逻辑 ---
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                mv.visitLabel(labels[i]);

                // 判断是否有参方法
                if (method.getParameterCount() != 0) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 加载 instance 并强转
                mv.visitVarInsn(Opcodes.ALOAD, 2);
                // 微优化：如果 targetClass 是 Object.class，CHECKCAST 是多余的，但在 ASM 中通常保留即可，
                // JIT 会自动将其优化掉。
                mv.visitTypeInsn(Opcodes.CHECKCAST, owner);

                // 调用目标方法
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, method.getName(),
                        Type.getMethodDescriptor(method), false);

                // 处理返回值
                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }

                // 返回结果
                mv.visitInsn(Opcodes.ARETURN);
            }

            // --- 4. Default Case ---
            mv.visitLabel(defaultLabel);
            AsmUtil.throwIAEForMethod(mv);

            // --- 5. 返回 Size 对象 ---
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
