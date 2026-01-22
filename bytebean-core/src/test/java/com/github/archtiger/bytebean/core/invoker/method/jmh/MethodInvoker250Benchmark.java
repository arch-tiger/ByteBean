package com.github.archtiger.bytebean.core.invoker.method.jmh;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.archtiger.bytebean.core.invoker.method.MethodHandleInvoker;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.entity.Field250Entity;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Method;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MethodInvoker性能测试 - 250字段版本
 * 只测试 getter 和 setter 调用，对比5种方式的性能
 * 所有字段均为对象类型（Integer）
 * 
 * 注意：与其他测试保持一致，不包含 DirectCall 测试
 *
 * @author ZIJIDELU
 * @datetime 2026/1/16
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MethodInvoker250Benchmark {

    private static final int TEST_COUNT = 250;

    private Field250Entity entity;

    // ========== MethodHandle ==========
    private List<MethodHandle> methodHandleGetters;
    private List<MethodHandle> methodHandleSetters;

    // ========== Reflection ==========
    private List<Method> reflectionGetters;
    private List<Method> reflectionSetters;

    // ========== ReflectASM ==========
    private MethodAccess reflectasmMethodAccess;
    private List<Integer> reflectasmGetterIndexes;
    private List<Integer> reflectasmSetterIndexes;

    // ========== MethodInvokerHelper ==========
    private MethodInvokerHelper methodInvokerHelper;
    private List<Integer> methodInvokerHelperGetterIndexes;
    private List<Integer> methodInvokerHelperSetterIndexes;

    // ========== MethodHandleInvoker ==========
    private MethodHandleInvoker methodHandleInvoker;
    private List<Integer> methodHandleInvokerGetterIndexes;
    private List<Integer> methodHandleInvokerSetterIndexes;

    // ========== Hutool ReflectUtil ==========
    private List<Method> hutoolGetters;
    private List<Method> hutoolSetters;

    private Integer testValue = 250;

    @Setup(Level.Trial)
    public void setup() throws Throwable {
        entity = new Field250Entity();

        // 初始化 MethodHandle
        methodHandleGetters = new ArrayList<>(TEST_COUNT);
        methodHandleSetters = new ArrayList<>(TEST_COUNT);

        // 初始化反射
        reflectionGetters = new ArrayList<>(TEST_COUNT);
        reflectionSetters = new ArrayList<>(TEST_COUNT);

        // 初始化 ReflectASM
        reflectasmMethodAccess = MethodAccess.get(Field250Entity.class);
        reflectasmGetterIndexes = new ArrayList<>(TEST_COUNT);
        reflectasmSetterIndexes = new ArrayList<>(TEST_COUNT);

        // 初始化 MethodInvokerHelper
        methodInvokerHelper = MethodInvokerHelper.of(Field250Entity.class);
        methodInvokerHelperGetterIndexes = new ArrayList<>(TEST_COUNT);
        methodInvokerHelperSetterIndexes = new ArrayList<>(TEST_COUNT);

        // 初始化 MethodHandleInvoker
        methodHandleInvoker = MethodHandleInvoker.of(Field250Entity.class);
        methodHandleInvokerGetterIndexes = new ArrayList<>(TEST_COUNT);
        methodHandleInvokerSetterIndexes = new ArrayList<>(TEST_COUNT);

        // 初始化 Hutool ReflectUtil
        hutoolGetters = new ArrayList<>(TEST_COUNT);
        hutoolSetters = new ArrayList<>(TEST_COUNT);

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (int i = 1; i <= TEST_COUNT; i++) {
            String fieldName = "field" + i;
            String capitalizedName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String getterName = "get" + capitalizedName;
            String setterName = "set" + capitalizedName;

            // 获取反射方法 - 参数类型为 Integer.class
            Method getter = Field250Entity.class.getMethod(getterName);
            Method setter = Field250Entity.class.getMethod(setterName, Integer.class);
            reflectionGetters.add(getter);
            reflectionSetters.add(setter);

            // 获取 MethodHandle
            MethodHandle getterHandle = lookup.unreflect(getter);
            MethodHandle setterHandle = lookup.unreflect(setter);
            methodHandleGetters.add(getterHandle);
            methodHandleSetters.add(setterHandle);

            // 获取 ReflectASM 索引 - 参数类型为 Integer.class
            int reflectasmGetterIndex = reflectasmMethodAccess.getIndex(getterName);
            int reflectasmSetterIndex = reflectasmMethodAccess.getIndex(setterName, Integer.class);
            reflectasmGetterIndexes.add(reflectasmGetterIndex);
            reflectasmSetterIndexes.add(reflectasmSetterIndex);

            // 获取 MethodInvokerHelper 索引 - 参数类型为 Integer.class
            int methodInvokerHelperGetterIndex = methodInvokerHelper.getMethodIndex(getterName);
            int methodInvokerHelperSetterIndex = methodInvokerHelper.getMethodIndex(setterName, Integer.class);
            methodInvokerHelperGetterIndexes.add(methodInvokerHelperGetterIndex);
            methodInvokerHelperSetterIndexes.add(methodInvokerHelperSetterIndex);

            // 获取 MethodHandleInvoker 索引 - 参数类型为 Integer.class（从 MethodInvokerHelper 获取）
            int methodHandleInvokerGetterIndex = methodInvokerHelper.getMethodIndex(getterName);
            int methodHandleInvokerSetterIndex = methodInvokerHelper.getMethodIndex(setterName, Integer.class);
            methodHandleInvokerGetterIndexes.add(methodHandleInvokerGetterIndex);
            methodHandleInvokerSetterIndexes.add(methodHandleInvokerSetterIndex);

            // 获取 Hutool ReflectUtil 方法
            hutoolGetters.add(getter);
            hutoolSetters.add(setter);
        }
    }

    // ==================== MethodHandle ====================

    @Benchmark
public void test_MethodHandle_Getter() throws Throwable {
        for (int i = 0; i < TEST_COUNT; i++) {
            MethodHandle getter = methodHandleGetters.get(i);
            Object result = getter.invoke(entity);
        }
    }

    @Benchmark
    public void test_MethodHandle_Setter() throws Throwable {
        for (int i = 0; i < TEST_COUNT; i++) {
            MethodHandle setter = methodHandleSetters.get(i);
            setter.invoke(entity, testValue);
        }
    }

    // ==================== Reflection ====================

    @Benchmark
    public void test_Reflection_Getter() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method getter = reflectionGetters.get(i);
            Object result = getter.invoke(entity);
        }
    }

    @Benchmark
    public void test_Reflection_Setter() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method setter = reflectionSetters.get(i);
            setter.invoke(entity, testValue);
        }
    }

    // ==================== ReflectASM ====================

    @Benchmark
    public void test_ReflectASM_Getter() {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = reflectasmGetterIndexes.get(i);
            Object result = reflectasmMethodAccess.invoke(entity, index);
        }
    }

    @Benchmark
    public void test_ReflectASM_Setter() {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = reflectasmSetterIndexes.get(i);
            reflectasmMethodAccess.invoke(entity, index, testValue);
        }
    }

    // ==================== MethodInvokerHelper (varargs) ====================

    @Benchmark
    public void test_MethodInvokerHelper_Getter_Varargs() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodInvokerHelperGetterIndexes.get(i);
            Object result = methodInvokerHelper.invoke(index, entity);
        }
    }

    @Benchmark
    public void test_MethodInvokerHelper_Setter_Varargs() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodInvokerHelperSetterIndexes.get(i);
            methodInvokerHelper.invoke1(index, entity, testValue);
        }
    }

    // ==================== MethodHandleInvoker ====================

    @Benchmark
    public void test_MethodHandleInvoker_Getter() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodHandleInvokerGetterIndexes.get(i);
            Object result = methodHandleInvoker.invoke(index, entity);
        }
    }

    @Benchmark
    public void test_MethodHandleInvoker_Setter() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodHandleInvokerSetterIndexes.get(i);
            methodHandleInvoker.invoke1(index, entity, testValue);
        }
    }

    // ==================== Hutool ReflectUtil ====================

    @Benchmark
    public void test_Hutool_Getter() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method getter = hutoolGetters.get(i);
            Object result = ReflectUtil.invoke(entity, getter);
        }
    }

    @Benchmark
    public void test_Hutool_Setter() throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method setter = hutoolSetters.get(i);
            ReflectUtil.invoke(entity, setter, testValue);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        entity = null;

        // MethodHandle
        methodHandleGetters = null;
        methodHandleSetters = null;

        // Reflection
        reflectionGetters = null;
        reflectionSetters = null;

        // ReflectASM
        reflectasmMethodAccess = null;
        reflectasmGetterIndexes = null;
        reflectasmSetterIndexes = null;

        // MethodInvokerHelper
        methodInvokerHelper = null;
        methodInvokerHelperGetterIndexes = null;
        methodInvokerHelperSetterIndexes = null;

        // MethodHandleInvoker
        methodHandleInvoker = null;
        methodHandleInvokerGetterIndexes = null;
        methodHandleInvokerSetterIndexes = null;

        // Hutool ReflectUtil
        hutoolGetters = null;
        hutoolSetters = null;
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{MethodInvoker250Benchmark.class.getName()});
    }
}
