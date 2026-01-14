package com.github.archtiger.bytebean.api;

import com.github.archtiger.bytebean.api.constructor.ConstructorExecutor;
import com.github.archtiger.bytebean.api.field.FieldGetter;
import com.github.archtiger.bytebean.api.field.FieldSetter;
import com.github.archtiger.bytebean.api.method.MethodExecutor;

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
