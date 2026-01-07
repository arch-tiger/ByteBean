package com.github.archtiger.core.factory;

import com.github.archtiger.core.invoke.method.MethodInvoker;
import com.github.archtiger.core.bytecode.MethodInvokerAppender;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * 方法调用器工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 23:24
 */
public final class MethodInvokerFactory extends AbstractInvokerFactory<MethodInvoker> {
    private final Method method;
    public MethodInvokerFactory(Class<?> targetClass, Method method) {
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
        return ByteBeanConstant.METHOD_INVOKER_METHOD_NAME;
    }
}
