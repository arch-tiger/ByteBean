package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.PrimitiveFieldSetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.field.ByteFieldSetter;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * Byte FieldSetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class ByteFieldSetterFactory extends AbstractInvokerFactory<ByteFieldSetter> {
    private final Field targetField;

    public ByteFieldSetterFactory(Class<?> targetClass, Field targetField) {
        super(targetClass);
        this.targetField = targetField;
    }

    @Override
    protected Class<ByteFieldSetter> defineInvokerClass() {
        return ByteFieldSetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), targetField, ByteFieldSetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new PrimitiveFieldSetterAppender(getTargetClass(), targetField);
    }

    @Override
    protected String defineInvokerMethodName() {
        return InvokerConstant.FIELD_SETTER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessField(getTargetClass(), targetField);
    }
}
