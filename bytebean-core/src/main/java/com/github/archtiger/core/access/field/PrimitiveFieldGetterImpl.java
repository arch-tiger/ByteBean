package com.github.archtiger.core.access.field;

import com.github.archtiger.core.support.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.util.List;

public final class PrimitiveFieldGetterImpl implements Implementation {

    private final Class<?> targetClass;
    private final List<Field> fields;

    public PrimitiveFieldGetterImpl(Class<?> targetClass, List<Field> fields) {
        this.targetClass = targetClass;
        this.fields = fields;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

            // slot: 0=this, 1=index, 2=instance, 3=castedInstance
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, 3);

            mv.visitVarInsn(Opcodes.ILOAD, 1);

            // default label
            Label defaultLabel = new Label();
            Label[] labels = new Label[fields.size()];
            for (int i = 0; i < labels.length; i++) labels[i] = new Label();

            mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

            // case 0..N
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                mv.visitLabel(labels[i]);
                mv.visitFrame(Opcodes.F_NEW, 4, new Object[]{
                        generatedClassName,
                        Opcodes.INTEGER,
                        "java/lang/Object",
                        owner
                }, 0, new Object[0]);

                // 只处理 int 类型的字段，其他类型跳转到 default 分支
                if (f.getType() != int.class) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                mv.visitVarInsn(Opcodes.ALOAD, 3);

                String desc = Type.getDescriptor(f.getType());
                mv.visitFieldInsn(Opcodes.GETFIELD, owner, f.getName(), desc);

                // int 类型使用 IRETURN
                mv.visitInsn(Opcodes.IRETURN);
            }

            // default
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_NEW, 4, new Object[]{
                    generatedClassName,
                    Opcodes.INTEGER,
                    "java/lang/Object",
                    owner
            }, 0, new Object[0]);

            // 抛出 IllegalArgumentException 异常
            AsmUtil.throwIAE(mv);

            return new ByteCodeAppender.Size(4, 4); // maxStack=4, maxLocals=4
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
