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
 * 基本类型返回方法调用实现类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
public final class PrimitiveMethodByteCode implements Implementation {
    private final Class<?> targetClass;
    private final List<Method> methods;
    private final Class<?> primitiveType;

    public PrimitiveMethodByteCode(Class<?> targetClass, List<Method> methods, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.methods = methods;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 1. 预加载并强转 Instance (Slot 2 -> Slot 4)
            // ============================================================
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 4);

            // ============================================================
            // 2. 加载 Index 并初始化 Switch
            // ============================================================
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

            // ============================================================
            // 3. 生成 Case 分支
            // ============================================================
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                mv.visitLabel(labels[i]);

                // 防御性检查：只处理返回值类型匹配的方法
                if (method.getReturnType() != primitiveType) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 加载目标对象
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // 循环加载参数
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int j = 0; j < paramTypes.length; j++) {
                    // A. 加载 Args 数组
                    mv.visitVarInsn(Opcodes.ALOAD, 3);

                    // B. 加载索引 (优化为单字节指令)
                    switch (j) {
                        case 0:
                            mv.visitInsn(Opcodes.ICONST_0);
                            break;
                        case 1:
                            mv.visitInsn(Opcodes.ICONST_1);
                            break;
                        case 2:
                            mv.visitInsn(Opcodes.ICONST_2);
                            break;
                        case 3:
                            mv.visitInsn(Opcodes.ICONST_3);
                            break;
                        case 4:
                            mv.visitInsn(Opcodes.ICONST_4);
                            break;
                        case 5:
                            mv.visitInsn(Opcodes.ICONST_5);
                            break;
                        default:
                            mv.visitIntInsn(Opcodes.BIPUSH, j);
                            break;
                    }

                    // C. 读取数组元素
                    mv.visitInsn(Opcodes.AALOAD);

                    // D. 参数适配 (拆箱或强转)
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

            // ============================================================
            // 4. Default 分支
            // ============================================================
            mv.visitLabel(defaultLabel);
            AsmUtil.throwIAEForMethod(mv);

            // ============================================================
            // 5. 返回 ZERO，由 ByteBuddy/ASM 自动计算
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
