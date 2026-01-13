package com.github.archtiger.core.invoker.field;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public final class PrimitiveFieldSetterImpl implements Implementation {

    private final Class<?> targetClass;
    private final List<Field> fields;
    private final Class<?> primitiveType;

    public PrimitiveFieldSetterImpl(Class<?> targetClass, List<Field> fields, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.fields = fields;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

            // 计算 value 参数的 slot 大小和 castedInstance 的 slot 位置
            int valueSlotSize = ByteCodeSizeUtil.slotSize(primitiveType);
            int castedInstanceSlot = 3 + valueSlotSize; // value 占 slot 3 (或 3-4)，castedInstance 在 value 之后
            int maxLocals = castedInstanceSlot + 1; // castedInstance 占 1 个 slot

            // slot: 0=this, 1=index, 2=instance, 3=value(primitive, 可能占2个slot), castedInstanceSlot=castedInstance
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, castedInstanceSlot);

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
                // 获取基本类型的 Frame 描述符
                // Frame locals 参数是描述符数组的长度（long/double 在 Frame 中只占一个元素）
                Object primitiveFrameType = getPrimitiveFrameType(primitiveType);
                mv.visitFrame(Opcodes.F_NEW, 5, new Object[]{
                        generatedClassName,
                        Opcodes.INTEGER,
                        "java/lang/Object",
                        primitiveFrameType,
                        owner
                }, 0, new Object[0]);

                final boolean notPrimitive = f.getType() != primitiveType;
                final boolean isFinalField = Modifier.isFinal(f.getModifiers());

                // reject: non-primitive or final field
                if (notPrimitive || isFinalField) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 加载目标对象引用
                mv.visitVarInsn(Opcodes.ALOAD, castedInstanceSlot);

                // 加载基本类型的 value 参数
                mv.visitVarInsn(AsmUtil.getLoadOpcode(primitiveType), 3);

                // 设置字段值
                String desc = Type.getDescriptor(f.getType());
                mv.visitFieldInsn(Opcodes.PUTFIELD, owner, f.getName(), desc);

                // void 方法使用 RETURN
                mv.visitInsn(Opcodes.RETURN);
            }

            // default
            mv.visitLabel(defaultLabel);
            Object primitiveFrameType = getPrimitiveFrameType(primitiveType);
            // Frame locals 参数是描述符数组的长度（long/double 在 Frame 中只占一个元素）
            mv.visitFrame(Opcodes.F_NEW, 5, new Object[]{
                    generatedClassName,
                    Opcodes.INTEGER,
                    "java/lang/Object",
                    primitiveFrameType,
                    owner
            }, 0, new Object[0]);

            // 抛出 IllegalArgumentException 异常
            AsmUtil.throwIAEForField(mv);

            // maxStack = 4 (PUTFIELD路径需要2, throwIAE需要4)
            // maxLocals 根据基本类型计算: 对于 long/double 为 6，其他为 5
            return new ByteCodeAppender.Size(4, maxLocals);
        };
    }

    /**
     * 获取基本类型的 Frame 描述符
     */
    private static Object getPrimitiveFrameType(Class<?> type) {
        if (type == long.class) {
            return Opcodes.LONG;
        } else if (type == float.class) {
            return Opcodes.FLOAT;
        } else if (type == double.class) {
            return Opcodes.DOUBLE;
        } else {
            // byte, short, int, char, boolean 都使用 INTEGER
            return Opcodes.INTEGER;
        }
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
