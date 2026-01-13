package com.github.archtiger.core.factory.constructor;

import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.definition.invoker.constructor.ConstructorInvoker;
import com.github.archtiger.core.bytecode.constructor.CtorInvokerAppender;
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
public final class CtorInvokerLoader extends AbstractInvokerLoader<ConstructorInvoker> {
    private final Constructor<?> constructor;

    public CtorInvokerLoader(Class<?> targetClass, Constructor<?> constructor) {
        super(targetClass);
        this.constructor = constructor;
    }

    @Override
    protected Class<ConstructorInvoker> getInvokerClass() {
        return ConstructorInvoker.class;
    }

    @Override
    protected InvokerNameInfo getInvokerName() {
        return InvokerNameInfo.forConstructor(getTargetClass(), constructor, ConstructorInvoker.class);
    }

    @Override
    protected ByteCodeAppender getByteCodeAppender() {
        return new CtorInvokerAppender(getTargetClass(), constructor);
    }

    @Override
    protected String getInvokerMethodName() {
        return InvokerConstant.CONSTRUCTOR_INVOKER_METHOD_NAME;
    }

    @Override
    public boolean canInstantiate() {
        return InvokerRule.canAccessConstructor(getTargetClass(), constructor);
    }
}
