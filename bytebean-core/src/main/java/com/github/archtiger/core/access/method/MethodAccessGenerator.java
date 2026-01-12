package com.github.archtiger.core.access.method;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.core.model.MethodAccessInfo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 方法访问生成器
 * <p>
 * 为目标类生成一个能够通过索引访问所有方法的辅助类。
 * 使用字节码的 tableswitch 指令实现高效的方法调用，避免反射开销。
 * </p>
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
public final class MethodAccessGenerator {
    private static final Map<Class<?>, MethodAccessInfo> CACHE = new WeakKeyValueConcurrentMap<>();

    private MethodAccessGenerator() {
    }

    /**
     * 为目标类生成 MethodAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 MethodAccess 实现类
     */
    public static MethodAccessInfo generate(Class<?> targetClass) {
        return CACHE.computeIfAbsent(targetClass, k -> {
            // 步骤1: 收集目标类的所有非静态、可访问的方法
            List<Method> methods = new ArrayList<>();
            Method[] declaredMethods = targetClass.getDeclaredMethods();
            if (declaredMethods.length == 0) {
                return MethodAccessInfo.fail();
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                int mods = method.getModifiers();
                // 跳过静态方法、私有方法
                if (Modifier.isStatic(mods) || Modifier.isPrivate(mods)) {
                    continue;
                }
                methods.add(method);
            }

            // 检查方法列表是否为空
            if (methods.isEmpty()) {
                return MethodAccessInfo.fail();
            }

            // 步骤1.1: 对方法列表进行排序
            methods.sort(Comparator
                    .comparing(Method::getName)
                    .thenComparing(m -> Type.getMethodDescriptor(m))
            );
            // 步骤2: 构造生成类的全限定名
            String name = targetClass.getName() + "$$MethodAccess";

            // 步骤3: 使用 ByteBuddy 动态生成类
            Class<? extends MethodAccess> invokerClass = new ByteBuddy()
                    .subclass(MethodAccess.class)
                    .name(name)
                    // 定义 invoke 方法: Object invoke(int index, Object instance, Object... arguments)
                    .defineMethod("invoke", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new MethodInvokerImpl(targetClass, methods))
                    // 基本类型返回方法
                    .defineMethod("intInvoke", int.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, int.class))
                    .defineMethod("longInvoke", long.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, long.class))
                    .defineMethod("floatInvoke", float.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, float.class))
                    .defineMethod("doubleInvoke", double.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, double.class))
                    .defineMethod("booleanInvoke", boolean.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, boolean.class))
                    .defineMethod("byteInvoke", byte.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, byte.class))
                    .defineMethod("shortInvoke", short.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, short.class))
                    .defineMethod("charInvoke", char.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodInvokerImpl(targetClass, methods, char.class))
                    // 单参数基本类型方法
                    .defineMethod("invokeInt1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, int.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, int.class))
                    .defineMethod("invokeLong1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, long.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, long.class))
                    .defineMethod("invokeFloat1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, float.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, float.class))
                    .defineMethod("invokeDouble1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, double.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, double.class))
                    .defineMethod("invokeBoolean1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, boolean.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, boolean.class))
                    .defineMethod("invokeByte1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, byte.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, byte.class))
                    .defineMethod("invokeShort1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, short.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, short.class))
                    .defineMethod("invokeChar1", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, char.class)
                    .intercept(new MethodPrimitiveP1InvokerImpl(targetClass, methods, char.class))
                    // 生成字节码
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return MethodAccessInfo.success(
                    invokerClass,
                    Collections.unmodifiableList(methods)
            );
        });


    }
}
