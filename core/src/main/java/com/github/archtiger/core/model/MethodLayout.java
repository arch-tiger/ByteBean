package com.github.archtiger.core.model;

/**
 * 方法访问布局
 * 提供对类方法的索引访问和查询能力
 * 支持高性能随机访问方法
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6
 */
public interface MethodLayout {

    int methodCount();

    int indexOf(String name, Class<?>... parameterTypes);

    String methodName(int index);

    boolean containsIndex(int index);

    boolean isMethodAccessible(int index);

}
