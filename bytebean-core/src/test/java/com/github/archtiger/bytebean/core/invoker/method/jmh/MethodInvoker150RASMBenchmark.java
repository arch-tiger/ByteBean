package com.github.archtiger.bytebean.core.invoker.method.jmh;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.archtiger.bytebean.core.invoker.method.MethodInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.entity.Field150Entity;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MethodInvoker性能测试 - 150字段版本
 * 只测试 getter 和 setter 调用，对比6种方式的性能
 * 所有字段均为对象类型（Integer）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/15
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MethodInvoker150RASMBenchmark {

    private static final int TEST_COUNT = 10;

    private Field150Entity entity;

    // ========== ReflectASM ==========
    private MethodAccess reflectasmMethodAccess;
    private List<Integer> reflectasmGetterIndexes;
    private List<Integer> reflectasmSetterIndexes;

    // ========== MethodInvokerHelper ==========
    private MethodInvokerHelper methodInvokerHelper;
    private List<Integer> methodInvokerHelperGetterIndexes;
    private List<Integer> methodInvokerHelperSetterIndexes;

    private Integer testValue = 100;

    @Setup(Level.Trial)
    public void setup() throws Throwable {
        entity = new Field150Entity();

        // 初始化 ReflectASM
        reflectasmMethodAccess = MethodAccess.get(Field150Entity.class);
        reflectasmGetterIndexes = new ArrayList<>(TEST_COUNT);
        reflectasmSetterIndexes = new ArrayList<>(TEST_COUNT);

        // 初始化 MethodInvokerHelper
        methodInvokerHelper = MethodInvokerHelper.of(Field150Entity.class);
        methodInvokerHelperGetterIndexes = new ArrayList<>(TEST_COUNT);
        methodInvokerHelperSetterIndexes = new ArrayList<>(TEST_COUNT);

        for (int i = 1; i <= TEST_COUNT; i++) {
            String fieldName = "field" + i;
            String capitalizedName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            String getterName = "get" + capitalizedName;
            String setterName = "set" + capitalizedName;

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

        }
    }


    // ==================== ReflectASM ====================

    @Benchmark
    public void test_ReflectASM_Getter(Blackhole bh) {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = reflectasmGetterIndexes.get(i);
            Object result = reflectasmMethodAccess.invoke(entity, index);
            bh.consume(result);
        }
    }

    @Benchmark
    public void test_ReflectASM_Setter(Blackhole bh) {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = reflectasmSetterIndexes.get(i);
            reflectasmMethodAccess.invoke(entity, index, testValue);
        }
    }

    // ==================== MethodInvokerHelper (varargs) ====================

    @Benchmark
    public void test_MethodInvokerHelper_Getter_Varargs(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodInvokerHelperGetterIndexes.get(i);
            Object result = methodInvokerHelper.invoke(index, entity);
            bh.consume(result);
        }
    }

    @Benchmark
    public void test_MethodInvokerHelper_Setter_Varargs(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodInvokerHelperSetterIndexes.get(i);
            methodInvokerHelper.invoke1(index, entity, testValue);
        }
    }


    @TearDown(Level.Trial)
    public void tearDown() {
        entity = null;

        // ReflectASM
        reflectasmMethodAccess = null;
        reflectasmGetterIndexes = null;
        reflectasmSetterIndexes = null;

        // MethodInvokerHelper
        methodInvokerHelper = null;
        methodInvokerHelperGetterIndexes = null;
        methodInvokerHelperSetterIndexes = null;

    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{MethodInvoker150RASMBenchmark.class.getName()});
    }
}
