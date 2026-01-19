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
        List<MethodIdentify> methodIdentifyList,
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
        Comparator<Method> methodComparator = Comparator
                .comparing(Method::getName)
                .thenComparing(m -> Type.getMethodDescriptor(m));

        method0List.sort(methodComparator);
        method1List.sort(methodComparator);
        method2List.sort(methodComparator);
        method3List.sort(methodComparator);
        method4List.sort(methodComparator);
        method5List.sort(methodComparator);
        otherMethodList.sort(methodComparator);

        // 按顺序将特化表加入总表，确保索引连续
        List<MethodIdentify> methodIdentifyList = new ArrayList<>();
        List<MethodIdentify> identifyMethod0List = new ArrayList<>();
        List<MethodIdentify> identifyMethod1List = new ArrayList<>();
        List<MethodIdentify> identifyMethod2List = new ArrayList<>();
        List<MethodIdentify> identifyMethod3List = new ArrayList<>();
        List<MethodIdentify> identifyMethod4List = new ArrayList<>();
        List<MethodIdentify> identifyMethod5List = new ArrayList<>();

        AtomicInteger globalIndex = new AtomicInteger(0);

        // 添加0参数方法（索引连续）
        for (Method method : method0List) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
            identifyMethod0List.add(identify);
        }

        // 添加1参数方法（索引连续）
        for (Method method : method1List) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
            identifyMethod1List.add(identify);
        }

        // 添加2参数方法（索引连续）
        for (Method method : method2List) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
            identifyMethod2List.add(identify);
        }

        // 添加3参数方法（索引连续）
        for (Method method : method3List) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
            identifyMethod3List.add(identify);
        }

        // 添加4参数方法（索引连续）
        for (Method method : method4List) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
            identifyMethod4List.add(identify);
        }

        // 添加5参数方法（索引连续）
        for (Method method : method5List) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
            identifyMethod5List.add(identify);
        }

        // 添加其他方法（超过5个参数）
        for (Method method : otherMethodList) {
            MethodIdentify identify = new MethodIdentify(method, globalIndex.getAndIncrement());
            methodIdentifyList.add(identify);
        }

        return new MethodGroup(
                true,
                methodIdentifyList,
                identifyMethod0List.isEmpty() ? Collections.emptyList() : identifyMethod0List,
                identifyMethod1List.isEmpty() ? Collections.emptyList() : identifyMethod1List,
                identifyMethod2List.isEmpty() ? Collections.emptyList() : identifyMethod2List,
                identifyMethod3List.isEmpty() ? Collections.emptyList() : identifyMethod3List,
                identifyMethod4List.isEmpty() ? Collections.emptyList() : identifyMethod4List,
                identifyMethod5List.isEmpty() ? Collections.emptyList() : identifyMethod5List
        );
    }
}
