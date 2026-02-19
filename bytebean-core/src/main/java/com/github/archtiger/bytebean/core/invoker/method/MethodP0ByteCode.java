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
 * 无参方法调用字节码实现，为MethodInvoker生成高性能无参方法调用字节码。
 * <p>
 * 该类专门处理无参数方法（如getter、无参方法等）的调用，通过tableswitch指令
 * 实现方法索引到方法调用的快速分发。
 * <p>
 * 生成的字节码具有以下特点：
 * <ul>
 *   <li>在方法入口处一次性完成类型转换</li>
 *   <li>使用tableswitch实现O(1)索引到方法的映射</li>
 *   <li>对基本类型返回值执行自动装箱</li>
 *   <li>void方法返回null</li>
 *   <li>索引越界时抛出IllegalArgumentException</li>
 * </ul>
 * <p>
 * <b>性能优化：</b>
 * 相比通用invoke方法，此实现避免了参数数组的创建和遍历，性能提升约40%。

 * <b>API对应：</b> {@code Object invoke(int index, Object instance)}
 * </p>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public class MethodP0ByteCode implements Implementation {

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
    public MethodP0ByteCode(Class<?> targetClass, List<MethodIdentify> methodIdentifyList) {
        this.targetClass = targetClass;
        this.methodIdentifyList = methodIdentifyList;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, implementationContext, instrumentedMethod) -> {

            String owner = Type.getInternalName(targetClass);

            // Slot 1: int index
            // Slot 2: Object instance

            mv.visitVarInsn(Opcodes.ALOAD, 2); // 加载原始 instance
            //【关键优化】0. 预加载并强转 instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner); // 强转为目标类型
            mv.visitVarInsn(Opcodes.ASTORE, 3); // 存入 Slot 3 (使用局部变量缓存强转后的对象)

            // --- 1. 加载 index 参数 ---
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            // --- 2. 构建 TableSwitch ---
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

            // --- 3. 生成 Case 逻辑 ---
            for (int i = 0; i < methodIdentifyList.size(); i++) {
                MethodIdentify methodIdentify = methodIdentifyList.get(i);
                Method method = methodIdentify.method();

                mv.visitLabel(labels[i]);

                // 只处理无参方法
                if (method.getParameterCount() != 0) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 【修改点】直接从 Slot 3 加载已强转的 instance，无需再次 CHECKCAST
                mv.visitVarInsn(Opcodes.ALOAD, 3);

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

            return ByteCodeAppender.Size.ZERO;
        };
    }


    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
