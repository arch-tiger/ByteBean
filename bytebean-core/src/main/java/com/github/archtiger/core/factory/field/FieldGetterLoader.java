package com.github.archtiger.core.factory.field;

import com.github.archtiger.core.bytecode.field.FieldGetterAppender;
import com.github.archtiger.core.factory.AbstractInvokerLoader;
import com.github.archtiger.core.model.InvokerInfo;
import com.github.archtiger.definition.invoker.field.FieldGetter;
import com.github.archtiger.definition.model.InvokerConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
import com.github.archtiger.core.support.InvokerRule;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

import java.lang.reflect.Field;

/**
 * FieldGetter
 * 对象类型字段获取器加载器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 16:49
 */
public final class FieldGetterLoader extends AbstractInvokerLoader<FieldGetter> {
    private final Field targetField;

    public FieldGetterLoader(Class<?> targetClass,
                             Field targetField) {
        super(targetClass);
        this.targetField = targetField;
    }

    @Override
    protected Class<FieldGetter> getInvokerClass() {
        return FieldGetter.class;
    }

    @Override
    protected InvokerNameInfo getInvokerName() {
        return InvokerNameInfo.forField(getTargetClass(), targetField, FieldGetter.class);
    }

    @Override
    protected ByteCodeAppender getByteCodeAppender() {
        return new FieldGetterAppender(new InvokerInfo<>(getTargetClass(), FieldGetter.class, Object.class), targetField);
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
