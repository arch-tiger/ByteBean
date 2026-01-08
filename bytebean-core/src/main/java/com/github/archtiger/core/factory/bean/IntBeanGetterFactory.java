package com.github.archtiger.core.factory.bean;

import com.github.archtiger.core.bytecode.bean.PrimitiveBeanGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.bean.IntBeanGetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * IntBeanGetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class IntBeanGetterFactory extends AbstractInvokerFactory<IntBeanGetter> {
    private final Method targetMethod;

    public IntBeanGetterFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<IntBeanGetter> defineInvokerClass() {
        return IntBeanGetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, IntBeanGetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new PrimitiveBeanGetterAppender(getTargetClass(), targetMethod);
    }

    @Override
    protected String defineInvokerMethodName() {
        return ByteBeanConstant.BEAN_GETTER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessMethod(getTargetClass(), targetMethod);
    }
}
