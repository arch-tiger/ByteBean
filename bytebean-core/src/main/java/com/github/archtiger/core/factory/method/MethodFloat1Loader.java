package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.MethodPrimitive1InvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.definition.invoker.method.MethodFloat1;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * MethodFloat1Loader
 * float 类型一元方法调用器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
public final class MethodFloat1Loader extends AbstractInvokerLoader<MethodFloat1> {
    private final Method targetMethod;

    public MethodFloat1Loader(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<MethodFloat1> getInvokerClass() {
        return MethodFloat1.class;
    }

    @Override
    protected InvokerNameInfo getInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, MethodFloat1.class);
    }

    @Override
    protected ByteCodeAppender getByteCodeAppender() {
        return new MethodPrimitive1InvokerAppender(new InvokerInfo(getTargetClass(), getInvokerClass(), float.class), targetMethod);
    }

    @Override
    protected String getInvokerMethodName() {
        return InvokerConstant.METHOD_INVOKER_METHOD_NAME;
    }

    @Override
    public boolean canInstantiate() {
        return InvokerRule.canAccessMethod(getTargetClass(), targetMethod);
    }
}
