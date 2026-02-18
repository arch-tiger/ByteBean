package com.github.archtiger.bytebean.extensions;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.core.invoker.constructor.ConstructorInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.extensions.model.BeanCopierIdentifier;
import com.github.archtiger.bytebean.extensions.model.BeanCopyAction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * BeanCopier
 * 支持Record
 *
 * @author ZIJIDELU
 * @datetime 2026/2/18 13:11
 */
public final class BeanCopier {
    private static final Map<BeanCopierIdentifier, BiFunction<?, ?, ?>> BEAN_COPIER_CACHE = new WeakKeyValueConcurrentMap<>();

    private BeanCopier() {
        // 工具类不允许实例化。
    }

    /**
     * 执行 Record 场景复制。
     *
     * @param origin 来源对象
     * @param target 目标对象
     * @param <O>    来源类型
     * @param <T>    目标类型
     * @return 复制后的目标对象
     */
    @SuppressWarnings("unchecked")
    public static <O, T> T copy(O origin, T target) {
        // 缓存按来源/目标类型组合构建的复制函数。
        final BeanCopierIdentifier identifier = new BeanCopierIdentifier(origin.getClass(), target.getClass());
        final BiFunction<O, T, T> copier = (BiFunction<O, T, T>) BEAN_COPIER_CACHE.computeIfAbsent(identifier, BeanCopier::createCopier);
        return copier.apply(origin, target);
    }

    /**
     * 创建copier
     *
     * @param identifier id
     * @param <O>        来源类型
     * @param <T>        目标类型
     * @return copier
     */
    private static <O, T> BiFunction<O, T, T> createCopier(BeanCopierIdentifier identifier) {
        // record -> record
        if (identifier.originClass().isRecord() && identifier.targetClass().isRecord()) {
            return createRecordToRecordCopier(identifier);
        }

        // record -> bean
        if (identifier.originClass().isRecord() && !identifier.targetClass().isRecord()) {
            return createRecordToBeanCopier(identifier);
        }

        // bean -> record
        if (!identifier.originClass().isRecord() && identifier.targetClass().isRecord()) {
            return createBeanToRecordCopier(identifier);
        }

        // bean -> bean
        return createBeanToBeanCopier(identifier);
    }

    /**
     * record -> bean：仅复制来源存在且值非 null 的字段。
     *
     * @param identifier 复制器标识
     * @param <O>        来源类型
     * @param <T>        目标类型
     * @return 复制函数
     */
    private static <O, T> BiFunction<O, T, T> createRecordToBeanCopier(BeanCopierIdentifier identifier) {
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(identifier.originClass());
        final MethodInvokerHelper targetMethodInvokerHelper = MethodInvokerHelper.of(identifier.targetClass());

        // 来源对象是Record不需要构建getter前缀
        final Map<String, Method> originGetterMethodMap = ByteBeanCopierUtil.calcBeanMethodMap(identifier.originClass());
        // 目标对象
        final Map<String, Method> targetSetterMethodMap = ByteBeanCopierUtil.calcBeanSetterMethodMap(identifier.targetClass());
        final List<BeanCopyAction> beanCopyActions = new ArrayList<>();

        targetSetterMethodMap.forEach((setterName, setterMethod) -> {
            final String fieldName = ByteBeanCopierUtil.calcFieldNameWithSetter(setterName);
            // 来源对象Record找不到对应的Getter
            final Method getterMethod = originGetterMethodMap.get(fieldName);
            if (getterMethod == null) {
                return;
            }

            // 来源对象Record找不到对应的Getter
            if (!ByteBeanCopierUtil.isMatchGetterAndSetterType(getterMethod, setterMethod)) {
                return;
            }

            // 目标类型 setter 找不到对应的 getter 方法时，跳过。
            final int originGetterIndex = originMethodInvokerHelper.getMethodIndex(fieldName);
            if (originGetterIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }

            // 目标类型 setter 方法参数类型与来源类型组件类型不同时，跳过。
            final int targetSetterIndex = targetMethodInvokerHelper.getMethodIndex(setterName, setterMethod.getParameterTypes()[0]);
            if (targetSetterIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }

            beanCopyActions.add(new BeanCopyAction(originGetterIndex, targetSetterIndex));
        });

        final BeanCopyAction[] actionArray = beanCopyActions.toArray(new BeanCopyAction[0]);

        return (origin, target) -> {
            // 仅来源值非 null 时覆盖目标字段。
            for (BeanCopyAction beanCopyAction : actionArray) {
                final Object getterValue = originMethodInvokerHelper.invoke(beanCopyAction.originGetterIndex(), origin);
                if (getterValue != null) {
                    targetMethodInvokerHelper.invoke1(beanCopyAction.targetSetterIndex(), target, getterValue);
                }
            }
            return target;
        };
    }

