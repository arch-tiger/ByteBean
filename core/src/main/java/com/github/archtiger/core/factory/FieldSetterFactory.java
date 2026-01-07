package com.github.archtiger.core.factory;

import com.github.archtiger.core.bytecode.FieldSetterAppender;
import com.github.archtiger.core.invoke.field.FieldSetter;
import com.github.archtiger.core.model.ByteBeanConstant;
import com.github.archtiger.core.model.InvokerNameInfo;
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
        return ByteBeanConstant.FIELD_SETTER_METHOD_NAME;
    }
}
