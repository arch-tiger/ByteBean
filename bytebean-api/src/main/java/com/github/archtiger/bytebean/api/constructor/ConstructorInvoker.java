package com.github.archtiger.bytebean.api.constructor;

/**
 * 构造器访问
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public abstract class ConstructorInvoker {
    public abstract Object newInstance(int index, Object... args);

    public abstract Object newInstance();
}
