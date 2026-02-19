package com.github.archtiger.bytebean.core.invoker.constructor;

import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 基于MethodHandle的构造器调用器，为大量构造器场景提供高性能调用能力。
 * <p>
 * 当类的构造器数量超过阈值（默认20）时，使用MethodHandle实现而非字节码生成。
 * <p>
 * <b>特点：</b>
 * <ul>
 *   <li>使用MethodHandle的asSpreader方法支持可变参数调用</li>
 *   <li>缓存无参构造器，提供快速访问</li>
 *   <li>相比反射调用，性能提升约2-3倍</li>
 * </ul>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public final class ConstructorHandleInvoker extends ConstructorInvoker {

    /**
     * MethodHandles.Lookup实例，用于创建MethodHandle。
     */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * 构造器MethodHandle数组，按构造器索引排列。
     * 无参构造器的Handle会被转换为返回Object类型。
     * 有参构造器的Handle使用asSpreader支持Object[]参数。
     */
    private final MethodHandle[] constructorHandles;

    /**
     * 无参构造器的MethodHandle缓存。
     * 如果存在无参构造器，此字段会被设置，用于快速调用newInstance()。
     */
    private final MethodHandle defaultConstructorHandle;

    private ConstructorHandleInvoker(MethodHandle[] constructorHandles,
                                     MethodHandle defaultConstructorHandle) {
        this.constructorHandles = constructorHandles;
        this.defaultConstructorHandle = defaultConstructorHandle;
    }

    /**
     * 创建基于MethodHandle的构造器调用器
     *
     * @param targetClass 目标类
     * @return ConstructorHandleInvoker 实例
     */
    public static ConstructorHandleInvoker of(Class<?> targetClass) {
        List<Constructor<?>> constructors = ByteBeanReflectUtil.getConstructors(targetClass);
        MethodHandle[] constructorHandles = new MethodHandle[constructors.size()];
        try {
            for (int i = 0; i < constructors.size(); i++) {
                Constructor<?> constructor = constructors.get(i);
                if (constructor.getParameterCount() == 0) {
                    constructorHandles[i] = LOOKUP.unreflectConstructor(constructor)
                            .asType(MethodType.methodType(Object.class));
                } else {
                    constructorHandles[i] = LOOKUP.unreflectConstructor(constructor)
                            .asSpreader(Object[].class, constructor.getParameterCount());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return new ConstructorHandleInvoker(constructorHandles, constructorHandles[0]);
    }

    @Override
    public Object newInstance(int index, Object... args) {
        try {
            return constructorHandles[index].invoke(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object newInstance() {
        try {
            return defaultConstructorHandle.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
