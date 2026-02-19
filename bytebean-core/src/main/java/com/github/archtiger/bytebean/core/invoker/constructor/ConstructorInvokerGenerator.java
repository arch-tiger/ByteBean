package com.github.archtiger.bytebean.core.invoker.constructor;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.api.constructor.ConstructorInvoker;
import com.github.archtiger.bytebean.core.constant.ByteBeanConstant;
import com.github.archtiger.bytebean.core.model.ConstructorInvokerResult;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;
import com.github.archtiger.bytebean.core.utils.NameUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.ClassWriter;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 构造器访问生成器
 * <p>
 * 为目标类生成一个能够通过索引访问所有构造器的辅助类。
 * 使用字节码的 tableswitch 指令实现高效的构造器调用，避免反射开销。
 * 支持自动拆装箱功能。
 * </p>
 *
 * @author ZIJIDELU
 * @since 2026/1/11 21:44
 */
public final class ConstructorInvokerGenerator {
    private static final Map<Class<?>, ConstructorInvokerResult> CACHE = new WeakKeyValueConcurrentMap<>();

    private ConstructorInvokerGenerator() {
    }

    private static ConstructorInvokerResult doCreate(final Class<?> targetClass) {

        // 步骤1: 收集目标类的所有可访问的构造器
        final List<Constructor<?>> constructors = ByteBeanReflectUtil.getConstructors(targetClass);

        // 检查构造器数量是否超过阈值
        if (constructors.size() > ByteBeanConstant.CONSTRUCTOR_SHARDING_THRESHOLD_VALUE) {
            return ConstructorInvokerResult.fail();
        }

        // 检查构造器列表是否为空
        if (constructors.isEmpty()) {
            return ConstructorInvokerResult.fail();
        }

        // 步骤2: 构造生成类的全限定名
        final String invokerName = NameUtil.calcInvokerName(targetClass, ConstructorInvoker.class);
        try {
            final Class<?> aClass = Class.forName(invokerName, false, targetClass.getClassLoader())
                    .asSubclass(ConstructorInvoker.class);
            return ConstructorInvokerResult.success((Class<? extends ConstructorInvoker>) aClass, Collections.unmodifiableList(constructors));
        } catch (ClassNotFoundException e) {
            // Class not generated yet, continue with ByteBuddy generation.
        }

        // 步骤3: 使用 ByteBuddy 动态生成类
        final Class<? extends ConstructorInvoker> invokerClass = new ByteBuddy()
                .subclass(ConstructorInvoker.class)
                .modifiers(Visibility.PUBLIC, TypeManifestation.FINAL)
                // 设置生成类的名称
                .name(invokerName)
                // 定义 newInstance 方法: Object newInstance(int index, Object... args)
                .defineMethod("newInstance", Object.class, Visibility.PUBLIC)
                .withParameters(int.class, Object[].class)
                // 使用 ConstructorAccessImpl 作为方法实现的字节码生成器
                .intercept(new ConstructorByteCode(targetClass, constructors))
                // 定义 newInstance 方法: Object newInstance()
                .defineMethod("newInstance", Object.class, Visibility.PUBLIC)
                // 使用 ConstructorAccessImpl 作为方法实现的字节码生成器
                .intercept(new ConstructorP0ByteCode(targetClass))
                // 自动计算
                .visit(new AsmVisitorWrapper.ForDeclaredMethods()
                        .writerFlags(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
                )
                // 生成字节码
                .make()
                .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        return ConstructorInvokerResult.success(invokerClass, Collections.unmodifiableList(constructors));
    }

    /**
     * 为目标类生成 ConstructorAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 ConstructorAccess 实现类
     */
    static ConstructorInvokerResult generate(Class<?> targetClass) {
        return CACHE.computeIfAbsent(targetClass, ConstructorInvokerGenerator::doCreate);
    }
}
