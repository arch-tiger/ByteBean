package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.PrimitiveFieldGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.definition.invoker.field.BooleanFieldGetter;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * BooleanFieldGetter
 * Boolean 类型字段获取器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/8
 */
public final class BooleanFieldGetterLoader extends AbstractInvokerLoader<BooleanFieldGetter> {
    private final Field targetField;

    public BooleanFieldGetterLoader(Class<?> targetClass, Field targetField) {
        super(targetClass);
        this.targetField = targetField;
    }

    @Override
    protected Class<BooleanFieldGetter> getInvokerClass() {
        return BooleanFieldGetter.class;
    }

    @Override
    protected InvokerNameInfo getInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), targetField, BooleanFieldGetter.class);
    }

    @Override
    protected ByteCodeAppender getByteCodeAppender() {
        return new PrimitiveFieldGetterAppender(getTargetClass(), targetField);
    }

    @Override
    protected String getInvokerMethodName() {
        return InvokerConstant.FIELD_GETTER_METHOD_NAME;
    }

    @Override
    public boolean canInstantiate() {
        return InvokerRule.canAccessField(getTargetClass(), targetField);
    }
}
