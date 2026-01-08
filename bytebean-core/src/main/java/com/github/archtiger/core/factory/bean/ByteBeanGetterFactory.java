package com.github.archtiger.core.factory.bean;

import com.github.archtiger.core.bytecode.bean.PrimitiveBeanGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.bean.ByteBeanGetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * ByteBeanGetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class ByteBeanGetterFactory extends AbstractInvokerFactory<ByteBeanGetter> {
    private final Method targetMethod;

    public ByteBeanGetterFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<ByteBeanGetter> defineInvokerClass() {
        return ByteBeanGetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, ByteBeanGetter.class);
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
