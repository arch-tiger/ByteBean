package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.PrimitiveFieldGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerFactory;
import com.github.archtiger.definition.invoker.field.LongFieldGetter;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * Long FieldGetter 工厂
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class LongFieldGetterFactory extends AbstractInvokerFactory<LongFieldGetter> {
    private final Field targetField;

    public LongFieldGetterFactory(Class<?> targetClass, Field targetField) {
        super(targetClass);
        this.targetField = targetField;
    }

    @Override
    protected Class<LongFieldGetter> defineInvokerClass() {
        return LongFieldGetter.class;
    }

    @Override
    protected InvokerNameInfo defineInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), targetField, LongFieldGetter.class);
    }

    @Override
    protected ByteCodeAppender defineByteCodeAppender() {
        return new PrimitiveFieldGetterAppender(getTargetClass(), targetField);
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
