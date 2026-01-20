package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.support.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.util.List;

public final class PrimitiveFieldGetterImpl implements Implementation {

    private final Class<?> targetClass;
    private final List<Field> fields;
    private final Class<?> primitiveType;

    public PrimitiveFieldGetterImpl(Class<?> targetClass, List<Field> fields, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.fields = fields;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 步骤1: 将 instance 强制转换为目标类型
            // ============================================================
            // slot: 0=this, 1=index, 2=instance, 3=castedInstance
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 3);

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

                // 类型校验：只处理指定基本类型的字段，其他跳转到 default
                if (f.getType() != primitiveType) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 读取字段值
                mv.visitVarInsn(Opcodes.ALOAD, 3);
                String desc = Type.getDescriptor(f.getType());
                mv.visitFieldInsn(Opcodes.GETFIELD, owner, f.getName(), desc);

                // 根据基本类型选择对应的 RETURN 指令 (IRETURN, LRETURN, FRETURN, DRETURN)
                mv.visitInsn(AsmUtil.getReturnOpcode(primitiveType));
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
