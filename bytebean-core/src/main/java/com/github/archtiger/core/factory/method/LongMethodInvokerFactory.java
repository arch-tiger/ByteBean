package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.PrimitiveMethodInvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.method.LongMethodInvoker;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * Long MethodInvoker 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class LongMethodInvokerFactory extends AbstractInvokerFactory<LongMethodInvoker> {
    private final Method targetMethod;

    public LongMethodInvokerFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<LongMethodInvoker> defineInvokerClass() {
        return LongMethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, LongMethodInvoker.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new PrimitiveMethodInvokerAppender(getTargetClass(), targetMethod);
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
