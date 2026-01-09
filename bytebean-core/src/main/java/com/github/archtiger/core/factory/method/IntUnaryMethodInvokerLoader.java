package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.UnaryMethodInvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.definition.invoker.method.IntUnaryMethodInvoker;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * IntUnaryMethodInvoker
 * int 类型一元方法调用器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
public final class IntUnaryMethodInvokerLoader extends AbstractInvokerLoader<IntUnaryMethodInvoker> {
    private final Method targetMethod;

    public IntUnaryMethodInvokerLoader(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<IntUnaryMethodInvoker> defineInvokerClass() {
        return IntUnaryMethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, IntUnaryMethodInvoker.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new UnaryMethodInvokerAppender(getTargetClass(), targetMethod, int.class);
    }

    @Override
    protected String defineInvokerMethodName() {
        return InvokerConstant.METHOD_INVOKER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessMethod(getTargetClass(), targetMethod);
    }
}
