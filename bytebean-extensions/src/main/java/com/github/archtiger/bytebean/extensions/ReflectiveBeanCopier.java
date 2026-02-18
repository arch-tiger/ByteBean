package com.github.archtiger.bytebean.extensions;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.extensions.model.BeanCopierIdentifier;
import com.github.archtiger.bytebean.extensions.model.BeanCopyAction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 普通 Bean（非 Record）复制器。
 * 通过 getter/setter 映射构建复制动作，并按类型对复制函数进行缓存。
 *
 * @author ZIJIDELU
 * @datetime 2026/2/18 13:10
 */
public final class ReflectiveBeanCopier {
    private static final Map<BeanCopierIdentifier, BiFunction<?, ?, ?>> BEAN_COPIER_CACHE = new WeakKeyValueConcurrentMap<>();

    private ReflectiveBeanCopier() {
        // 工具类不允许实例化。
    }

    /**
     * 执行普通 Bean 复制。
     *
     * @param origin 来源对象
     * @param target 目标对象
     * @param <O>    来源类型
     * @param <T>    目标类型
     * @return 复制后的目标对象
     */
    @SuppressWarnings("unchecked")
    static <O, T> T copy(O origin, T target) {
        // 按来源/目标类型缓存复制函数，避免重复反射分析。
        final BeanCopierIdentifier identifier = new BeanCopierIdentifier(origin.getClass(), target.getClass());
        final BiFunction<O, T, T> copier = (BiFunction<O, T, T>) BEAN_COPIER_CACHE.computeIfAbsent(identifier, ReflectiveBeanCopier::createCopier);
        return copier.apply(origin, target);
    }

    /**
     * 构建复制函数（仅处理非 Record 场景）。
     */
    private static <O, T> BiFunction<O, T, T> createCopier(BeanCopierIdentifier identifier) {
        // 固化来源/目标类型，并初始化方法调用助手。
        final Class<O> originClass = (Class<O>) identifier.originClass();
        final Class<T> targetClass = (Class<T>) identifier.targetClass();
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(originClass);
        final MethodInvokerHelper targetMethodInvokerHelper = MethodInvokerHelper.of(targetClass);

        // 提前提取来源 getter 与目标 setter，避免执行阶段再做反射扫描。
        final Map<String, Method> originGetterMethodMap = ByteBeanCopierUtil.calcBeanGetterMethodMap(originClass);
        final Map<String, Method> targetSetterMethodMap = ByteBeanCopierUtil.calcBeanSetterMethodMap(targetClass);

        // 预计算 getter -> setter 的索引映射，复制时只做调用，不再查找方法。
        final List<BeanCopyAction> beanCopyActions = new ArrayList<>();
        originGetterMethodMap.forEach((getterMethodName, getterMethod) -> {
            // 将 getter 方法转换为可直接调用的索引。
            final int getterMethodIndex = originMethodInvokerHelper.getMethodIndex(getterMethod.getName(), getterMethod.getParameterTypes());
            if (getterMethodIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }

            // 根据 getter 命名规则推导目标 setter 名称。
            final String setterMethodName;
            if (ByteBeanCopierUtil.isGetterWithGet(getterMethod)) {
                setterMethodName = "set" + getterMethodName.substring(3);
            } else if (ByteBeanCopierUtil.isGetterWithIs(getterMethod)) {
                setterMethodName = "set" + getterMethodName.substring(2);
            } else {
                return;
            }

            // 目标中不存在对应 setter 时直接跳过该字段。
            final Method setterMethod = targetSetterMethodMap.get(setterMethodName);
            if (setterMethod == null) {
                return;
            }

            // setter 可调用时记录动作，后续执行阶段按索引复制。
            final int setterMethodIndex = targetMethodInvokerHelper.getMethodIndex(setterMethod.getName(), setterMethod.getParameterTypes());
            if (setterMethodIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }
            beanCopyActions.add(new BeanCopyAction(getterMethodIndex, setterMethodIndex));
        });

        final BeanCopyAction[] actionArray = beanCopyActions.toArray(new BeanCopyAction[0]);
        return (origin, target) -> {
            // 运行阶段仅按索引执行调用，提高复制性能。
            for (BeanCopyAction beanCopyAction : actionArray) {
                final Object getterValue = originMethodInvokerHelper.invoke(beanCopyAction.originGetterIndex(), origin);
                if (getterValue != null) {
                    targetMethodInvokerHelper.invoke1(beanCopyAction.targetSetterIndex(), target, getterValue);
                }
            }
            return target;
        };
    }

}
