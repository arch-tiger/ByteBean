package com.github.archtiger.bytebean.extensions;

/**
 * Bean 复制器
 *
 * @author ZIJIDELU
 * @datetime 2026/2/6 22:30
 */
public final class BeanCopier {
    private BeanCopier() {
        // 工具类不允许实例化。
    }

    /**
     * 复制Bean
     *
     * @param origin 来源Bean
     * @param target 目标Bean
     * @param <O>    来源Bean类型
     * @param <T>    目标Bean类型
     * @return 目标Bean
     */
    public static <O, T> T copy(O origin, T target) {
        // 按是否涉及 Record 路由到不同复制器。
        final Class<?> originClass = origin.getClass();
        final Class<?> targetClass = target.getClass();
        if (originClass.isRecord() || targetClass.isRecord()) {
            return RecordBeanCopier.copy(origin, target);
        }
        return ReflectiveBeanCopier.copy(origin, target);
    }

}
