package com.github.archtiger.core.factory.constructor;

import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.constructor.ConstructorInvoker;
import com.github.archtiger.core.bytecode.constructor.ConstructorInvokerAppender;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Constructor;

/**
 * 创建器工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class ConstructorInvokerFactory extends AbstractInvokerFactory<ConstructorInvoker> {
    private final Constructor<?> constructor;

    public ConstructorInvokerFactory(Class<?> targetClass, Constructor<?> constructor) {
        super(targetClass);
        this.constructor = constructor;
    }

    @Override
    protected Class<ConstructorInvoker> defineInvokerClass() {
        return ConstructorInvoker.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forConstructor(getTargetClass(), constructor, ConstructorInvoker.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new ConstructorInvokerAppender(getTargetClass(), constructor);
    }

    @Override
    protected String defineInvokerMethodName() {
        return ByteBeanConstant.CONSTRUCTOR_INVOKER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessConstructor(getTargetClass(), constructor);
    }
}
