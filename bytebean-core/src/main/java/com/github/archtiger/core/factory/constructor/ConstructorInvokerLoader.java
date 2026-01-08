package com.github.archtiger.core.factory.constructor;

import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.definition.invoker.constructor.ConstructorInvoker;
import com.github.archtiger.core.bytecode.constructor.ConstructorInvokerAppender;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Constructor;

/**
 * ConstructorInvoker
 * 构造器调用器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class ConstructorInvokerLoader extends AbstractInvokerLoader<ConstructorInvoker> {
    private final Constructor<?> constructor;

    public ConstructorInvokerLoader(Class<?> targetClass, Constructor<?> constructor) {
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
        return InvokerConstant.CONSTRUCTOR_INVOKER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessConstructor(getTargetClass(), constructor);
    }
}
