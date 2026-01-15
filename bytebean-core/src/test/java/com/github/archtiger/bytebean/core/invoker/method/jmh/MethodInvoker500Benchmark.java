package com.github.archtiger.bytebean.core.invoker.method.jmh;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.archtiger.bytebean.core.invoker.MethodInvokerHelper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.Method;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MethodInvoker性能测试 - 500字段版本
 * 只测试 getter 和 setter 调用，对比6种方式的性能
 * 所有字段均为对象类型（Integer）
 *
 * @author ZIJIDELU
 * @datetime 2026/1/15
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class MethodInvoker500Benchmark {

    private static final int TEST_COUNT = 100;
    
    private Field500Entity entity;
    
    // ========== Direct Call ==========
    
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
    
    // ========== Hutool ReflectUtil ==========
    private List<Method> hutoolGetters;
    private List<Method> hutoolSetters;
    
    private Integer testValue = 100;
    
    @Setup(Level.Trial)
    public void setup() throws Throwable {
        entity = new Field500Entity();
        
        // 初始化 MethodHandle
        methodHandleGetters = new ArrayList<>(TEST_COUNT);
        methodHandleSetters = new ArrayList<>(TEST_COUNT);
        
        // 初始化反射
        reflectionGetters = new ArrayList<>(TEST_COUNT);
        reflectionSetters = new ArrayList<>(TEST_COUNT);
        
        // 初始化 ReflectASM
        reflectasmMethodAccess = MethodAccess.get(Field500Entity.class);
        reflectasmGetterIndexes = new ArrayList<>(TEST_COUNT);
        reflectasmSetterIndexes = new ArrayList<>(TEST_COUNT);
        
        // 初始化 MethodInvokerHelper
        methodInvokerHelper = MethodInvokerHelper.of(Field500Entity.class);
        methodInvokerHelperGetterIndexes = new ArrayList<>(TEST_COUNT);
        methodInvokerHelperSetterIndexes = new ArrayList<>(TEST_COUNT);
        
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
            Method getter = Field500Entity.class.getMethod(getterName);
            Method setter = Field500Entity.class.getMethod(setterName, Integer.class);
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
            
            // 获取 Hutool ReflectUtil 方法
            hutoolGetters.add(getter);
            hutoolSetters.add(setter);
        }
    }
    
    // ==================== Direct Call ====================
    
    @Benchmark
    public void test_DirectCall_Getter(Blackhole bh) {
        // 调用100个不同的getter方法
        Integer result = entity.getField1();
        bh.consume(result);
        result = entity.getField2();
        bh.consume(result);
        result = entity.getField3();
        bh.consume(result);
        result = entity.getField4();
        bh.consume(result);
        result = entity.getField5();
        bh.consume(result);
        result = entity.getField6();
        bh.consume(result);
        result = entity.getField7();
        bh.consume(result);
        result = entity.getField8();
        bh.consume(result);
        result = entity.getField9();
        bh.consume(result);
        result = entity.getField10();
        bh.consume(result);
        result = entity.getField11();
        bh.consume(result);
        result = entity.getField12();
        bh.consume(result);
        result = entity.getField13();
        bh.consume(result);
        result = entity.getField14();
        bh.consume(result);
        result = entity.getField15();
        bh.consume(result);
        result = entity.getField16();
        bh.consume(result);
        result = entity.getField17();
        bh.consume(result);
        result = entity.getField18();
        bh.consume(result);
        result = entity.getField19();
        bh.consume(result);
        result = entity.getField20();
        bh.consume(result);
        result = entity.getField21();
        bh.consume(result);
        result = entity.getField22();
        bh.consume(result);
        result = entity.getField23();
        bh.consume(result);
        result = entity.getField24();
        bh.consume(result);
        result = entity.getField25();
        bh.consume(result);
        result = entity.getField26();
        bh.consume(result);
        result = entity.getField27();
        bh.consume(result);
        result = entity.getField28();
        bh.consume(result);
        result = entity.getField29();
        bh.consume(result);
        result = entity.getField30();
        bh.consume(result);
        result = entity.getField31();
        bh.consume(result);
        result = entity.getField32();
        bh.consume(result);
        result = entity.getField33();
        bh.consume(result);
        result = entity.getField34();
        bh.consume(result);
        result = entity.getField35();
        bh.consume(result);
        result = entity.getField36();
        bh.consume(result);
        result = entity.getField37();
        bh.consume(result);
        result = entity.getField38();
        bh.consume(result);
        result = entity.getField39();
        bh.consume(result);
        result = entity.getField40();
        bh.consume(result);
        result = entity.getField41();
        bh.consume(result);
        result = entity.getField42();
        bh.consume(result);
        result = entity.getField43();
        bh.consume(result);
        result = entity.getField44();
        bh.consume(result);
        result = entity.getField45();
        bh.consume(result);
        result = entity.getField46();
        bh.consume(result);
        result = entity.getField47();
        bh.consume(result);
        result = entity.getField48();
        bh.consume(result);
        result = entity.getField49();
        bh.consume(result);
        result = entity.getField50();
        bh.consume(result);
        result = entity.getField51();
        bh.consume(result);
        result = entity.getField52();
        bh.consume(result);
        result = entity.getField53();
        bh.consume(result);
        result = entity.getField54();
        bh.consume(result);
        result = entity.getField55();
        bh.consume(result);
        result = entity.getField56();
        bh.consume(result);
        result = entity.getField57();
        bh.consume(result);
        result = entity.getField58();
        bh.consume(result);
        result = entity.getField59();
        bh.consume(result);
        result = entity.getField60();
        bh.consume(result);
        result = entity.getField61();
        bh.consume(result);
        result = entity.getField62();
        bh.consume(result);
        result = entity.getField63();
        bh.consume(result);
        result = entity.getField64();
        bh.consume(result);
        result = entity.getField65();
        bh.consume(result);
        result = entity.getField66();
        bh.consume(result);
        result = entity.getField67();
        bh.consume(result);
        result = entity.getField68();
        bh.consume(result);
        result = entity.getField69();
        bh.consume(result);
        result = entity.getField70();
        bh.consume(result);
        result = entity.getField71();
        bh.consume(result);
        result = entity.getField72();
        bh.consume(result);
        result = entity.getField73();
        bh.consume(result);
        result = entity.getField74();
        bh.consume(result);
        result = entity.getField75();
        bh.consume(result);
        result = entity.getField76();
        bh.consume(result);
        result = entity.getField77();
        bh.consume(result);
        result = entity.getField78();
        bh.consume(result);
        result = entity.getField79();
        bh.consume(result);
        result = entity.getField80();
        bh.consume(result);
        result = entity.getField81();
        bh.consume(result);
        result = entity.getField82();
        bh.consume(result);
        result = entity.getField83();
        bh.consume(result);
        result = entity.getField84();
        bh.consume(result);
        result = entity.getField85();
        bh.consume(result);
        result = entity.getField86();
        bh.consume(result);
        result = entity.getField87();
        bh.consume(result);
        result = entity.getField88();
        bh.consume(result);
        result = entity.getField89();
        bh.consume(result);
        result = entity.getField90();
        bh.consume(result);
        result = entity.getField91();
        bh.consume(result);
        result = entity.getField92();
        bh.consume(result);
        result = entity.getField93();
        bh.consume(result);
        result = entity.getField94();
        bh.consume(result);
        result = entity.getField95();
        bh.consume(result);
        result = entity.getField96();
        bh.consume(result);
        result = entity.getField97();
        bh.consume(result);
        result = entity.getField98();
        bh.consume(result);
        result = entity.getField99();
        bh.consume(result);
        result = entity.getField100();
        bh.consume(result);
    }
    
    @Benchmark
    public void test_DirectCall_Setter(Blackhole bh) {
        // 调用100个不同的setter方法
        Integer value = testValue;
        entity.setField1(value);
        entity.setField2(value);
        entity.setField3(value);
        entity.setField4(value);
        entity.setField5(value);
        entity.setField6(value);
        entity.setField7(value);
        entity.setField8(value);
        entity.setField9(value);
        entity.setField10(value);
        entity.setField11(value);
        entity.setField12(value);
        entity.setField13(value);
        entity.setField14(value);
        entity.setField15(value);
        entity.setField16(value);
        entity.setField17(value);
        entity.setField18(value);
        entity.setField19(value);
        entity.setField20(value);
        entity.setField21(value);
        entity.setField22(value);
        entity.setField23(value);
        entity.setField24(value);
        entity.setField25(value);
        entity.setField26(value);
        entity.setField27(value);
        entity.setField28(value);
        entity.setField29(value);
        entity.setField30(value);
        entity.setField31(value);
        entity.setField32(value);
        entity.setField33(value);
        entity.setField34(value);
        entity.setField35(value);
        entity.setField36(value);
        entity.setField37(value);
        entity.setField38(value);
        entity.setField39(value);
        entity.setField40(value);
        entity.setField41(value);
        entity.setField42(value);
        entity.setField43(value);
        entity.setField44(value);
        entity.setField45(value);
        entity.setField46(value);
        entity.setField47(value);
        entity.setField48(value);
        entity.setField49(value);
        entity.setField50(value);
        entity.setField51(value);
        entity.setField52(value);
        entity.setField53(value);
        entity.setField54(value);
        entity.setField55(value);
        entity.setField56(value);
        entity.setField57(value);
        entity.setField58(value);
        entity.setField59(value);
        entity.setField60(value);
        entity.setField61(value);
        entity.setField62(value);
        entity.setField63(value);
        entity.setField64(value);
        entity.setField65(value);
        entity.setField66(value);
        entity.setField67(value);
        entity.setField68(value);
        entity.setField69(value);
        entity.setField70(value);
        entity.setField71(value);
        entity.setField72(value);
        entity.setField73(value);
        entity.setField74(value);
        entity.setField75(value);
        entity.setField76(value);
        entity.setField77(value);
        entity.setField78(value);
        entity.setField79(value);
        entity.setField80(value);
        entity.setField81(value);
        entity.setField82(value);
        entity.setField83(value);
        entity.setField84(value);
        entity.setField85(value);
        entity.setField86(value);
        entity.setField87(value);
        entity.setField88(value);
        entity.setField89(value);
        entity.setField90(value);
        entity.setField91(value);
        entity.setField92(value);
        entity.setField93(value);
        entity.setField94(value);
        entity.setField95(value);
        entity.setField96(value);
        entity.setField97(value);
        entity.setField98(value);
        entity.setField99(value);
        entity.setField100(value);
    }
    
    // ==================== MethodHandle ====================
    
    @Benchmark
    public void test_MethodHandle_Getter(Blackhole bh) throws Throwable {
        for (int i = 0; i < TEST_COUNT; i++) {
            MethodHandle getter = methodHandleGetters.get(i);
            Object result = getter.invoke((Object) entity);
            bh.consume(result);
        }
    }
    
    @Benchmark
    public void test_MethodHandle_Setter(Blackhole bh) throws Throwable {
        for (int i = 0; i < TEST_COUNT; i++) {
            MethodHandle setter = methodHandleSetters.get(i);
            setter.invoke(entity, testValue);
        }
    }
    
    // ==================== Reflection ====================
    
    @Benchmark
    public void test_Reflection_Getter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method getter = reflectionGetters.get(i);
            Object result = getter.invoke(entity);
            bh.consume(result);
        }
    }
    
    @Benchmark
    public void test_Reflection_Setter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method setter = reflectionSetters.get(i);
            setter.invoke(entity, testValue);
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
            Object result = methodInvokerHelper.getMethodInvoker().invoke(index, entity);
            bh.consume(result);
        }
    }
    
    @Benchmark
    public void test_MethodInvokerHelper_Setter_Varargs(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            int index = methodInvokerHelperSetterIndexes.get(i);
            methodInvokerHelper.getMethodInvoker().invoke(index, entity, testValue);
        }
    }
    
    // ==================== Hutool ReflectUtil ====================
    
    @Benchmark
    public void test_Hutool_Getter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method getter = hutoolGetters.get(i);
            Object result = ReflectUtil.invoke(entity, getter);
            bh.consume(result);
        }
    }
    
    @Benchmark
    public void test_Hutool_Setter(Blackhole bh) throws Exception {
        for (int i = 0; i < TEST_COUNT; i++) {
            Method setter = hutoolSetters.get(i);
            ReflectUtil.invoke(entity, setter, testValue);
        }
    }
    
    @TearDown(Level.Trial)
    public void tearDown() {
        entity = null;
        
        // Direct Call - 无需清理
        
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
        
        // Hutool ReflectUtil
        hutoolGetters = null;
        hutoolSetters = null;
    }
    
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{MethodInvoker500Benchmark.class.getName()});
    }
}
