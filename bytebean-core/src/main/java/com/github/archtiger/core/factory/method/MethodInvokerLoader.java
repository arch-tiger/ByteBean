package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.definition.invoker.method.MethodInvoker;
import com.github.archtiger.core.bytecode.method.MethodInvokerAppender;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * MethodInvoker
 * 方法调用器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 23:24
 */
public final class MethodInvokerLoader extends AbstractInvokerLoader<MethodInvoker> {
    private final Method method;
    public MethodInvokerLoader(Class<?> targetClass, Method method) {
        super(targetClass);
        this.method = method;
    }

    @Override
    protected Class<MethodInvoker> defineInvokerClass() {
        return MethodInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), method, MethodInvoker.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new MethodInvokerAppender(getTargetClass(), method);
    }

    @Override
    protected String defineInvokerMethodName() {
        return InvokerConstant.METHOD_INVOKER_METHOD_NAME;
    }

    @Override
    public boolean canInstantiate() {
        return InvokerRule.canAccessMethod(getTargetClass(), method);
    }
}