    /**
     * bean -> record：仅复制来源存在且值非 null 的字段。
     *
     * @param identifier 复制器标识
     * @param <O>        来源类型
     * @param <T>        目标类型
     * @return 复制函数
     */
    private static <O, T> BiFunction<O, T, T> createBeanToRecordCopier(BeanCopierIdentifier identifier) {
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(identifier.originClass());
        final Map<String, Method> originGetterMethodMap = ByteBeanCopierUtil.calcBeanGetterMethodMap(identifier.originClass());
        final Constructor<?> maxParameterConstructor = ByteBeanCopierUtil.calcMaxParameterConstructor(identifier.targetClass());
        final Parameter[] parameters = maxParameterConstructor.getParameters();
        final int[] getterIndexArray = new int[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            // 获取来源对象Getter方法名
            final String getterName = ByteBeanCopierUtil.calcGetterName(parameter.getName(), parameter.getType());
            final Method getterMethod = originGetterMethodMap.get(getterName);
            if (getterMethod == null) {
                getterIndexArray[i] = ExceptionCode.INVALID_INDEX;
                continue;
            }

            // 来源对象Getter方法返回类型与目标参数类型不同时，跳过。
            if (!getterMethod.getReturnType().equals(parameter.getType())) {
                getterIndexArray[i] = ExceptionCode.INVALID_INDEX;
                continue;
            }

            getterIndexArray[i] = originMethodInvokerHelper.getMethodIndex(getterName);
        }

        final ConstructorInvokerHelper constructorInvokerHelper = ConstructorInvokerHelper.of(identifier.targetClass());
        final int maxConstructorIndex = constructorInvokerHelper.getConstructorIndex(maxParameterConstructor.getParameterTypes());
        return (origin, target) -> {
            final Object[] args = new Object[getterIndexArray.length];
            for (int i = 0; i < getterIndexArray.length; i++) {
                final int getterIndex = getterIndexArray[i];
                if (getterIndex == ExceptionCode.INVALID_INDEX) {
                    args[i] = null;
                    continue;
                }

                args[i] = originMethodInvokerHelper.invoke(getterIndex, origin);
            }
            return (T) constructorInvokerHelper.newInstance(maxConstructorIndex, args);
        };
    }

