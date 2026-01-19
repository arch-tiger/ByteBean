package com.github.archtiger.bytebean.core.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/19 15:07
 */
public record MethodGroup(
        List<Method> methodList,
        List<Method> method0List,
        List<Method> method1List,
        List<Method> intMethodList,
        List<Method> longMethodList,
        List<Method> doubleMethodList,
        List<Method> floatMethodList,
        List<Method> booleanMethodList,
        List<Method> charMethodList,
        List<Method> byteMethodList,
        List<Method> shortMethodList,
        List<Method> methodInt1List,
        List<Method> methodLong1List,
        List<Method> methodDouble1List,
        List<Method> methodFloat1List,
        List<Method> methodBoolean1List,
        List<Method> methodChar1List,
        List<Method> methodByte1List,
        List<Method> methodShort1List
) {
}
