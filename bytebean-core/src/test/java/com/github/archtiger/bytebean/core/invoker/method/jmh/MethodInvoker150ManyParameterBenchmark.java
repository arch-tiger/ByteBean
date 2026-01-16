package com.github.archtiger.bytebean.core.invoker.method.jmh;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.archtiger.bytebean.core.invoker.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.entity.Field150Entity;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.Method;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

/**
 * MethodInvoker性能测试 - 150字段5参数版本
 * 所有方法都测试5参数，对比5种方式的性能
 * 所有字段均为对象类型（Integer）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/16
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class MethodInvoker150ManyParameterBenchmark {

    private static final int TEST_COUNT = 150;

    private Field150Entity entity;

    // ========== MethodHandle ==========
    private MethodHandle methodHandleSetter;

    // ========== Reflection ==========
    private Method reflectionSetter;

    // ========== ReflectASM ==========
    private MethodAccess reflectasmMethodAccess;
    private int reflectasmSetterIndex;

    // ========== MethodInvokerHelper ==========
    private MethodInvokerHelper methodInvokerHelper;
    private int methodInvokerHelperSetterIndex;

    // ========== Hutool ReflectUtil ==========
    private Method hutoolSetter;

    private static final Integer V1 = 1;
    private static final Integer V2 = 2;
    private static final Integer V3 = 3;
    private static final Integer V4 = 4;
    private static final Integer V5 = 5;

    @Setup(Level.Trial)
    public void setup() throws Throwable {
        entity = new Field150Entity();

        // 初始化 MethodHandle
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // 使用5参数方法进行测试
        String methodName = "setFiveFields";

        // 获取反射方法 - 参数类型为 5个 Integer.class
        reflectionSetter = Field150Entity.class.getMethod(methodName,
            Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);

        // 获取 MethodHandle
        methodHandleSetter = lookup.unreflect(reflectionSetter);

        // 获取 ReflectASM 索引 - 参数类型为 5个 Integer.class
        reflectasmMethodAccess = MethodAccess.get(Field150Entity.class);
        reflectasmSetterIndex = reflectasmMethodAccess.getIndex(methodName,
            Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);

        // 获取 MethodInvokerHelper 索引 - 参数类型为 5个 Integer.class
        methodInvokerHelper = MethodInvokerHelper.of(Field150Entity.class);
        methodInvokerHelperSetterIndex = methodInvokerHelper.getMethodIndex(methodName,
            Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);

        // 获取 Hutool ReflectUtil 方法
        hutoolSetter = reflectionSetter;
    }

    // ==================== MethodHandle ====================

    @Benchmark
    public void test_MethodHandle_Setter(Blackhole bh) throws Throwable {
        for (int i = 0; i < TEST_COUNT; i++) {
            methodHandleSetter.invoke(entity, V1, V2, V3, V4, V5);
        }
        bh.consume(entity);
    }

    // ==================== Reflection ====================

    @Benchmark
    public void test_Reflection_Setter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            reflectionSetter.invoke(entity, V1, V2, V3, V4, V5);
        }
        bh.consume(entity);
    }

    // ==================== ReflectASM ====================

    @Benchmark
    public void test_ReflectASM_Setter(Blackhole bh) {
        for (int i = 0; i < TEST_COUNT; i++) {
            reflectasmMethodAccess.invoke(entity, reflectasmSetterIndex, V1, V2, V3, V4, V5);
        }
        bh.consume(entity);
    }

    // ==================== MethodInvokerHelper ====================

    @Benchmark
    public void test_MethodInvokerHelper_Setter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            methodInvokerHelper.invoke(methodInvokerHelperSetterIndex, entity, V1, V2, V3, V4, V5);
        }
        bh.consume(entity);
    }

    // ==================== Hutool ReflectUtil ====================

    @Benchmark
    public void test_Hutool_Setter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            ReflectUtil.invoke(entity, hutoolSetter, V1, V2, V3, V4, V5);
        }
        bh.consume(entity);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        entity = null;

        // MethodHandle
        methodHandleSetter = null;

        // Reflection
        reflectionSetter = null;

        // ReflectASM
        reflectasmMethodAccess = null;

        // MethodInvokerHelper
        methodInvokerHelper = null;

        // Hutool ReflectUtil
        hutoolSetter = null;
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{MethodInvoker150ManyParameterBenchmark.class.getName()});
    }
}
