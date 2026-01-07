package com.github.archtiger.core.exception;

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

    public UnsupportedCreateInvokerException(Class<?> invokerClass) {
        super("Unsupported create invoker: " + invokerClass.getName());
    }
}
