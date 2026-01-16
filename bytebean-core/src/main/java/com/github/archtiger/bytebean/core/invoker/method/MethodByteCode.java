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
 * 方法调用实现类（修复 Stack Overflow 版本）
 * <p>
 * 修复内容：
 * 恢复并修正 maxStack 的精确计算逻辑。
 * 之前简化计算导致的 maxStack 偏小（例如只有 3/4），无法满足 long/double 参数或多参数场景。
 * <p>
 * 优化策略：
 * 1. 单字节指令优化（ALOAD_0~3, ICONST_0~5）保持不变。
 * 2. maxStack 计算采用模拟栈策略：
 *    - 基础：target (1)。
 *    - 循环加载参数：每个参数的峰值 = 当前已有栈深 + 2 (array ref + index)。
 *    - 最终：取所有分支的最大值。
 *
 * @author ZIJIDELU
 */
public final class MethodByteCode implements Implementation {
    private final Class<?> targetClass;
    private final List<Method> methods;

    public MethodByteCode(Class<?> targetClass, List<Method> methods) {
        this.targetClass = targetClass;
        this.methods = methods;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 1. 预处理实例 (Slot 2 -> ALOAD_2 自动优化)
            // ============================================================
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            // Slot 4 只能用宽指令
            mv.visitVarInsn(Opcodes.ASTORE, 4);

            // ============================================================
            // 2. Switch 初始化 (Slot 1 -> ILOAD_1 自动优化)
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            mv.visitTableSwitchInsn(0, methods.size() - 1, defaultLabel, labels);

            // ============================================================
            // 3. 生成 Case 分支
            // ============================================================
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                mv.visitLabel(labels[i]);

                // --- 高性能 StackMapFrame ---
                if (i == 0) {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{owner}, 0, null);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }

                // 加载目标实例 (Slot 4)
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // 循环加载参数
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int j = 0; j < paramTypes.length; j++) {
                    // 加载数组引用 (Slot 3 -> ALOAD_3 自动优化)
                    mv.visitVarInsn(Opcodes.ALOAD, 3);

                    // --- 核心：手动优化常量加载 ---
                    switch (j) {
                        case 0: mv.visitInsn(Opcodes.ICONST_0); break;
                        case 1: mv.visitInsn(Opcodes.ICONST_1); break;
                        case 2: mv.visitInsn(Opcodes.ICONST_2); break;
                        case 3: mv.visitInsn(Opcodes.ICONST_3); break;
                        case 4: mv.visitInsn(Opcodes.ICONST_4); break;
                        case 5: mv.visitInsn(Opcodes.ICONST_5); break;
                        default: mv.visitIntInsn(Opcodes.BIPUSH, j); break;
                    }

                    mv.visitInsn(Opcodes.AALOAD);
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

                // 处理返回值
                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }
                mv.visitInsn(Opcodes.ARETURN);
            }

            // ============================================================
            // 4. Default 分支
            // ============================================================
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            AsmUtil.throwIAEForMethod(mv);

            // ============================================================
            // 5. 修正后的 Stack Size 计算 (关键修复)
            // ============================================================
            int maxLocals = 5; // this + index + instance + args + castedInstance

            // 默认异常处理通常需要 4 (new, dup, invokespecial)
            int maxStack = 4;

            for (Method method : methods) {
                int currentStack = 1; // 初始：ALOAD 4 (target) 压入栈顶，栈深=1

                Class<?>[] paramTypes = method.getParameterTypes();
                for (Class<?> pType : paramTypes) {
                    // 模拟参数加载过程：
                    // 1. ALOAD_3 (args)  -> +1
                    // 2. ICONST_X (index) -> +1
                    // 此时栈上临时有：target + previous_params + args + index
                    // 栈深峰值 = 当前栈深 + 2
                    int peak = currentStack + 2;
                    if (peak > maxStack) {
                        maxStack = peak;
                    }

                    // 3. AALOAD       -> -2 (弹出args, index) +1 (压入value) = 净-1
                    // 4. unboxOrCast  -> 0 (Ref->Primitive, 栈深不变)
                    // 执行完后，栈上新增了该参数
                    int paramSize = (pType == long.class || pType == double.class) ? 2 : 1;
                    currentStack += paramSize;
                }

                // 方法调用前一刻的栈深也可能很大
                if (currentStack > maxStack) {
                    maxStack = currentStack;
                }
            }

            return new ByteCodeAppender.Size(maxStack, maxLocals);
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
