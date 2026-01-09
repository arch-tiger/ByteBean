package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.UnaryMethodInvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.definition.invoker.method.LongUnaryMethodInvoker;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * LongUnaryMethodInvoker
 * long 类型一元方法调用器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
public final class LongUnaryMethodInvokerLoader extends AbstractInvokerLoader<LongUnaryMethodInvoker> {
    private final Method targetMethod;

    public LongUnaryMethodInvokerLoader(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<LongUnaryMethodInvoker> defineInvokerClass() {
        return LongUnaryMethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, LongUnaryMethodInvoker.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new UnaryMethodInvokerAppender(getTargetClass(), targetMethod, long.class);
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
