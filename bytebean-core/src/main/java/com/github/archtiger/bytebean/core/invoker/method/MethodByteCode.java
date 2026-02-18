package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.core.model.MethodIdentify;
import com.github.archtiger.bytebean.core.utils.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 通用方法调用字节码实现
 * <p>
 *
 * @author ZIJIDELU
 */
public final class MethodByteCode implements Implementation {

    private final Class<?> targetClass;
    private final List<MethodIdentify> methodIdentifyList;



    public MethodByteCode(Class<?> targetClass, List<MethodIdentify> methodIdentifyList) {
        this.targetClass = targetClass;
        this.methodIdentifyList = methodIdentifyList;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, implementationContext, instrumentedMethod) -> {

            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 1. 预加载并强转 Instance (Slot 2 -> Slot 4)
            // 【已优化】强转在 Switch 外部，只执行一次
            // ============================================================
            mv.visitVarInsn(Opcodes.ALOAD, 2); // 加载 Object instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 4); // 存入 Slot 4 (避开 args 数组 Slot 3)

            // ============================================================
            // 2. 加载 Index 并初始化 Switch
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[methodIdentifyList.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            if (!methodIdentifyList.isEmpty()) {
                mv.visitTableSwitchInsn(0, methodIdentifyList.size() - 1, defaultLabel, labels);
            } else {
                mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
            }

            // ============================================================
            // 3. 生成 Case 分支
            // ============================================================
            for (int i = 0; i < methodIdentifyList.size(); i++) {
                MethodIdentify methodIdentify = methodIdentifyList.get(i);
                Method method = methodIdentify.method();
                mv.visitLabel(labels[i]);

                // 加载强转后的 Target Instance
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // 加载参数 (Varargs 逻辑: Object[] args)
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int j = 0; j < paramTypes.length; j++) {
                    // A. 加载 Args 数组
                    mv.visitVarInsn(Opcodes.ALOAD, 3);

                    // B. 加载索引 (直接 Switch，优化为单字节指令)
                    switch (j) {
                        case 0 -> mv.visitInsn(Opcodes.ICONST_0);
                        case 1 -> mv.visitInsn(Opcodes.ICONST_1);
                        case 2 -> mv.visitInsn(Opcodes.ICONST_2);
                        case 3 -> mv.visitInsn(Opcodes.ICONST_3);
                        case 4 -> mv.visitInsn(Opcodes.ICONST_4);
                        case 5 -> mv.visitInsn(Opcodes.ICONST_5);
                        default -> mv.visitIntInsn(Opcodes.BIPUSH, j);
                    }

                    // C. 读取数组元素
                    mv.visitInsn(Opcodes.AALOAD);

                    // D. 参数适配 (拆箱或强转)
                    AsmUtil.unboxOrCast(mv, paramTypes[j]);
                }

                // 调用目标方法
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
            AsmUtil.throwIAEForMethod(mv);

            // ============================================================
            // 5. 返回 ZERO，由 ByteBuddy/ASM 自动计算 maxStack/maxLocals
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
