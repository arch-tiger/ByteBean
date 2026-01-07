package com.github.archtiger.core.exception;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 当尝试创建不支持的调用器时抛出此异常。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7 22:58
 */
public class UnsupportedCreateInvokerException extends RuntimeException {

    public UnsupportedCreateInvokerException(String message) {
        super(message);
    }

    /**
     * 构造器：带目标类和调用器类
     *
     * @param targetClass  目标类
     * @param invokerClass 调用器类
     */
    public UnsupportedCreateInvokerException(Class<?> targetClass, Class<?> invokerClass) {
        super("Cannot create invoker '" + invokerClass.getName() +
                "' for target class '" + targetClass.getName() +
                "'. Field/method/constructor may be private or not declared in the target class.");
    }

    public UnsupportedCreateInvokerException(Field field, Class<?> targetClass, Class<?> invokerType, String reason) {
        super(String.format(
                "Cannot create invoker [%s] for field [%s] in class [%s]: %s",
                invokerType.getSimpleName(),
                field.getName(),
                targetClass.getName(),
                reason
        ));
    }

    public UnsupportedCreateInvokerException(Method method, Class<?> targetClass, Class<?> invokerType, String reason) {
        super(String.format(
                "Cannot create invoker [%s] for method [%s%s] in class [%s]: %s",
                invokerType.getSimpleName(),
                method.getName(),
                methodSignature(method),
                targetClass.getName(),
                reason
        ));
    }

    public UnsupportedCreateInvokerException(Constructor<?> constructor, Class<?> targetClass, Class<?> invokerType, String reason) {
        super(String.format(
                "Cannot create invoker [%s] for constructor [%s] in class [%s]: %s",
                invokerType.getSimpleName(),
                constructorSignature(constructor),
                targetClass.getName(),
                reason
        ));
    }

    private static String methodSignature(Method method) {
        return typesSignature(method.getParameterTypes());
    }

    private static String constructorSignature(Constructor<?> constructor) {
        return typesSignature(constructor.getParameterTypes());
    }

    private static String typesSignature(Class<?>[] types) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < types.length; i++) {
            sb.append(types[i].getSimpleName());
            if (i < types.length - 1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

}
