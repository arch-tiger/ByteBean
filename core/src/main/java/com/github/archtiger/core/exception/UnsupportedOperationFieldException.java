package com.github.archtiger.core.exception;

/**
 * 表示某个操作在当前上下文下不被支持
 * 例如对 final 字段写入、static 字段实例访问等
 * 
 * @author ZIJIDELU
 * @datetime 2026/1/6
 */
public class UnsupportedOperationFieldException extends RuntimeException {

    /**
     * 构造一个默认异常
     */
    public UnsupportedOperationFieldException() {
        super("This operation is not supported");
    }

    /**
     * 构造异常并传入字段名
     * 
     * @param fieldName 字段名
     */
    public UnsupportedOperationFieldException(String fieldName) {
        super("Operation not supported for field: " + fieldName);
    }

    /**
     * 构造异常并传入字段名和原因
     * 
     * @param fieldName 字段名
     * @param cause 异常原因
     */
    public UnsupportedOperationFieldException(String fieldName, Throwable cause) {
        super("Operation not supported for field: " + fieldName, cause);
    }
}
