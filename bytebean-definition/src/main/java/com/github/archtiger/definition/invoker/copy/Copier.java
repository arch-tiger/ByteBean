package com.github.archtiger.definition.invoker.copy;

@FunctionalInterface
public interface Copier {
    /**
     * 将 source 的数据复制到 target
     *
     * 可以是字段、getter/setter 或任意方法组合
     *
     * @param source 来源对象
     * @param target 目标对象
     */
    void copy(Object source, Object target);
}
