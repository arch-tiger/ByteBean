package com.github.archtiger.definition.constructor;

/**
 * 构造器访问
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public interface ConstructorInvoker {
    Object newInstance(int index, Object... args);
}
