package com.github.archtiger.bytebean.core.model;

import java.lang.reflect.Method;

/**
 * 方法唯一标识
 *
 * @author ZIJIDELU
 * @datetime 2026/1/19 18:58
 */
public record MethodIdentify(
        Method method,
        int index
) {

}
