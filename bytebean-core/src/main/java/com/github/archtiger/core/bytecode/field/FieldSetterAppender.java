package com.github.archtiger.core.bytecode.field;

import com.github.archtiger.core.bytecode.AbstractInvokerAppender;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import com.github.archtiger.definition.invoker.field.FieldSetter;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;

/**
 * 设置器字节码追加器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
public final class FieldSetterAppender extends AbstractInvokerAppender<FieldSetter> {
    private final Field field;

    public FieldSetterAppender(InvokerInfo<FieldSetter> invokerInfo, Field field) {
        super(invokerInfo);
        this.field = field;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 将 target 参数加载到栈上
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(getInvokerInfo().targetClass()));

        // 将 value 参数加载到栈上
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);

        // 如果是 primitive 字段，拆箱
        AsmUtil.unboxOrCast(methodVisitor, field.getType());

        // 执行 PUTFIELD 指令
        methodVisitor.visitFieldInsn(Opcodes.PUTFIELD,
                Type.getInternalName(getInvokerInfo().targetClass()),
                field.getName(),
                Type.getDescriptor(field.getType())
        );

        methodVisitor.visitInsn(Opcodes.RETURN);

        return calcSize();
    }

    @Override
    protected Size calcSize() {

        // 1. 计算 maxStack
        // 流程：
        // ALOAD 1         -> 栈深 1
        // CHECKCAST       -> 栈深 1
        // ALOAD 2         -> 栈深 2
        // unboxOrCast     -> 如果是 long/double，由 1 引用 变为 2 long，栈深变为 3。其余保持 2。
        // PUTFIELD        -> 栈清空
        int fieldSlotSize = ByteCodeSizeUtil.slotSize(field.getType()) + 1;

        // 2. 计算 maxLocals
        // 代码显式访问了索引 1 (target) 和索引 2 (value)
        int maxLocals = 3;
        return new Size(fieldSlotSize, maxLocals);
    }
}
