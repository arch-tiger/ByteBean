package com.github.archtiger.bytebean.core.model;

import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 方法分组，按参数数量分组，保证参数0-5的特化表全局索引连续
 *
 * @author ZIJIDELU
 * @datetime 2026/1/19 15:07
 */
public record MethodGroup(
        boolean ok,
        List<MethodIdentify> methodAllList,
        List<MethodIdentify> method0List,
        List<MethodIdentify> method1List,
        List<MethodIdentify> method2List,
        List<MethodIdentify> method3List,
        List<MethodIdentify> method4List,
        List<MethodIdentify> method5List
) {
    private static final MethodGroup FAIL_METHOD_GROUP = new MethodGroup(
            false,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
    );

    public static MethodGroup of(Class<?> targetClass) {
        List<Method> methods = ByteBeanReflectUtil.getMethods(targetClass);
        if (methods.isEmpty()) {
            return FAIL_METHOD_GROUP;
        }

        // 分类：按参数数量分组
        List<Method> method0List = new ArrayList<>();
        List<Method> method1List = new ArrayList<>();
        List<Method> method2List = new ArrayList<>();
        List<Method> method3List = new ArrayList<>();
        List<Method> method4List = new ArrayList<>();
        List<Method> method5List = new ArrayList<>();
        List<Method> otherMethodList = new ArrayList<>();  // 超过5个参数的方法

        for (Method method : methods) {
            int paramCount = method.getParameterCount();
            if (paramCount == 0) {
                method0List.add(method);
            } else if (paramCount == 1) {
                method1List.add(method);
            } else if (paramCount == 2) {
                method2List.add(method);
            } else if (paramCount == 3) {
                method3List.add(method);
            } else if (paramCount == 4) {
                method4List.add(method);
            } else if (paramCount == 5) {
                method5List.add(method);
            } else {
                otherMethodList.add(method);
            }
        }

        // 对每个特化表内部排序：按 方法名 + 参数类型 + 参数数量
        final Comparator<Method> methodComparator = Comparator
                .comparing(Method::getName)
                .thenComparing(m -> Type.getMethodDescriptor(m));

        method0List.sort(methodComparator);
        method1List.sort(methodComparator);
        method2List.sort(methodComparator);
        method3List.sort(methodComparator);
        method4List.sort(methodComparator);
        method5List.sort(methodComparator);
        otherMethodList.sort(methodComparator);

        final AtomicInteger globalIndex = new AtomicInteger(0);

        List<MethodIdentify> identifyMethodAllList = new ArrayList<>();

        List<MethodIdentify> identifyMethod0List = method0List.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        List<MethodIdentify> identifyMethod1List = method1List.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        List<MethodIdentify> identifyMethod2List = method2List.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        List<MethodIdentify> identifyMethod3List = method3List.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        List<MethodIdentify> identifyMethod4List = method4List.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        List<MethodIdentify> identifyMethod5List = method5List.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        List<MethodIdentify> identifyOtherMethodList = otherMethodList.stream()
                .map(method -> new MethodIdentify(method, globalIndex.getAndIncrement()))
                .toList();
        // 按顺序将特化表加入总表，确保索引连续
        identifyMethodAllList.addAll(identifyMethod0List);
        identifyMethodAllList.addAll(identifyMethod1List);
        identifyMethodAllList.addAll(identifyMethod2List);
        identifyMethodAllList.addAll(identifyMethod3List);
        identifyMethodAllList.addAll(identifyMethod4List);
        identifyMethodAllList.addAll(identifyMethod5List);
        identifyMethodAllList.addAll(identifyOtherMethodList);

        return new MethodGroup(
                true,
                identifyMethodAllList,
                identifyMethod0List.isEmpty() ? Collections.emptyList() : identifyMethod0List,
                identifyMethod1List.isEmpty() ? Collections.emptyList() : identifyMethod1List,
                identifyMethod2List.isEmpty() ? Collections.emptyList() : identifyMethod2List,
                identifyMethod3List.isEmpty() ? Collections.emptyList() : identifyMethod3List,
                identifyMethod4List.isEmpty() ? Collections.emptyList() : identifyMethod4List,
                identifyMethod5List.isEmpty() ? Collections.emptyList() : identifyMethod5List
        );
    }
}
