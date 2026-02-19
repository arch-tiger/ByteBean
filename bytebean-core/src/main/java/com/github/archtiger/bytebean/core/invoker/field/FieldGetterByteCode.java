package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.utils.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 字段getter字节码实现，为FieldInvoker生成高性能字段读取字节码。
 * <p>
 * 该类使用ASM字节码操作技术，通过tableswitch指令实现字段索引到字段读取的快速分发。
 * 生成的字节码具有以下特点：
 * <ul>
 *   <li>在方法入口处一次性完成类型转换，避免在switch分支内重复检查</li>
 *   <li>使用tableswitch实现O(1)索引到字段的映射</li>
 *   <li>对基本类型字段执行自动装箱</li>
 *   <li>索引越界时抛出IllegalArgumentException</li>
 * </ul>
 * <p>
 * <b>性能优化：</b>
 * 相比反射调用，此字节码实现可提供3-5倍的性能提升。
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public final class FieldGetterByteCode implements Implementation {

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
    public FieldGetterByteCode(Class<?> targetClass, List<Field> fields) {
        this.targetClass = targetClass;
        this.fields = fields;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            // 获取目标类的内部名称（ASM 格式）
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 步骤1: 将参数中的 instance 强制转换为目标类型
            // 放在 switch 外部，避免在 case 内部重复进行类型检查
            // ============================================================
            // 方法签名: Object get(int index, Object instance)
            // 局部变量表:
            //   slot 0: this
            //   slot 1: int index
            //   slot 2: Object instance
            //   slot 3: Target castedInstance

            mv.visitVarInsn(Opcodes.ALOAD, 2);          // 加载 instance
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner); // 类型转换检查
            mv.visitVarInsn(Opcodes.ASTORE, 3);         // 存储转换后的对象

            // ============================================================
            // 步骤2: 加载索引参数
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            // 创建 switch 分支标签
            Label defaultLabel = new Label();
            Label[] labels = new Label[fields.size()];
            for (int i = 0; i < labels.length; i++) labels[i] = new Label();

            // ============================================================
            // 步骤3: 生成 tableswitch 指令
            // ============================================================
            mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

            // ============================================================
            // 步骤4: 生成各个 case 分支
            // ============================================================
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                mv.visitLabel(labels[i]);

                // 读取字段值 (直接使用 slot 3 中已转换好的对象)
                mv.visitVarInsn(Opcodes.ALOAD, 3);
                mv.visitFieldInsn(Opcodes.GETFIELD,
                        owner, f.getName(), Type.getDescriptor(f.getType()));

                // 基本类型装箱
                AsmUtil.boxIfNeeded(mv, f.getType());

                // 返回结果
                mv.visitInsn(Opcodes.ARETURN);
            }

            // ============================================================
            // 步骤5: 处理 default 分支（索引越界）
            // ============================================================
            mv.visitLabel(defaultLabel);

            AsmUtil.throwIAEForField(mv);

            // ============================================================
            // 返回 Size.ZERO，由 ByteBuddy 自动计算栈映射和局部变量表大小
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        // 预处理阶段，不修改类型信息，直接返回
        return instrumentedType;
    }
}