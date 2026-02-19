package com.github.archtiger.bytebean.core.utils;

import com.github.archtiger.bytebean.core.constant.ByteBeanConstant;

/**
 * 名称工具类
 * <p>
 * 提供生成调用器类名称的工具方法。
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public class NameUtil {

    /**
     * 私有构造函数，防止实例化。
     */
    private NameUtil() {
    }

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
