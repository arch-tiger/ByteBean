package com.github.archtiger.core.factory.bean;

import com.github.archtiger.core.bytecode.bean.BeanSetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.bean.BeanSetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * BeanSetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class BeanSetterFactory extends AbstractInvokerFactory<BeanSetter> {
    private final Method targetMethod;

    public BeanSetterFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<BeanSetter> defineInvokerClass() {
        return BeanSetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, BeanSetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new BeanSetterAppender(getTargetClass(), targetMethod);
    }

    @Override
    protected String defineInvokerMethodName() {
        return ByteBeanConstant.BEAN_SETTER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessMethod(getTargetClass(), targetMethod);
    }
}
