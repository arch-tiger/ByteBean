package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.PrimitiveMethodInvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.method.FloatMethodInvoker;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * Float MethodInvoker 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class FloatMethodInvokerFactory extends AbstractInvokerFactory<FloatMethodInvoker> {
    private final Method targetMethod;

    public FloatMethodInvokerFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<FloatMethodInvoker> defineInvokerClass() {
        return FloatMethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, FloatMethodInvoker.class);
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
