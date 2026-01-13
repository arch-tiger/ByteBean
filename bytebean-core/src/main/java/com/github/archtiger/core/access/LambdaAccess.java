package com.github.archtiger.core.access;

import com.github.archtiger.definition.invoker.constructor.ConstructorInvoker;
import com.github.archtiger.definition.invoker.field.FieldGetter;
import com.github.archtiger.definition.invoker.field.FieldSetter;
import com.github.archtiger.definition.invoker.method.MethodInvoker;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/13 13:42
 */
public interface LambdaAccess {
    MethodInvoker[] getMethodInvokers();

    ConstructorInvoker[] getConstructorInvokers();

    FieldGetter[] getFieldGetters();

    FieldSetter[] getFieldSetters();
}
