package com.github.archtiger.bytebean.core.invoker.constructor;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;
import com.github.archtiger.bytebean.core.model.ConstructorInvokerResult;
import com.github.archtiger.bytebean.core.constant.ByteBeanConstant;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;
import com.github.archtiger.bytebean.core.constant.ExceptionCode;
import com.github.archtiger.bytebean.core.utils.ExceptionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 构造器访问器Helper，提供构造器索引管理和缓存能力。
 * <p>
 * 该类继承自{@link ConstructorInvoker}，在提供构造器调用能力的同时，
 * 维护了构造器参数类型到索引的映射，并支持按目标类进行缓存。
 * <p>
 * <b>特点：</b>
 * <ul>
 *   <li>使用WeakKeyValueConcurrentMap缓存，避免内存泄漏</li>
 *   <li>支持通过参数类型获取构造器索引</li>
 *   <li>支持构造器重载的精确匹配</li>
 *   <li>根据构造器数量自动选择字节码或MethodHandle实现</li>
 * </ul>
 *
 * @author ZIJIDELU
 * @since 1.0.0
 */
public class ConstructorInvokerHelper extends ConstructorInvoker {

    /**
     * ConstructorInvokerHelper缓存，按目标Class索引。
     * 使用WeakKeyValueConcurrentMap确保在目标Class被卸载时自动清除缓存。
     */
    private static final Map<Class<?>, ConstructorInvokerHelper> CONSTRUCTOR_INVOKER_HELPER_CACHE = new WeakKeyValueConcurrentMap<>();

    /**
     * 实际的构造器访问器实现，可能是字节码生成或MethodHandle实现。
     */
    private final ConstructorInvoker constructorInvoker;

    /**
     * 构造器参数类型数组，按索引顺序排列。
     * 每个元素是一个Class[]，表示对应索引构造器的参数类型列表。
     */
    private final Class<?>[][] constructorParameterTypes;

    private ConstructorInvokerHelper(ConstructorInvoker constructorInvoker, Class<?>[][] constructorParameterTypes) {
        this.constructorInvoker = constructorInvoker;
        this.constructorParameterTypes = constructorParameterTypes;
    }

    /**
     * 创建 ConstructorAccessHelper 实例
     *
     * @param targetClass 目标类
     * @return ConstructorAccessHelper 实例
     */
    public static ConstructorInvokerHelper of(Class<?> targetClass) {
        return CONSTRUCTOR_INVOKER_HELPER_CACHE.computeIfAbsent(targetClass, k -> {
            final List<Constructor<?>> constructors = ByteBeanReflectUtil.getConstructors(targetClass);
            final Class<?>[][] constructorParameterTypes = constructors
                    .stream()
                    .map(Constructor::getParameterTypes)
                    .toArray(Class[][]::new);

            // 构造器数量小于等于阈值时，使用字节码调用
            if (constructors.size() <= ByteBeanConstant.CONSTRUCTOR_SHARDING_THRESHOLD_VALUE) {
                final ConstructorInvokerResult constructorInvokerResult = ConstructorInvokerGenerator.generate(targetClass);
                if (!constructorInvokerResult.ok()) {
                    return null;
                }

                try {
                    final ConstructorInvoker constructorInvoker = constructorInvokerResult.constructorInvokerClass().getDeclaredConstructor().newInstance();
                    return new ConstructorInvokerHelper(constructorInvoker, constructorParameterTypes);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            final ConstructorHandleInvoker constructorHandleInvoker = ConstructorHandleInvoker.of(targetClass);
            return new ConstructorInvokerHelper(constructorHandleInvoker, constructorParameterTypes);
        });

    }

    /**
     * 获取构造器索引
     *
     * @param paramTypes 构造器参数类型
     * @return 构造器索引
     */
    public int getConstructorIndex(Class<?>... paramTypes) {
        for (int i = 0, n = constructorParameterTypes.length; i < n; i++) {
            if (Arrays.equals(paramTypes, constructorParameterTypes[i])) {
                return i;
            }
        }

        return ExceptionCode.INVALID_INDEX;
    }

    /**
     * 获取构造器索引
     *
     * @param constructor 构造器对象
     * @return 构造器索引，若不存在则返回 -1
     */
    public int getConstructorIndex(Constructor<?> constructor) {
        return getConstructorIndex(constructor.getParameterTypes());
    }

    /**
     * 获取构造器索引或抛出异常
     *
     * @param paramTypes 构造器参数类型
     * @return 构造器索引
     * @throws IllegalArgumentException 当构造器不存在时抛出
     */
    public int getConstructorIndexOrThrow(Class<?>... paramTypes) {
        int constructorIndex = getConstructorIndex(paramTypes);
        if (constructorIndex == ExceptionCode.INVALID_INDEX) {
            throw ExceptionUtil.constructorNotFound(paramTypes);
        }

        return constructorIndex;
    }

    @Override
    public Object newInstance(int index, Object... args) {
        return constructorInvoker.newInstance(index, args);
    }

    @Override
    public Object newInstance() {
        return constructorInvoker.newInstance();
    }
}
