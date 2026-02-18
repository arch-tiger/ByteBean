package com.github.archtiger.bytebean.core.utils;

/**
 * 栈大小工具类
 *
 * @author archtiger
 * @datetime 2026/1/6 11:12
 */
public final class ByteCodeSizeUtil {
    /**
     * 返回字段类型在栈上占用的 slot 数量
     *
     * @param type 字段类型
     * @return 字段类型在栈上占用的 slot 数量
     */
    public static int slotSize(Class<?> type) {
        if (type == long.class || type == double.class) {
            return 2;
        }
        return 1;
    }
}