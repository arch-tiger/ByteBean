package com.github.archtiger.core.access;

import com.github.archtiger.core.access.method.MethodAccessGenerator;
import com.github.archtiger.core.model.MethodAccessInfo;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 极致性能方法匹配助手
 * <p>
 * 核心策略：
 * 1. 开放寻址法：使用 int 数组作为哈希表，无对象开销，L1 缓存友好。
 * 2. 负载因子 0.25：极大降低哈希冲突概率，保证 99.9% 的调用是 O(1)。
 * 3. 零 GC：运行时查找不产生任何临时对象。
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:23
 */
public class MethodAccessHelper {
    static class A {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Method getName = A.class.getMethod("setName", String.class);
        int hash = getName.hashCode();
        for (Class<?> paramType : getName.getParameterTypes()) {
            hash = 31 * hash + paramType.getName().hashCode();
        }

        System.out.println(hash);
        int idx = (hash * 0x9E3779B9) >>> 1 & 2;
        System.out.println(idx);
    }
}
