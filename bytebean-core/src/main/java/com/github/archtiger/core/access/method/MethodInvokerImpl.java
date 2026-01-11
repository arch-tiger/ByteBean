package com.github.archtiger.core.access.method;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法调用实现类
 * <p>
 * 实现 MethodAccess.invoke(int, Object, Object...) 方法
 * 使用 tableswitch 指令实现高效的方法调用
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
public final class MethodInvokerImpl implements Implementation {
    private final Class<?> targetClass;
    private final List<Method> methods;

    public MethodInvokerImpl(Class<?> targetClass, List<Method> methods) {
        this.targetClass = targetClass;
        this.methods = methods;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

            // ============================================================
            // 步骤1: 将参数中的 instance 强制转换为目标类型
            // ============================================================
            // 方法签名: Object invoke(int index, Object instance, Object... arguments)
            // 局部变量表布局:
            //   slot 0: this
            //   slot 1: int index
            //   slot 2: Object instance
            //   slot 3: Object[] arguments
            //   slot 4: Target castedInstance (临时变量)

            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 4);

            // ============================================================
            // 步骤2: 加载索引参数，准备进行 switch 分支选择
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            // ============================================================
            // 步骤3: 生成 tableswitch 指令
            // ============================================================
            mv.visitTableSwitchInsn(0, methods.size() - 1, defaultLabel, labels);

            // ============================================================
            // 步骤4: 为每个方法生成对应的 case 分支
            // ============================================================
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                mv.visitLabel(labels[i]);

                // ========================================================
                // 步骤4.1: 插入 StackMapFrame
                // ========================================================
                mv.visitFrame(Opcodes.F_NEW, 5,
                        new Object[]{
                                generatedClassName,
                                Opcodes.INTEGER,
                                "java/lang/Object",
                                "[Ljava/lang/Object;",
                                owner
                        },
                        0, new Object[0]);

                // ========================================================
                // 步骤4.2: 加载目标对象
                // ========================================================
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // ========================================================
                // 步骤4.3: 加载方法参数
                // ========================================================
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int j = 0; j < paramTypes.length; j++) {
                    mv.visitVarInsn(Opcodes.ALOAD, 3);   // Object[] arguments
                    mv.visitIntInsn(Opcodes.BIPUSH, j);
                    mv.visitInsn(Opcodes.AALOAD);         // arguments[j]
                    AsmUtil.unboxOrCast(mv, paramTypes[j]); // 拆箱/类型转换
                }

                // ========================================================
                // 步骤4.4: 调用方法
                // ========================================================
                mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        owner,
                        method.getName(),
                        Type.getMethodDescriptor(method),
                        false
                );

                // ========================================================
                // 步骤4.5: 处理返回值
                // ========================================================
                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }

                // ========================================================
                // 步骤4.6: 返回结果
                // ========================================================
                mv.visitInsn(Opcodes.ARETURN);
            }

            // ============================================================
            // 步骤5: 处理 default 分支（索引越界）
            // ============================================================
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_NEW, 5,
                    new Object[]{
                            generatedClassName,
                            Opcodes.INTEGER,
                            "java/lang/Object",
                            "[Ljava/lang/Object;",
                            owner
                    },
                    0, new Object[0]);

            AsmUtil.throwIAEForMethod(mv);

            // ============================================================
            // 步骤6: 计算并返回方法的栈和局部变量大小
            // ============================================================
            // 计算最大栈深度和局部变量数量
            int maxStack = 0;
            int maxLocals = 5; // this + index + instance + arguments + castedInstance

            // 遍历所有方法，找出最大的栈深度需求
            for (Method method : methods) {
                // 直接计算栈深度，参考 ByteCodeSizeUtil.forMethodInvoker 的实现
                boolean returnsVoid = method.getReturnType() == void.class;
                Class<?>[] params = method.getParameterTypes();
                
                int methodStack = 1; // target
                for (Class<?> p : params) {
                    methodStack += ByteCodeSizeUtil.slotSize(p);
                    if (p.isPrimitive()) {
                        methodStack += 1; // 拆箱临时栈
                    }
                }
                if (params.length > 0) {
                    methodStack += 2; // ALOAD args + BIPUSH index
                }
                if (!returnsVoid && method.getReturnType().isPrimitive()) {
                    methodStack += 1; // 返回值装箱
                }
                
                if (methodStack > maxStack) {
                    maxStack = methodStack;
                }
            }

            // throwIAE 也需要栈空间，确保足够
            if (maxStack < 4) {
                maxStack = 4;
            }

            return new ByteCodeAppender.Size(maxStack, maxLocals);
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
