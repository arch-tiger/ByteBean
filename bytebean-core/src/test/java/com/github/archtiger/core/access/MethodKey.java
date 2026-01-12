package com.github.archtiger.core.access;

import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * MethodKey：方法签名唯一标识（方法名 + 参数类型）
 */
public class MethodKey {
    private final String name;
    private final Class<?>[] params;
    private final int hash; // 预计算 hash

    public MethodKey(String name, Class<?>[] params) {
        this.name = name;
        this.params = params != null ? params.clone() : new Class<?>[0]; // 防止外部数组被修改
        this.hash = computeHash();
    }

    private int computeHash() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MethodKey)) return false;
        MethodKey other = (MethodKey) obj;
        return name.equals(other.name) && Arrays.equals(params, other.params);
    }

    @Override
    public String toString() {
        return name + Arrays.toString(params);
    }

    // ===============================
    // 示例用法：构建 methodKey -> index 映射
    // ===============================
    public static void main(String[] args) throws NoSuchMethodException {
        // 假设有一个类
        class Foo {
            public void foo() {}
            public void foo(String s) {}
            public void bar(int x, String y) {}
        }

        // 获取方法列表
        java.lang.reflect.Method[] methods = Foo.class.getDeclaredMethods();

        // Map<MethodKey, index>
        Map<MethodKey, Integer> methodKeyToIndex = new HashMap<>();

        for (int i = 0; i < methods.length; i++) {
            java.lang.reflect.Method m = methods[i];
            MethodKey key = new MethodKey(m.getName(), m.getParameterTypes());
            methodKeyToIndex.put(key, i);
        }

        // 测试
        MethodKey test1 = new MethodKey("foo", new Class<?>[]{});
        MethodKey test2 = new MethodKey("foo", new Class<?>[]{String.class});
        MethodKey test3 = new MethodKey("bar", new Class<?>[]{int.class, String.class});

        System.out.println("Index of foo(): " + methodKeyToIndex.get(test1));
        System.out.println("Index of foo(String): " + methodKeyToIndex.get(test2));
        System.out.println("Index of bar(int,String): " + methodKeyToIndex.get(test3));
    }
}
