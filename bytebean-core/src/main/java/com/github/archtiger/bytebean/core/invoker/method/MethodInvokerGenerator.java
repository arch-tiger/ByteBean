package com.github.archtiger.bytebean.core.invoker.method;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.model.MethodGroup;
import com.github.archtiger.bytebean.core.model.MethodIdentify;
import com.github.archtiger.bytebean.core.model.MethodInvokerResult;
import com.github.archtiger.bytebean.core.support.NameUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.ClassWriter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

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
public final class MethodInvokerGenerator {
    private static final Map<Class<?>, MethodInvokerResult> CACHE = new WeakKeyValueConcurrentMap<>();

    private MethodInvokerGenerator() {
    }

    /**
     * 为目标类生成 MethodAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 MethodAccess 实现类
     */
    public static MethodInvokerResult generate(Class<?> targetClass) {
        return CACHE.computeIfAbsent(targetClass, k -> {
            // 步骤1: 收集目标类的所有非静态、可访问的方法
            MethodGroup methodGroup = MethodGroup.of(targetClass);

            // 检查方法列表是否为空
            if (!methodGroup.ok()) {
                return MethodInvokerResult.fail();
            }

            // 步骤2: 构造生成类的全限定名
            String invokerName = NameUtil.calcInvokerName(targetClass, MethodInvoker.class);

            // 步骤3: 使用 ByteBuddy 动态生成类
            Class<? extends MethodInvoker> invokerClass = new ByteBuddy()
                    .subclass(MethodInvoker.class)
                    .modifiers(Visibility.PUBLIC, TypeManifestation.FINAL)
                    .name(invokerName)
                    // 定义 invoke 方法: Object invoke(int index, Object instance, Object... arguments)
                    .defineMethod("invoke", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new MethodByteCode(targetClass, methodGroup.methodAllList()))
                    // 定义 invoke 方法: Object invoke(int index, Object instance)
                    .defineMethod("invoke", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class)
                    .intercept(new MethodP0ByteCode(targetClass, methodGroup.method0List()))
                    // 定义 invoke1 方法: Object invoke(int index, Object instance, Object arg0)
                    .defineMethod("invoke1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object.class)
                    .intercept(new MethodP1ByteCode(targetClass, methodGroup.method1List()))
                    // 定义 invoke2 方法: Object invoke(int index, Object instance, Object arg0, Object arg1)
                    .defineMethod("invoke2", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object.class, Object.class)
                    .intercept(new MethodP2ByteCode(targetClass, methodGroup.method2List()))
                    // 定义 invoke3 方法: Object invoke(int index, Object instance, Object arg0, Object arg1, Object arg2)
                    .defineMethod("invoke3", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object.class, Object.class, Object.class)
                    .intercept(new MethodP3ByteCode(targetClass, methodGroup.method3List()))
                    // 定义 invoke4 方法: Object invoke(int index, Object instance, Object arg0, Object arg1, Object arg2, Object arg3)
                    .defineMethod("invoke4", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object.class, Object.class, Object.class, Object.class)
                    .intercept(new MethodP4ByteCode(targetClass, methodGroup.method4List()))
                    // 定义 invoke5 方法: Object invoke(int index, Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4)
                    .defineMethod("invoke5", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class)
                    .intercept(new MethodP5ByteCode(targetClass, methodGroup.method5List()))
                    // 基本类型返回方法
                    .defineMethod("intInvoke", int.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), int.class))
                    .defineMethod("longInvoke", long.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), long.class))
                    .defineMethod("floatInvoke", float.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), float.class))
                    .defineMethod("doubleInvoke", double.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), double.class))
                    .defineMethod("booleanInvoke", boolean.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), boolean.class))
                    .defineMethod("byteInvoke", byte.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), byte.class))
                    .defineMethod("shortInvoke", short.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), short.class))
                    .defineMethod("charInvoke", char.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, Object[].class)
                    .intercept(new PrimitiveMethodByteCode(targetClass, methodGroup.methodAllList(), char.class))
                    // 单参数基本类型方法
                    .defineMethod("invokeInt1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, int.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), int.class))
                    .defineMethod("invokeLong1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, long.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), long.class))
                    .defineMethod("invokeFloat1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, float.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), float.class))
                    .defineMethod("invokeDouble1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, double.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), double.class))
                    .defineMethod("invokeBoolean1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, boolean.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), boolean.class))
                    .defineMethod("invokeByte1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, byte.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), byte.class))
                    .defineMethod("invokeShort1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, short.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), short.class))
                    .defineMethod("invokeChar1", Object.class, Visibility.PUBLIC, MethodManifestation.FINAL)
                    .withParameters(int.class, Object.class, char.class)
                    .intercept(new MethodPrimitiveP1ByteCode(targetClass, methodGroup.method1List(), char.class))
                    // 自动计算
                    .visit(new AsmVisitorWrapper.ForDeclaredMethods()
                            .writerFlags(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
                    )
                    // 生成字节码
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return MethodInvokerResult.success(
                    invokerClass,
                    Collections.unmodifiableList(methodGroup.methodAllList().stream().map(MethodIdentify::method).collect(Collectors.toList()))
            );
        });


    }
}
