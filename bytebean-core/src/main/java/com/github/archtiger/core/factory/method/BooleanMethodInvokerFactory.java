package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.PrimitiveMethodInvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.method.BooleanMethodInvoker;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * Boolean MethodInvoker 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class BooleanMethodInvokerFactory extends AbstractInvokerFactory<BooleanMethodInvoker> {
    private final Method targetMethod;

    public BooleanMethodInvokerFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<BooleanMethodInvoker> defineInvokerClass() {
        return BooleanMethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, BooleanMethodInvoker.class);
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
