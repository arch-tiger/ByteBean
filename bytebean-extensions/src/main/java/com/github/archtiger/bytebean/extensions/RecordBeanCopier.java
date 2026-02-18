package com.github.archtiger.bytebean.extensions;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import cn.hutool.core.util.StrUtil;
import com.github.archtiger.bytebean.core.invoker.constructor.ConstructorInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.support.ExceptionCode;
import com.github.archtiger.bytebean.extensions.model.BeanCopierIdentifier;
import com.github.archtiger.bytebean.extensions.model.BeanCopyAction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Record 相关复制器。
 * 负责 record -> record、record -> bean、bean -> record 三种路径。
 *
 * @author ZIJIDELU
 * @datetime 2026/2/18 13:11
 */
public final class RecordBeanCopier {
    private static final Map<BeanCopierIdentifier, BiFunction<?, ?, ?>> BEAN_COPIER_CACHE = new WeakKeyValueConcurrentMap<>();

    private RecordBeanCopier() {
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
    static <O, T> T copy(O origin, T target) {
        // 缓存按来源/目标类型组合构建的复制函数。
        final BeanCopierIdentifier identifier = new BeanCopierIdentifier(origin.getClass(), target.getClass());
        final BiFunction<O, T, T> copier = (BiFunction<O, T, T>) BEAN_COPIER_CACHE.computeIfAbsent(identifier, RecordBeanCopier::createCopier);
        return copier.apply(origin, target);
    }

    /**
     * 构建 Record 场景复制函数。
     */
    private static <O, T> BiFunction<O, T, T> createCopier(BeanCopierIdentifier identifier) {
        // 以目标类型决定后续策略。
        if (identifier.targetClass().isRecord()) {
            return createTargetRecordCopier(identifier);
        }
        return createTargetBeanCopier(identifier);
    }

    /**
     * 目标对象是普通 Bean 的复制逻辑。
     */
    private static <O, T> BiFunction<O, T, T> createTargetBeanCopier(BeanCopierIdentifier identifier) {
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(identifier.originClass());
        final Constructor<?> originConstructor = ByteBeanCopierUtil.calcMaxParameterConstructor(identifier.originClass());
        final Parameter[] parameters = originConstructor.getParameters();
        final int[] originGetterIndexArray = new int[parameters.length];
        Arrays.fill(originGetterIndexArray, ExceptionCode.INVALID_INDEX);

        // 预先定位 record 组件访问器索引。
        for (int i = 0; i < parameters.length; i++) {
            originGetterIndexArray[i] = originMethodInvokerHelper.getMethodIndex(parameters[i].getName());
        }

        // record -> bean：仅复制来源存在且值非 null 的字段。
        final MethodInvokerHelper targetMethodInvokerHelper = MethodInvokerHelper.of(identifier.targetClass());
        final List<BeanCopyAction> beanCopyActions = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            final int originGetterIndex = originGetterIndexArray[i];
            if (originGetterIndex == ExceptionCode.INVALID_INDEX) {
                continue;
            }
            final Parameter parameter = parameters[i];
            final int targetSetterIndex = targetMethodInvokerHelper.getMethodIndex("set" + StrUtil.upperFirst(parameter.getName()), parameter.getType());
            if (targetSetterIndex == ExceptionCode.INVALID_INDEX) {
                continue;
            }
            beanCopyActions.add(new BeanCopyAction(originGetterIndex, targetSetterIndex));
        }

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
        final Constructor<?> maxParameterConstructor = ByteBeanCopierUtil.calcMaxParameterConstructor(identifier.targetClass());
        final Parameter[] parameters = maxParameterConstructor.getParameters();
        for (Parameter parameter : parameters) {

        }

        return null;
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

        return null;
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

        return null;
    }

