package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.FieldGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.field.FieldGetter;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * Getter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class FieldGetterFactory extends AbstractInvokerFactory<FieldGetter> {
    private final Field targetField;

    public FieldGetterFactory(Class<?> targetClass,
                              Field targetField) {
        super(targetClass);
        this.targetField = targetField;
    }

    @Override
    protected Class<FieldGetter> defineInvokerClass() {
        return FieldGetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), targetField, FieldGetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new FieldGetterAppender(getTargetClass(), targetField);
    }

    @Override
    protected String defineInvokerMethodName() {
        return InvokerConstant.FIELD_GETTER_METHOD_NAME;
    }

    @Override
    protected boolean canInstantiate() {
        return InvokerRule.canAccessField(getTargetClass(), targetField);
    }

}
