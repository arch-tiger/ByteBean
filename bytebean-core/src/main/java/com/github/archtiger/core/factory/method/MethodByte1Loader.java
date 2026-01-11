package com.github.archtiger.core.factory.method;

import com.github.archtiger.core.bytecode.method.MethodPrimitive1InvokerAppender;
import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.definition.invoker.method.MethodByte1;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * MethodByte1Loader
 * byte 类型一元方法调用器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/9
 */
public final class MethodByte1Loader extends AbstractInvokerLoader<MethodByte1> {
    private final Method targetMethod;

    public MethodByte1Loader(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<MethodByte1> getInvokerClass() {
        return MethodByte1.class;
    }

    @Override
    protected InvokerNameInfo getInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, MethodByte1.class);
    }

    @Override
    protected ByteCodeAppender getByteCodeAppender() {
        return new MethodPrimitive1InvokerAppender(new InvokerInfo(getTargetClass(), getInvokerClass(), byte.class), targetMethod);
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
