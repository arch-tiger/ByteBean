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
 * 单参数方法调用字节码实现，为MethodInvoker生成高性能单参数方法调用字节码。
 * <p>
 * 该类专门处理单参数方法的调用，通过tableswitch指令实现方法索引到方法调用的快速分发。
 * 相比通用invoke方法，避免了参数数组的创建开销。
 * <p>
 * 生成的字节码具有以下特点：
 * <ul>
 *   <li>在方法入口处一次性完成类型转换</li>
 *   <li>使用tableswitch实现O(1)索引到方法的映射</li>
 *   <li>对基本类型参数执行自动拆箱</li>
 *   <li>对基本类型返回值执行自动装箱</li>
 *   <li>void方法返回null</li>
 *   <li>索引越界时抛出IllegalArgumentException</li>
 * </ul>
 * <p>
 * <b>性能优化：</b>
 * 相比通用invoke方法，此实现避免了参数数组的创建和遍历，性能提升约30%。

 * <p>
 * <b>API对应：</b> {@code Object invoke(int index, Object instance, Object arg)}
 * </p>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public class MethodP1ByteCode implements Implementation {

    /**
     * 目标类，用于类型检查和字节码生成。
     */
    private final Class<?> targetClass;

    /**
     * 方法标识列表，按索引顺序排列。
     */
    private final List<MethodIdentify> methodIdentifyList;

    /**
     * 构造函数。
     *
     * @param targetClass         目标类
     * @param methodIdentifyList  方法标识列表
     */
    public MethodP1ByteCode(Class<?> targetClass, List<MethodIdentify> methodIdentifyList) {
        this.targetClass = targetClass;
        this.methodIdentifyList = methodIdentifyList;
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
            Label[] labels = new Label[methodIdentifyList.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            if (!methodIdentifyList.isEmpty()) {
                mv.visitTableSwitchInsn(methodIdentifyList.get(0).index(), methodIdentifyList.get(methodIdentifyList.size() - 1).index(), defaultLabel, labels);
            } else {
                mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
            }

            // --- 2. 生成 Case 逻辑 ---
            for (int i = 0; i < methodIdentifyList.size(); i++) {
                MethodIdentify methodIdentify = methodIdentifyList.get(i);
                Method method = methodIdentify.method();

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
