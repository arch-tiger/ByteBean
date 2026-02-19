package com.github.archtiger.bytebean.core.invoker.constructor;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

/**
 * 无参构造器字节码实现，为ConstructorInvoker生成极致优化的无参构造器调用字节码。
 * <p>
 * 该类专门处理无参构造器的调用，生成最简洁高效的字节码序列。
 * <p>
 * 生成的字节码指令序列：
 * <ol>
 *   <li>NEW - 分配对象内存</li>
 *   <li>DUP - 复制引用（一份用于初始化，一份用于返回）</li>
 *   <li>INVOKESPECIAL - 调用&lt;init&gt;方法进行初始化</li>
 *   <li>ARETURN - 返回实例</li>
 * </ol>
 * <p>
 * <b>性能优化：</b>
 * 相比通用构造器调用，此实现避免了参数数组的创建、索引检查等开销，
 * 是最快的构造器调用方式。
 * <p>
 * <b>API对应：</b> {@code Object newInstance()}
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public final class ConstructorP0ByteCode implements Implementation {

    /**
     * 目标类，用于字节码生成。
     */
    private final Class<?> targetClass;

    /**
     * 构造函数。
     *
     * @param targetClass 目标类
     */
    public ConstructorP0ByteCode(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);

            // ============================================================
            // 步骤1: 在堆上创建新对象
            // 栈状态: [未初始化的对象引用]
            // ============================================================
            mv.visitTypeInsn(Opcodes.NEW, owner);

            // ============================================================
            // 步骤2: 复制栈顶引用
            // 栈状态: [未初始化的对象引用, 未初始化的对象引用]
            //
            // 为什么需要 DUP:
            //   下一步的 INVOKESPECIAL 指令会消费掉栈顶的一个引用用于初始化。
            //   如果不复制，栈就空了，最后就没有对象可以返回。
            //   DUP 之后，一个引用传给构造器，另一个引用留在栈顶用于 ARETURN。
            // ============================================================
            mv.visitInsn(Opcodes.DUP);

            // ============================================================
            // 步骤3: 调用无参构造函数 <init>
            // 栈状态: [初始化完成的对象引用]
            //
            // INVOKESPECIAL 会弹出栈顶引用，调用构造函数，并将初始化后的对象引用
            // 留在操作数栈中（虽然通常语义上是消费引用，但对象本身现在状态变了）。
            // 实际上对于实例初始化，栈顶引用在调用期间被消耗，完成后栈为空？
            // 不，标准 JVM 行为：INVOKESPECIAL instance 方法需要对象引用。
            // 但对于构造器调用，它修改的是那个引用指向的堆内存。
            // 关键点：DUP 确保了引用被复制。
            // 执行完后，栈上依然剩下 1 个引用（即第二个复制的引用）。
            // ============================================================
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, "<init>", "()V", false);

            // ============================================================
            // 步骤4: 返回对象
            // 栈状态: []
            // ============================================================
            mv.visitInsn(Opcodes.ARETURN);

            // ============================================================
            // 极致优化: 返回 Size.ZERO
            // MaxStack = 2 (NEW + DUP = 2 个引用)
            // MaxLocals = 1 (仅 this)
            // ByteBuddy 会自动计算并填入这些常量，无需手动维护。
            // ============================================================
            return ByteCodeAppender.Size.ZERO;
        };
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
