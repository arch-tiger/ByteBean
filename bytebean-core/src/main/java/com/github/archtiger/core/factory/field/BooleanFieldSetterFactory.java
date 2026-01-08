package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.PrimitiveFieldSetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.core.invoke.field.BooleanFieldSetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * Boolean FieldSetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class BooleanFieldSetterFactory extends AbstractInvokerFactory<BooleanFieldSetter> {
    private final Field targetField;

    public BooleanFieldSetterFactory(Class<?> targetClass, Field targetField) {
        super(targetClass);
        this.targetField = targetField;
    }

    @Override
    protected Class<BooleanFieldSetter> defineInvokerClass() {
        return BooleanFieldSetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), targetField, BooleanFieldSetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new PrimitiveFieldSetterAppender(getTargetClass(), targetField);
    }

    @Override
    protected String defineInvokerMethodName() {
        return ByteBeanConstant.FIELD_SETTER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessField(getTargetClass(), targetField);
    }
}
