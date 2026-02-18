package com.github.archtiger.bytebean.core.invoker.constructor;

import com.github.archtiger.bytebean.core.utils.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 构造器访问实现类
 * <p>
 * 实现 ConstructorAccess.newInstance(int, Object, Object...) 方法
 * 使用 tableswitch 指令实现高效的构造器调用
 * 支持自动拆装箱功能
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public final class ConstructorByteCode implements Implementation {
    private final Class<?> targetClass;
    private final List<Constructor<?>> constructors;

    public ConstructorByteCode(Class<?> targetClass, List<Constructor<?>> constructors) {
        this.targetClass = targetClass;
        this.constructors = constructors;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 方法签名: Object newInstance(int index, Object... args)
            // 局部变量表布局:
            //   slot 0: this
            //   slot 1: int index
            //   slot 2: Object[] args
            // ============================================================

            // ============================================================
            // 步骤1: 加载索引参数，准备进行 switch 分支选择
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[constructors.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            // ============================================================
            // 步骤2: 生成 tableswitch 指令
            // ============================================================
            mv.visitTableSwitchInsn(0, constructors.size() - 1, defaultLabel, labels);

            // ============================================================
            // 步骤3: 为每个构造器生成对应的 case 分支
            // ============================================================
            for (int i = 0; i < constructors.size(); i++) {
                Constructor<?> constructor = constructors.get(i);
                mv.visitLabel(labels[i]);

                // ========================================================
                // 步骤3.2: 在堆上分配新对象
                // ========================================================
                // NEW 指令: 在堆上分配一个新对象，类型为 owner（目标类）
                mv.visitTypeInsn(Opcodes.NEW, owner);
                // DUP 指令: 复制栈顶的对象引用
                // 结果栈上有两个对象引用：一个用于调用 <init>，一个用于返回
                mv.visitInsn(Opcodes.DUP);

                // ========================================================
                // 步骤3.3: 加载构造器参数并自动拆箱
                // ========================================================
                Class<?>[] paramTypes = constructor.getParameterTypes();
                for (int j = 0; j < paramTypes.length; j++) {
                    // 加载 Object[] args
                    mv.visitVarInsn(Opcodes.ALOAD, 2);
                    // 加载数组索引 j
                    switch (j) {
                        case 0 -> mv.visitInsn(Opcodes.ICONST_0);
                        case 1 -> mv.visitInsn(Opcodes.ICONST_1);
                        case 2 -> mv.visitInsn(Opcodes.ICONST_2);
                        case 3 -> mv.visitInsn(Opcodes.ICONST_3);
                        case 4 -> mv.visitInsn(Opcodes.ICONST_4);
                        case 5 -> mv.visitInsn(Opcodes.ICONST_5);
                        default -> mv.visitIntInsn(Opcodes.BIPUSH, j);
                    }
                    // AALOAD: 从数组中加载元素 args[j]
                    mv.visitInsn(Opcodes.AALOAD);
                    // 自动拆箱或类型转换
                    AsmUtil.unboxOrCast(mv, paramTypes[j]);
                }

                // ========================================================
                // 步骤3.4: 调用构造器 <init>
                // ========================================================
                // INVOKESPECIAL 指令: 调用构造器方法
                mv.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        owner,
                        "<init>",
                        Type.getConstructorDescriptor(constructor),
                        false
                );

                // ========================================================
                // 步骤3.5: 返回新创建的实例
                // ========================================================
                // ARETURN 指令: 从操作数栈顶弹出引用并返回
                mv.visitInsn(Opcodes.ARETURN);
            }

            // ============================================================
            // 步骤4: 处理 default 分支（索引越界）
            // ============================================================
            mv.visitLabel(defaultLabel);

            // 抛出 IllegalArgumentException 异常
            AsmUtil.throwIAEForConstructor(mv);

            // ============================================================
            // 返回 Size.ZERO，由 ByteBuddy 自动计算栈和局部变量大小
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
