package com.github.archtiger.core.invoker.constructor;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.core.model.ConstructorInvokerResult;
import com.github.archtiger.core.support.NameUtil;
import com.github.archtiger.definition.constructor.ConstructorInvoker;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 构造器访问生成器
 * <p>
 * 为目标类生成一个能够通过索引访问所有构造器的辅助类。
 * 使用字节码的 tableswitch 指令实现高效的构造器调用，避免反射开销。
 * 支持自动拆装箱功能。
 * </p>
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11 21:44
 */
public final class ConstructorInvokerGenerator {
    private static final Map<Class<?>, ConstructorInvokerResult> CACHE = new WeakKeyValueConcurrentMap<>();

    private ConstructorInvokerGenerator() {
    }

    /**
     * 为目标类生成 ConstructorAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 ConstructorAccess 实现类
     */
    public static ConstructorInvokerResult generate(Class<?> targetClass) {
        return CACHE.computeIfAbsent(targetClass, k -> {

            // 步骤1: 收集目标类的所有可访问的构造器
            List<Constructor<?>> constructors = new ArrayList<>();
            Constructor<?>[] declaredConstructors = targetClass.getDeclaredConstructors();
            if (declaredConstructors.length == 0) {
                return ConstructorInvokerResult.fail();
            }

            for (Constructor<?> constructor : declaredConstructors) {
                int mods = constructor.getModifiers();
                // 跳过私有构造器
                if (Modifier.isPrivate(mods)) {
                    continue;
                }
                constructors.add(constructor);
            }

            // 检查构造器列表是否为空
            if (constructors.isEmpty()) {
                return ConstructorInvokerResult.fail();
            }

            // 步骤1.1: 对构造器列表进行排序
            // 排序规则: 先按参数数量排序，再按参数类型描述符排序
            constructors.sort(Comparator
                    .comparing((Constructor<?> c) -> c.getParameterTypes().length)
                    .thenComparing(Type::getConstructorDescriptor)
            );

            // 步骤2: 构造生成类的全限定名
            String invokerName = NameUtil.calcInvokerName(targetClass, ConstructorInvoker.class);

            // 步骤3: 使用 ByteBuddy 动态生成类
            Class<? extends ConstructorInvoker> invokerClass = new ByteBuddy()
                    .subclass(ConstructorInvoker.class)
                    // 设置生成类的名称
                    .name(invokerName)
                    // 定义 newInstance 方法: Object newInstance(int index, Object target, Object... args)
                    .defineMethod("newInstance", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object[].class)
                    // 使用 ConstructorAccessImpl 作为方法实现的字节码生成器
                    .intercept(new ConstructorInvokerImpl(targetClass, constructors))
                    // 生成字节码
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return ConstructorInvokerResult.success(invokerClass, Collections.unmodifiableList(constructors));
        });

    }
}
