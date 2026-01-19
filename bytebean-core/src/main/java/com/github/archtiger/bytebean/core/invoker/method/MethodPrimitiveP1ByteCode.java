package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.core.model.MethodIdentify;
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
 * 单参数基本类型方法调用实现类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
public final class MethodPrimitiveP1ByteCode implements Implementation {
    private final Class<?> targetClass;
    private final List<MethodIdentify> identifyMethodList;
    private final Class<?> primitiveType;

    public MethodPrimitiveP1ByteCode(Class<?> targetClass, List<MethodIdentify> identifyMethodList, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.identifyMethodList = identifyMethodList;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 1. 计算 Slot 分配
            // ============================================================
            // Slot 0=this, 1=index, 2=instance
            // Slot 3 是基本类型参数。
            // long/double 占 2 个 slot，其他占 1 个
            int valueSlotSize = (primitiveType == long.class || primitiveType == double.class) ? 2 : 1;
            // castedInstance 紧跟在 value 之后
            int castedInstanceSlot = 3 + valueSlotSize;

            // ============================================================
            // 2. 预加载并强转 Instance
            // ============================================================
            mv.visitVarInsn(Opcodes.ALOAD, 2); // 加载 Object instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, castedInstanceSlot); // 存入计算好的 Slot

            // ============================================================
            // 3. 加载 Index 并初始化 Switch
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[identifyMethodList.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            if (!identifyMethodList.isEmpty()) {
                mv.visitTableSwitchInsn(0, identifyMethodList.size() - 1, defaultLabel, labels);
            } else {
                mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
            }

            // ============================================================
            // 4. 生成 Case 分支
            // ============================================================
            for (int i = 0; i < identifyMethodList.size(); i++) {
                Method method = identifyMethodList.get(i).method();
                mv.visitLabel(labels[i]);

                // 【必须保留】防御性检查
                // 基本类型指令（如 ILOAD, LLOAD）如果类型不匹配，会导致 VerifyError 或更严重的 JVM 行为
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length != 1 || paramTypes[0] != primitiveType) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue; // 跳过生成，直接进入下一个 Case
                }

                // A. 加载强转后的 Target Instance
                mv.visitVarInsn(Opcodes.ALOAD, castedInstanceSlot);

                // B. 加载基本类型参数 (固定在 Slot 3)
                mv.visitVarInsn(AsmUtil.getLoadOpcode(primitiveType), 3);

                // C. 调用方法
                mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        owner,
                        method.getName(),
                        Type.getMethodDescriptor(method),
                        false
                );

                // D. 处理返回值
                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }

                mv.visitInsn(Opcodes.ARETURN);
            }

            // ============================================================
            // 5. Default 分支
            // ============================================================
            mv.visitLabel(defaultLabel);
            // ASM 自动计算模式下，不需要手动插入 visitFrame
            AsmUtil.throwIAEForMethod(mv);

            // ============================================================
            // 6. 返回 ZERO，由 ASM 自动计算
            // ============================================================
            // 因为存在 GOTO default 的分支，手动计算 maxStack 非常麻烦且易错。
            // COMPUTE_MAXS (由 Size.ZERO 触发) 是处理这种控制流的最优解。
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
