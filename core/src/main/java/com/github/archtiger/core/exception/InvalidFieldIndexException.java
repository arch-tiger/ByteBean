package com.github.archtiger.core.exception;

/**
 * 当访问字段索引无效时抛出此异常。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 00:01
 */
public class InvalidFieldIndexException extends RuntimeException {

    private final int invalidIndex;

    public InvalidFieldIndexException(int invalidIndex) {
        super("Invalid field index: " + invalidIndex);
        this.invalidIndex = invalidIndex;
    }

    public InvalidFieldIndexException(int invalidIndex, String message) {
        super(message);
        this.invalidIndex = invalidIndex;
    }

    public int getInvalidIndex() {
        return invalidIndex;
    }
}
