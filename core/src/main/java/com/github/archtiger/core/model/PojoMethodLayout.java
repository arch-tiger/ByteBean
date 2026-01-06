package com.github.archtiger.core.model;

import java.lang.reflect.Method;

/**
 * POJO 方法布局
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 15:58
 */
public class PojoMethodLayout implements MethodLayout {
    @Override
    public int methodCount() {
        return 0;
    }

    @Override
    public int indexOf(String name, Class<?>... parameterTypes) {
        return 0;
    }

    @Override
    public String methodName(int index) {
        return "";
    }

    @Override
    public boolean containsIndex(int index) {
        return false;
    }

    @Override
    public boolean isMethodAccessible(int index) {
        return false;
    }
}
