package com.github.archtiger.bytebean.core.model;

import java.lang.reflect.Method;

/**
 * 方法唯一标识
 *
 * @param method 方法对象
 * @param index 方法索引
 * @author ZIJIDELU
 * @since 1.0.0
 */
public record MethodIdentify(
        Method method,
        int index
) {

}
