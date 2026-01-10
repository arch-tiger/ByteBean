package com.github.archtiger.core.bytecode;

import com.github.archtiger.core.support.AsmUtil;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * 字节码追加器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9 20:24
 */
public abstract class AbstractByteCodeAppender implements ByteCodeAppender {


    /**
     * 计算字节码大小
     *
     * @return 字节码大小
     */
    protected abstract Size calcSize();

    /**
     * 追加返回值指令
     *
     * @param mv         方法访问器
     * @param returnType 返回值类型
     */
    public static void emitReturn(MethodVisitor mv, Class<?> returnType) {
        mv.visitInsn(AsmUtil.getReturnOpcode(returnType));
    }

}
