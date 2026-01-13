package com.github.archtiger.core.access.constructor;

import com.github.archtiger.core.support.AsmUtil;
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
public final class ConstructorAccessImpl implements Implementation {
    private final Class<?> targetClass;
    private final List<Constructor<?>> constructors;

    public ConstructorAccessImpl(Class<?> targetClass, List<Constructor<?>> constructors) {
        this.targetClass = targetClass;
        this.constructors = constructors;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

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
                // 步骤3.1: 插入 StackMapFrame
                // ========================================================
                mv.visitFrame(Opcodes.F_NEW, 3,
                        new Object[]{
                                generatedClassName,
                                Opcodes.INTEGER,
                                "[Ljava/lang/Object;"
                        },
                        0, new Object[0]);

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
                    mv.visitIntInsn(Opcodes.BIPUSH, j);
                    // AALOAD: 从数组中加载元素 args[j]
                    mv.visitInsn(Opcodes.AALOAD);
                    // 自动拆箱或类型转换
                    // 如果参数是基本类型，会调用对应的包装类的 valueOf 方法进行拆箱
                    // 如果参数是引用类型，会进行 CHECKCAST 类型检查
                    AsmUtil.unboxOrCast(mv, paramTypes[j]);
                }

                // ========================================================
                // 步骤3.4: 调用构造器 <init>
                // ========================================================
                // INVOKESPECIAL 指令: 调用构造器方法
                // 参数顺序依次从栈顶取（由上面的循环生成）
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
            mv.visitFrame(Opcodes.F_NEW, 3,
                    new Object[]{
                            generatedClassName,
                            Opcodes.INTEGER,
                            "[Ljava/lang/Object;"
                    },
                    0, new Object[0]);

            // 抛出 IllegalArgumentException 异常
            AsmUtil.throwIAEForConstructor(mv);

            // ============================================================
            // 步骤5: 计算并返回方法的栈和局部变量大小
            // ============================================================
            // 计算最大栈深度和局部变量数量
            int maxStack = 0;
            int maxLocals = 3; // this + index + args

            // 遍历所有构造器，找出最大的栈深度需求
            // 参考 ByteCodeSizeUtil.forConstructorInvoker 和 MethodInvokerImpl 的实现
            for (Constructor<?> constructor : constructors) {
                Class<?>[] params = constructor.getParameterTypes();

                // NEW + DUP = 2 (对象引用)
                int constructorStack = 2;

                // 参数加载和拆箱
                // 参考 ByteCodeSizeUtil.forMethodInvoker 的计算方式
                for (Class<?> param : params) {
                    // 参数值本身占用的栈空间
                    constructorStack += Type.getType(param).getSize();
                    // 拆箱操作需要额外栈空间
                    if (param.isPrimitive()) {
                        constructorStack += 1; // CHECKCAST + valueOf 调用
                    }
                }

                // 额外计算 Object[] args 加载栈空间
                // 参考 ByteCodeSizeUtil.forMethodInvoker: 如果有参数，需要额外栈空间
                if (params.length > 0) {
                    // 在加载参数时，需要 ALOAD args(1) + BIPUSH index(1) + AALOAD(1)
                    // 但这些是顺序执行的，栈空间可以复用
                    // 参考 forMethodInvoker 的实现，只加 2 (ALOAD args + BIPUSH index)
                    constructorStack += 2;
                }

                if (constructorStack > maxStack) {
                    maxStack = constructorStack;
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
