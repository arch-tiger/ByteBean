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
 * 针对无参方法的极速字节码实现
 * API: Object invoke(int index, Object instance)
 *
 * @author ZIJIDELU
 * @datetime 2026/1/15 18:18
 */
public class NoArgMethodInvokerImpl implements Implementation {

    private final Class<?> targetClass;
    private final List<Method> methods;

    public NoArgMethodInvokerImpl(Class<?> targetClass, List<Method> methods) {
        this.targetClass = targetClass;
        this.methods = methods;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, implementationContext, instrumentedMethod) -> {

            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

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
                // 为每个跳转目标设置栈映射帧
                // TableSwitch 消耗栈顶的 int，所以跳转目标处栈为空
                // 局部变量表不变：[this, index, instance]
                // 由于每个 case 分支要么 ARETURN 要么 GOTO，不会自然流向下一个 case
                // 所以每个跳转目标都需要使用 F_NEW 定义帧状态
                if (i == 0) {
                    mv.visitFrame(Opcodes.F_NEW, 3,
                            new Object[]{
                                    generatedClassName,
                                    Opcodes.INTEGER,
                                    "java/lang/Object"
                            },
                            0, new Object[0]);
                } else {
                    // 后续跳转目标也使用 F_NEW（最安全的方式）
                    // 虽然 MethodInvokerImpl 使用 F_SAME 是可行的，但由于 NoArgMethodInvokerImpl
                    // 有 GOTO defaultLabel 的跳转，使用 F_NEW 可以避免 offset 问题
                    mv.visitFrame(Opcodes.F_NEW, 3,
                            new Object[]{
                                    generatedClassName,
                                    Opcodes.INTEGER,
                                    "java/lang/Object"
                            },
                            0, new Object[0]);
                }

                // 检查方法参数个数，只有无参方法才能正常调用
                if (method.getParameterCount() != 0) {
                    // 有参数的方法跳转到 default 分支处理
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 无参方法：加载 instance 并强转
                mv.visitVarInsn(Opcodes.ALOAD, 2);
                mv.visitTypeInsn(Opcodes.CHECKCAST, owner);

                // B: 调用目标方法
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, method.getName(),
                        Type.getMethodDescriptor(method), false);

                // C: 处理返回值
                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }

                // D: 返回结果
                mv.visitInsn(Opcodes.ARETURN);
            }

            // --- 4. Default Case (必须补充，否则 VerifyError) ---
            mv.visitLabel(defaultLabel);
            // defaultLabel 同时接收来自 TableSwitch 和 GOTO 的跳转
            // 使用 F_NEW 明确指定帧状态，避免 F_SAME 的 offset 问题
            mv.visitFrame(Opcodes.F_NEW, 3,
                    new Object[]{
                            generatedClassName,
                            Opcodes.INTEGER,
                            "java/lang/Object"
                    },
                    0, new Object[0]);
            AsmUtil.throwIAEForMethod(mv);

            // --- 5. 必须返回 Size 对象 ---
            // 栈最大深度为 4 (CHECKCAST 1 + INVOKEVIRTUAL 0 + ACONST_NULL/boxIfNeeded 0 + default 分支的 throwIAEForMethod)
            // 局部变量表最大槽位为 4 (0=this, 1=index, 2=instance, 3=未使用但保留)
            return new ByteCodeAppender.Size(4, 4);
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
