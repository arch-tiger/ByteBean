package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.utils.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 字段setter字节码实现，为FieldInvoker生成高性能字段写入字节码。
 * <p>
 * 该类使用ASM字节码操作技术，通过tableswitch指令实现字段索引到字段写入的快速分发。
 * 生成的字节码具有以下特点：
 * <ul>
 *   <li>在方法入口处一次性完成类型转换，避免在switch分支内重复检查</li>
 *   <li>使用tableswitch实现O(1)索引到字段的映射</li>
 *   <li>对基本类型字段执行自动拆箱</li>
 *   <li>自动跳过final字段，拒绝修改</li>
 *   <li>索引越界或修改final字段时抛出IllegalArgumentException</li>
 * </ul>
 * <p>
 * <b>性能优化：</b>
 * 相比反射调用，此字节码实现可提供3-5倍的性能提升。
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public final class FieldSetterByteCode implements Implementation {

    /**
     * 目标类，用于类型检查和字节码生成。
     */
    private final Class<?> targetClass;

    /**
     * 字段列表，按索引顺序排列，用于生成switch分支。
     */
    private final List<Field> fields;

    /**
     * 构造函数。
     *
     * @param targetClass 目标类
     * @param fields      字段列表
     */
    public FieldSetterByteCode(Class<?> targetClass, List<Field> fields) {
        this.targetClass = targetClass;
        this.fields = fields;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            // 获取目标类的内部名称
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 步骤1: 将 instance 强制转换为目标类型
            // ============================================================
            // 方法签名: void set(int index, Object instance, Object value)
            // 局部变量表:
            //   slot 0: this
            //   slot 1: int index
            //   slot 2: Object instance
            //   slot 3: Object value
            //   slot 4: Target castedInstance

            mv.visitVarInsn(Opcodes.ALOAD, 2);           // 加载 instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner); // 类型转换
            mv.visitVarInsn(Opcodes.ASTORE, 4);        // 存储到 slot 4

            // ============================================================
            // 步骤2: 加载索引参数
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            // ============================================================
            // 步骤3: 生成 tableswitch 指令
            // ============================================================
            Label defaultLabel = new Label();
            Label[] labels = new Label[fields.size()];
            for (int i = 0; i < labels.length; i++) labels[i] = new Label();

            mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

            // ============================================================
            // 步骤4: 为每个字段生成对应的 case 分支
            // ============================================================
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                mv.visitLabel(labels[i]);

                // 拒绝修改 final 字段
                if (Modifier.isFinal(f.getModifiers())) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // ========================================================
                // 步骤4.2: 准备字段赋值
                // ========================================================
                // 加载 slot 4 的 castedInstance
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // 加载 slot 3 的 value
                mv.visitVarInsn(Opcodes.ALOAD, 3);

                // ========================================================
                // 步骤4.3: 基本类型拆箱（Unboxing）
                // ========================================================
                AsmUtil.unboxOrCast(mv, f.getType());

                // ========================================================
                // 步骤4.4: 写入字段值 (PUTFIELD)
                // ========================================================
                mv.visitFieldInsn(Opcodes.PUTFIELD,
                        owner, f.getName(), Type.getDescriptor(f.getType()));

                // ========================================================
                // 步骤4.5: 返回
                // ========================================================
                mv.visitInsn(Opcodes.RETURN);
            }

            // ============================================================
            // 步骤5: 处理 default 分支
            // ============================================================
            mv.visitLabel(defaultLabel);

            AsmUtil.throwIAEForField(mv);

            // ============================================================
            // 返回 Size.ZERO，由 ByteBuddy 自动计算
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