    /**
     * 目标对象是 Record 的复制逻辑。
     * 对于无法从来源匹配到的字段，保留目标对象当前值。
     */
    private static <O, T> BiFunction<O, T, T> createTargetRecordCopier(BeanCopierIdentifier identifier) {
        // 来源方法访问器用于读取候选值。
        final MethodInvokerHelper originMethodInvokerHelper = MethodInvokerHelper.of(identifier.originClass());
        final boolean originIsRecord = identifier.originClass().isRecord();
        final Map<String, Method> originMethodNameMap = ByteBeanCopierUtil.calcBeanMethodMap(identifier.originClass());

        // 目标 record 的构造器和访问器用于“保留原值 + 部分覆盖”。
        final ConstructorInvokerHelper constructorInvokerHelper = ConstructorInvokerHelper.of(identifier.targetClass());
        final MethodInvokerHelper targetMethodInvokerHelper = MethodInvokerHelper.of(identifier.targetClass());
        final Constructor<?> targetConstructor = ByteBeanCopierUtil.calcMaxParameterConstructor(identifier.targetClass());
        final Parameter[] parameters = targetConstructor.getParameters();
        final int[] originGetterIndexArray = new int[parameters.length];
        final int[] targetGetterIndexArray = new int[parameters.length];
        Arrays.fill(originGetterIndexArray, ExceptionCode.INVALID_INDEX);
        Arrays.fill(targetGetterIndexArray, ExceptionCode.INVALID_INDEX);

        // 针对每个目标参数，分别定位“目标当前值 getter”和“来源候选 getter”。
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            targetGetterIndexArray[i] = targetMethodInvokerHelper.getMethodIndex(parameter.getName());
            final int originGetterIndex = findOriginGetterIndex(originMethodInvokerHelper, originMethodNameMap, parameter, originIsRecord);
            // 来源 getter 未找到时保持 INVALID_INDEX，执行阶段按“跳过该字段”处理。
            if (originGetterIndex == ExceptionCode.INVALID_INDEX) {
                continue;
            }
            originGetterIndexArray[i] = originGetterIndex;
        }

        final int targetConstructorIndex = constructorInvokerHelper.getConstructorIndex(targetConstructor.getParameterTypes());
        if (targetConstructorIndex == ExceptionCode.INVALID_INDEX) {
            return (origin, target) -> target;
        }

        return (origin, target) -> {
            final Object[] originParameterValues = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                // 默认保留目标当前值。
                Object value = null;
                final int targetGetterIndex = targetGetterIndexArray[i];
                if (targetGetterIndex != ExceptionCode.INVALID_INDEX) {
                    value = targetMethodInvokerHelper.invoke(targetGetterIndex, target);
                }

                // 来源存在且非 null 时才覆盖。
                final int originGetterIndex = originGetterIndexArray[i];
                if (originGetterIndex != ExceptionCode.INVALID_INDEX) {
                    final Object originValue = originMethodInvokerHelper.invoke(originGetterIndex, origin);
                    if (originValue != null) {
                        value = originValue;
                    }
                }
                originParameterValues[i] = value;
            }
            return (T) constructorInvokerHelper.newInstance(targetConstructorIndex, originParameterValues);
        };
    }

    /**
     * 根据目标参数从来源对象中寻找 getter。
     */
    private static int findOriginGetterIndex(MethodInvokerHelper originMethodInvokerHelper,
                                             Map<String, Method> originMethodNameMap,
                                             Parameter parameter,
                                             boolean originIsRecord
    ) {
        // 来源是 Record 时，组件名就是访问器名。
        final String parameterName = parameter.getName();
        if (originIsRecord) {
            if (!originMethodNameMap.containsKey(parameterName)) {
                return ExceptionCode.INVALID_INDEX;
            }
            return originMethodInvokerHelper.getMethodIndex(parameterName);
        }

        // 来源是普通 Bean 时，优先尝试 isXxx/getXxx，再兜底同名方法。
        final String upperFirstParameterName = StrUtil.upperFirst(parameterName);
        if (parameter.getType() == boolean.class || parameter.getType() == Boolean.class) {
            final String isMethodName = "is" + upperFirstParameterName;
            if (originMethodNameMap.containsKey(isMethodName)) {
                final int isMethodIndex = originMethodInvokerHelper.getMethodIndex(isMethodName);
                if (isMethodIndex != ExceptionCode.INVALID_INDEX) {
                    return isMethodIndex;
                }
            }
        }

        final String getMethodName = "get" + upperFirstParameterName;
        if (originMethodNameMap.containsKey(getMethodName)) {
            final int getMethodIndex = originMethodInvokerHelper.getMethodIndex(getMethodName);
            if (getMethodIndex != ExceptionCode.INVALID_INDEX) {
                return getMethodIndex;
            }
        }

        if (originMethodNameMap.containsKey(parameterName)) {
            final int sameNameMethodIndex = originMethodInvokerHelper.getMethodIndex(parameterName);
            if (sameNameMethodIndex != ExceptionCode.INVALID_INDEX) {
                return sameNameMethodIndex;
            }
        }

        // 所有候选 getter 都不存在，显式返回 INVALID_INDEX，让调用侧跳过该字段。
        return ExceptionCode.INVALID_INDEX;
    }


}
