package com.github.archtiger.core.factory.bean;

import com.github.archtiger.core.bytecode.bean.PrimitiveBeanSetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.bean.ShortBeanSetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Method;

/**
 * ShortBeanSetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class ShortBeanSetterFactory extends AbstractInvokerFactory<ShortBeanSetter> {
    private final Method targetMethod;

    public ShortBeanSetterFactory(Class<?> targetClass, Method targetMethod) {
        super(targetClass);
        this.targetMethod = targetMethod;
    }

    @Override
    protected Class<ShortBeanSetter> defineInvokerClass() {
        return ShortBeanSetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forMethod(getTargetClass(), targetMethod, ShortBeanSetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new PrimitiveBeanSetterAppender(getTargetClass(), targetMethod);
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
