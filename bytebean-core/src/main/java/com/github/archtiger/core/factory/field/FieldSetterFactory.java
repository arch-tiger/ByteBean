package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.FieldSetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.field.FieldSetter;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * Setter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class FieldSetterFactory extends AbstractInvokerFactory<FieldSetter> {
    private final Field field;

    public FieldSetterFactory(Class<?> targetClass, Field field) {
        super(targetClass);
        this.field = field;
    }

    @Override
    protected Class<FieldSetter> defineInvokerClass() {
        return FieldSetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), field, FieldSetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new FieldSetterAppender(getTargetClass(), field);
    }

    @Override
    protected String defineInvokerMethodName() {
        return InvokerConstant.FIELD_SETTER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canWriteField(getTargetClass(), field);
    }
}
