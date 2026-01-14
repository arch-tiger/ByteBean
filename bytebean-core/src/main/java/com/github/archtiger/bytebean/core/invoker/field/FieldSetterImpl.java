package com.github.archtiger.bytebean.core.invoker.field;

import com.github.archtiger.bytebean.core.support.AsmUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public final class FieldSetterImpl implements Implementation {
    private final Class<?> targetClass;
    private final List<Field> fields;

    public FieldSetterImpl(Class<?> targetClass, List<Field> fields) {
        this.targetClass = targetClass;
        this.fields = fields;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            // 获取目标类的内部名称
            String owner = Type.getInternalName(targetClass);
            // 获取生成类的内部名称
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

            // ============================================================
            // 步骤1: 将 instance 强制转换为目标类型
            // ============================================================
            // 方法签名: void set(int index, Object instance, Object value)
            // 局部变量表布局:
            //   slot 0: this
            //   slot 1: int index
            //   slot 2: Object instance
            //   slot 3: Object value
            //   slot 4: Target castedInstance（临时变量）

            // 加载 slot 2 的 Object instance
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            // 检查类型转换
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            // 存储到 slot 4
            mv.visitVarInsn(Opcodes.ASTORE, 4);

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

            // 生成 tableswitch，原理与 Getter 相同
            mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

            // ============================================================
            // 步骤4: 为每个字段生成对应的 case 分支
            // ============================================================
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                mv.visitLabel(labels[i]);

                // ========================================================
                // 步骤4.1: 插入 StackMapFrame
                // ========================================================
                mv.visitFrame(Opcodes.F_NEW, 5,
                        new Object[]{
                                // slot 0: this
                                generatedClassName,
                                // slot 1: int index
                                Opcodes.INTEGER,
                                // slot 2: Object instance
                                "java/lang/Object",
                                // slot 3: Object value
                                "java/lang/Object",
                                // slot 4: Target castedInstance
                                owner
                        },
                        0, new Object[0]);  // 操作数栈为空

                final boolean isFinalField = Modifier.isFinal(f.getModifiers());

                // reject: final field
                if (isFinalField) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // ========================================================
                // 步骤4.2: 准备字段赋值
                // ========================================================
                // 加载 slot 4 的 castedInstance（目标对象引用）
                mv.visitVarInsn(Opcodes.ALOAD, 4);

                // 加载 slot 3 的 value（要设置的值）
                mv.visitVarInsn(Opcodes.ALOAD, 3);

                // ========================================================
                // 步骤4.3: 基本类型拆箱（Unboxing）
                // ========================================================
                // 如果字段是基本类型，需要将 Object 类型的 value 拆箱为基本类型
                // 例如: Integer -> int, Long -> long
                //
                // 为什么需要拆箱:
                //   PUTFIELD 指令需要实际的字段类型值
                //   不能直接将 Object 引引用存储到 int/long 等基本类型字段中
                AsmUtil.unboxOrCast(mv, f.getType());

                // ========================================================
                // 步骤4.4: 写入字段值
                // ========================================================
                // PUTFIELD 指令: 设置对象的实例字段
                // 工作原理:
                //   1. 从操作数栈顶弹出字段值
                //   2. 从操作数栈顶弹出对象引用
                //   3. 根据字段名和描述符定位字段
                //   4. 将字段值写入对象的字段中
                //
                // 参数说明:
                //   owner: 对象的类内部名称
                //   f.getName(): 字段名称
                //   Type.getDescriptor(f.getType()): 字段类型描述符
                mv.visitFieldInsn(Opcodes.PUTFIELD,
                        owner, f.getName(), Type.getDescriptor(f.getType()));

                // ========================================================
                // 步骤4.5: 返回
                // ========================================================
                // RETURN 指令: 从 void 方法返回
                mv.visitInsn(Opcodes.RETURN);
            }

            // ============================================================
            // 步骤5: 处理 default 分支
            // ============================================================
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_NEW, 5,
                    new Object[]{
                            generatedClassName,
                            Opcodes.INTEGER,
                            "java/lang/Object",
                            "java/lang/Object",
                            owner
                    },
                    0, new Object[0]);

            AsmUtil.throwIAEForField(mv);

            // ============================================================
            // 步骤6: 计算并返回方法的栈和局部变量大小
            // ============================================================
            // maxStack = 5: 操作数栈的最大深度
            //   可能的栈状态: 对象引用 + value 引用（拆箱前）+ 拆箱后的值
            //
            // maxLocals = 5: 局部变量表的最大槽数量
            //   使用了 slot 0-4，所以需要 5 个槽
            return new ByteCodeAppender.Size(5, 5);
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
