package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.support.AsmUtil;
import com.github.archtiger.bytebean.core.support.ByteCodeSizeUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public final class PrimitiveFieldSetterByteCode implements Implementation {

    private final Class<?> targetClass;
    private final List<Field> fields;
    private final Class<?> primitiveType;

    public PrimitiveFieldSetterByteCode(Class<?> targetClass, List<Field> fields, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.fields = fields;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 步骤1: 计算局部变量表位置并转换 instance
            // ============================================================
            // 方法签名: void set(int index, Object instance, <primitive> value)
            // 局部变量表布局:
            //   slot 0: this
            //   slot 1: int index
            //   slot 2: Object instance
            //   slot 3: <primitive> value (注意: long/double 占用 slot 3 和 4)

            // 计算基本类型 value 占用的 slot 数量 (1 或 2)
            int valueSlotSize = ByteCodeSizeUtil.slotSize(primitiveType);
            // 计算存放 castedInstance 的 slot 索引 (跳过 value 占用的 slot)
            int castedInstanceSlot = 3 + valueSlotSize;

            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, castedInstanceSlot);

            // ============================================================
            // 步骤2: 加载索引并生成 switch
            // ============================================================
            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[fields.size()];
            for (int i = 0; i < labels.length; i++) labels[i] = new Label();

            mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

            // ============================================================
            // 步骤3: 生成 case 分支
            // ============================================================
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                mv.visitLabel(labels[i]);

                final boolean notPrimitive = f.getType() != primitiveType;
                final boolean isFinalField = Modifier.isFinal(f.getModifiers());

                // 拒绝处理: 类型不匹配 或 final 字段
                if (notPrimitive || isFinalField) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 加载目标对象引用
                mv.visitVarInsn(Opcodes.ALOAD, castedInstanceSlot);

                // 加载基本类型的 value 参数 (从 slot 3 开始)
                mv.visitVarInsn(AsmUtil.getLoadOpcode(primitiveType), 3);

                // 设置字段值 (PUTFIELD)
                String desc = Type.getDescriptor(f.getType());
                mv.visitFieldInsn(Opcodes.PUTFIELD, owner, f.getName(), desc);

                // 返回
                mv.visitInsn(Opcodes.RETURN);
            }

            // ============================================================
            // 步骤4: 处理 default 分支
            // ============================================================
            mv.visitLabel(defaultLabel);

            // 抛出 IllegalArgumentException 异常
            AsmUtil.throwIAEForField(mv);

            // ============================================================
            // 返回 Size.ZERO
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
