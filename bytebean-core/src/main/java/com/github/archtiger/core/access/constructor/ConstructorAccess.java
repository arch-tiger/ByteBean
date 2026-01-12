package com.github.archtiger.core.access.constructor;

/**
 * 构造器访问
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public interface ConstructorAccess {
    Object newInstance(int index, Object target, Object... args);
}
