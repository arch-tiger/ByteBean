package com.github.archtiger.core.factory.bean;

import com.github.archtiger.core.bytecode.bean.PrimitiveBeanGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.bean.CharBeanGetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * CharBeanGetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class CharBeanGetterFactory extends AbstractInvokerFactory<CharBeanGetter> {
    private final Method targetMethod;

    public CharBeanGetterFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<CharBeanGetter> defineInvokerClass() {
        return CharBeanGetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, CharBeanGetter.class);
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
