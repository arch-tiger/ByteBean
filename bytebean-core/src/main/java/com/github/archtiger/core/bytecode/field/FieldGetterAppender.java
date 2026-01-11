package com.github.archtiger.core.bytecode.field;

import com.github.archtiger.core.bytecode.AbstractInvokerAppender;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import com.github.archtiger.definition.invoker.field.FieldGetter;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;

/**
 * 获取器字节码追加器
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
public final class FieldGetterAppender extends AbstractInvokerAppender<FieldGetter> {
    private final InvokerInfo<FieldGetter> invokerInfo;
    private final Field field;

    public FieldGetterAppender(InvokerInfo<FieldGetter> invokerInfo, Field field) {
        super(invokerInfo);
        this.invokerInfo = invokerInfo;
        this.field = field;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context context, MethodDescription methodDescription) {
        // 将 target 参数加载到栈上
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(invokerInfo.targetClass()));
        // 访问字段
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD,
                Type.getInternalName(invokerInfo.targetClass()),
                field.getName(),
                Type.getDescriptor(field.getType())
        );
        // 装箱 primitive
        AsmUtil.boxIfNeeded(methodVisitor, field.getType());
        // 返回
        methodVisitor.visitInsn(Opcodes.ARETURN);
        return calcSize();
    }

    @Override
    protected Size calcSize() {
        // 计算 maxStack
        // 栈变化分析：
        // ALOAD 1      -> 栈深 +1 (当前: 1)
        // CHECKCAST    -> 栈深不变 (当前: 1)
        // GETFIELD     -> 弹出引用(-1)，压入值(+size)
        //               若为 long/double (size=2): 栈深变为 1 - 1 + 2 = 2 (峰值)
        //               若为其他 (size=1): 栈深变为 1 - 1 + 1 = 1
        // boxIfNeeded   -> 消耗原始值，压入引用
        //               long/double: 消耗2压1，栈深 2 -> 1
        //               其他: 消耗1压1，栈深 1 -> 1
        // 结论：long/double 时栈峰值最大为 2，其余为 1
        final int maxStack = ByteCodeSizeUtil.slotSize(field.getType());

        // 计算 maxLocals
        // 代码中显式使用了索引 1 (ALOAD 1)
        // 因此局部变量表必须至少能容纳索引 0 和 1
        final int maxLocals = 2;
        return new Size(maxStack, maxLocals);
    }
}
