package com.github.archtiger.bytebean.extensions;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.extensions.model.BeanCopierIdentifier;
import com.github.archtiger.bytebean.extensions.model.BeanCopyAction;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Bean 复制器
 *
 * @author ZIJIDELU
 * @datetime 2026/2/6 22:30
 */
public final class BeanCopier {
    private static final Map<BeanCopierIdentifier, BiConsumer<?, ?>> BEAN_COPIER_CACHE = new WeakKeyValueConcurrentMap<>();

    @SuppressWarnings("unchecked")
    public static <O, T> T copy(O origin, T target) {
        final BeanCopierIdentifier beanCopierIdentifier = new BeanCopierIdentifier(origin.getClass(), target.getClass());
        final BiConsumer<O, T> megaCopier = (BiConsumer<O, T>) BEAN_COPIER_CACHE.computeIfAbsent(beanCopierIdentifier, BeanCopier::doCreate);
        megaCopier.accept(origin, target);
        return target;
    }

    private static <O, T> BiConsumer<O, T> doCreate(BeanCopierIdentifier beanCopierIdentifier) {
        final Class<O> originClass = (Class<O>) beanCopierIdentifier.originClass();
        final Class<T> targetClass = (Class<T>) beanCopierIdentifier.targetClass();

        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(originClass);
        final MethodInvokerHelper targetMethodInvokerHelper = MethodInvokerHelper.of(targetClass);

        final Method[] originMethods = originClass.getMethods();
        final Method[] targetMethods = targetClass.getMethods();

        final Map<String, Method> originGetterMethodMap = calcBeanGetMethod(originMethods);
        final Map<String, Method> targetSetterMethodMap = calcBeanSetterMethod(targetMethods);

        final List<BeanCopyAction> beanCopyActions = new ArrayList<>();
        originGetterMethodMap.forEach((getterMethodName, getterMethod) -> {
            final int getterMethodIndex = originMethodInvokerHelper.getMethodIndex(getterMethod);
            if (getterMethodIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }

            if (isGetterWithGet(getterMethod)) {
                final String setterMethodName = "set" + getterMethodName.substring(3);
                final Method setterMethod = targetSetterMethodMap.get(setterMethodName);
                if (setterMethod != null) {
                    final int setterMethodIndex = targetMethodInvokerHelper.getMethodIndex(setterMethod);
                    if (setterMethodIndex == ExceptionCode.INVALID_INDEX) {
                        return;
                    }
                    beanCopyActions.add(new BeanCopyAction(getterMethodIndex, setterMethodIndex));
                }

            } else if (isGetterWithIs(getterMethod)) {
                final String setterMethodName = "set" + getterMethodName.substring(2);
                final Method setterMethod = targetSetterMethodMap.get(setterMethodName);
                if (setterMethod != null) {
                    final int setterMethodIndex = targetMethodInvokerHelper.getMethodIndex(setterMethod);
                    if (setterMethodIndex == ExceptionCode.INVALID_INDEX) {
                        return;
                    }
                    beanCopyActions.add(new BeanCopyAction(getterMethodIndex, setterMethodIndex));
                }

            }
        });

        final BeanCopyAction[] actionArray = beanCopyActions.toArray(new BeanCopyAction[0]);

        return (origin, target) -> {
            for (BeanCopyAction beanCopyAction : actionArray) {
                final Object getterValue = originMethodInvokerHelper.invoke(beanCopyAction.originGetterIndex(), origin);
                if (getterValue != null) {
                    targetMethodInvokerHelper.invoke1(beanCopyAction.targetSetterIndex(), target, getterValue);
                }
            }
        };
    }

    private static Map<String, Method> calcBeanGetMethod(Method[] methods) {
        if (methods == null || methods.length == 0) {
            return Collections.emptyMap();
        }

        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            // 排除 Object 类的方法
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }

            if (isGetterWithGet(method)) {
                methodMap.put(method.getName(), method);
            }

            if (isGetterWithIs(method)) {
                methodMap.put(method.getName(), method);
            }

        }

        return methodMap;
    }

    private static Map<String, Method> calcBeanSetterMethod(Method[] methods) {
        if (methods == null || methods.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }

            if (isSetter(method)) {
                methodMap.put(method.getName(), method);
            }
        }

        return methodMap;
    }

    private static boolean isGetterWithGet(Method method) {
        return method.getName().startsWith("get")
                && method.getName().length() > 3
                && method.getParameterTypes().length == 0;
    }

    private static boolean isGetterWithIs(Method method) {
        return method.getName().startsWith("is")
                && method.getName().length() > 2
                && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)
                && method.getParameterTypes().length == 0;
    }

    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getName().length() > 3
                && method.getParameterTypes().length == 1;
    }

}