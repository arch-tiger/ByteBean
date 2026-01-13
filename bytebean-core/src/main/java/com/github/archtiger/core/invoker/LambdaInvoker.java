package com.github.archtiger.core.invoker;

import com.github.archtiger.definition.constructor.ConstructorExecutor;
import com.github.archtiger.definition.field.FieldGetter;
import com.github.archtiger.definition.field.FieldSetter;
import com.github.archtiger.definition.method.MethodExecutor;

/**
 * 函数式调用器
 *
 * @author ZIJIDELU
 * @datetime 2026/1/13 13:42
 */
public interface LambdaInvoker {
    MethodExecutor[] getMethodInvokers();

    ConstructorExecutor[] getConstructorInvokers();

    FieldGetter[] getFieldGetters();

    FieldSetter[] getFieldSetters();
}
