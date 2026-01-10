package com.github.archtiger.core.bytecode;

import com.github.archtiger.core.model.InvokerInfo;
import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * 抽象方法调用器字节码追加器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9 20:24
 */
public abstract class AbstractInvokerAppender extends AbstractByteCodeAppender {
    private final InvokerInfo invokerInfo;

    public AbstractInvokerAppender(InvokerInfo invokerInfo) {
        this.invokerInfo = invokerInfo;
    }

    /**
     * 追加返回值指令
     * <p>
     * 根据 invoker 声明类型和实际方法返回类型，计算最终返回指令
     * <p>
     * 用于支持：
     * - invoker 声明 Object，方法返回 primitive → 需要装箱
     * - invoker 声明 primitive，方法返回 Object → 需要拆箱
     * - 类型一致 → 直接返回对应 opcode
     *
     * @param mv 方法访问器
     */
    protected void emitReturn(MethodVisitor mv) {
        AbstractByteCodeAppender.emitReturn(mv, invokerInfo.invokerReturnType());
    }
}
