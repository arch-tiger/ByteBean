package com.github.archtiger.core.support;

/**
 * 名称工具类
 *
 * @author ZIJIDELU
 * @datetime 2026/1/13 16:21
 */
public class NameUtil {
    /**
     * 计算调用器名称
     *
     * @param targetClass  目标类
     * @param invokerClass 调用器类
     * @return 调用器名称
     */
    public static String calcInvokerName(Class<?> targetClass, Class<?> invokerClass) {
        return targetClass.getName() + "$$" + ByteBeanConstant.INVOKER_NAME_PREFIX + "$" + invokerClass.getSimpleName();
    }
}
