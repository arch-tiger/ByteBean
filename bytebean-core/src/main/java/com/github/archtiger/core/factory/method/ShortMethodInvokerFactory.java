package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.PrimitiveMethodInvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.method.ShortMethodInvoker;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * Short MethodInvoker 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class ShortMethodInvokerFactory extends AbstractInvokerFactory<ShortMethodInvoker> {
    private final Method targetMethod;

    public ShortMethodInvokerFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<ShortMethodInvoker> defineInvokerClass() {
        return ShortMethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, ShortMethodInvoker.class);
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