    /**
     * bean -> bean：仅复制来源存在且值非 null 的字段。
     *
     * @param identifier 复制器标识
     * @param <O>        来源类型
     * @param <T>        目标类型
     * @return 复制函数
     */
    private static <O, T> BiFunction<O, T, T> createBeanToBeanCopier(BeanCopierIdentifier identifier) {
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(identifier.originClass());
        final MethodInvokerHelper targetMethodInvokerHelper = MethodInvokerHelper.of(identifier.targetClass());

        // 来源对象是Record不需要构建getter前缀
        final Map<String, Method> originGetterMethodMap = ByteBeanCopierUtil.calcBeanMethodMap(identifier.originClass());
        // 目标对象
        final Map<String, Method> targetSetterMethodMap = ByteBeanCopierUtil.calcBeanSetterMethodMap(identifier.targetClass());
        final List<BeanCopyAction> beanCopyActions = new ArrayList<>();

        targetSetterMethodMap.forEach((setterName, setterMethod) -> {
            final String fieldName = ByteBeanCopierUtil.calcFieldNameWithSetter(setterName);
            final String getterName = ByteBeanCopierUtil.calcGetterName(fieldName, setterMethod.getParameterTypes()[0]);
            // 来源对象Record找不到对应的Getter
            final Method getterMethod = originGetterMethodMap.get(getterName);
            if (getterMethod == null) {
                return;
            }

            // 来源对象Record找不到对应的Getter
            if (!ByteBeanCopierUtil.isMatchGetterAndSetterType(getterMethod, setterMethod)) {
                return;
            }

            // 目标类型 setter 找不到对应的 getter 方法时，跳过。
            final int originGetterIndex = originMethodInvokerHelper.getMethodIndex(getterName);
            if (originGetterIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }

            // 目标类型 setter 方法参数类型与来源类型组件类型不同时，跳过。
            final int targetSetterIndex = targetMethodInvokerHelper.getMethodIndex(setterName, setterMethod.getParameterTypes()[0]);
            if (targetSetterIndex == ExceptionCode.INVALID_INDEX) {
                return;
            }

            beanCopyActions.add(new BeanCopyAction(originGetterIndex, targetSetterIndex));
        });

        final BeanCopyAction[] actionArray = beanCopyActions.toArray(new BeanCopyAction[0]);

        return (origin, target) -> {
            // 仅来源值非 null 时覆盖目标字段。
            for (BeanCopyAction beanCopyAction : actionArray) {
                final Object getterValue = originMethodInvokerHelper.invoke(beanCopyAction.originGetterIndex(), origin);
                if (getterValue != null) {
                    targetMethodInvokerHelper.invoke1(beanCopyAction.targetSetterIndex(), target, getterValue);
                }
            }
            return target;
        };
    }

    /**
     * record -> record：仅复制来源存在且值非 null 的字段。
     *
     * @param identifier 复制器标识
     * @param <O>        来源类型
     * @param <T>        目标类型
     * @return 复制函数
     */
    private static <O, T> BiFunction<O, T, T> createRecordToRecordCopier(BeanCopierIdentifier identifier) {
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(identifier.originClass());
        final Map<String, Method> originGetterMethodMap = ByteBeanCopierUtil.calcBeanMethodMap(identifier.originClass());
        final Constructor<?> maxParameterConstructor = ByteBeanCopierUtil.calcMaxParameterConstructor(identifier.targetClass());
        final Parameter[] parameters = maxParameterConstructor.getParameters();
        final int[] getterIndexArray = new int[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            // 获取来源对象Getter方法名
            final Method getterMethod = originGetterMethodMap.get(parameter.getName());
            if (getterMethod == null) {
                getterIndexArray[i] = ExceptionCode.INVALID_INDEX;
                continue;
            }

            // 来源对象Getter方法返回类型与目标参数类型不同时，跳过。
            if (!getterMethod.getReturnType().equals(parameter.getType())) {
                getterIndexArray[i] = ExceptionCode.INVALID_INDEX;
                continue;
            }

            getterIndexArray[i] = originMethodInvokerHelper.getMethodIndex(parameter.getName());
        }

        final ConstructorInvokerHelper constructorInvokerHelper = ConstructorInvokerHelper.of(identifier.targetClass());
        final int maxConstructorIndex = constructorInvokerHelper.getConstructorIndex(maxParameterConstructor.getParameterTypes());
        return (origin, target) -> {
            final Object[] args = new Object[getterIndexArray.length];
            for (int i = 0; i < getterIndexArray.length; i++) {
                final int getterIndex = getterIndexArray[i];
                if (getterIndex == ExceptionCode.INVALID_INDEX) {
                    args[i] = null;
                    continue;
                }

                args[i] = originMethodInvokerHelper.invoke(getterIndex, origin);
            }
            return (T) constructorInvokerHelper.newInstance(maxConstructorIndex, args);
        };
    }

}
