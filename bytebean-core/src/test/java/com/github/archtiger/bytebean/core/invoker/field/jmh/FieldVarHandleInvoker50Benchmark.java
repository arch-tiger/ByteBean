package com.github.archtiger.bytebean.core.invoker.field.jmh;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.github.archtiger.bytebean.core.invoker.entity.JMH50PublicFieldTestEntity;
import com.github.archtiger.bytebean.core.invoker.field.FieldInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.field.FieldVarHandleInvoker;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 字段访问性能基准测试
 * <p>
 * 对比五种字段访问方式的性能：
 * 1. Hutool ReflectUtil 反射工具
 * 2. FieldVarHandleInvoker (Java 9+)
 * 3. 标准 Java 反射
 * 4. ReflectASM (字节码生成)
 * 5. FieldInvokerHelper (封装了 FieldVarHandleInvoker)
 *
 * @author ZIJIDELU
 * @datetime 2026/1/20
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@State(Scope.Benchmark)
public class FieldVarHandleInvoker50Benchmark {

    /**
     * 测试实体
     */
    private JMH50PublicFieldTestEntity entity;

    /**
     * 标准反射方式：使用 Field 对象
     */
    private Field standardReflectionField;

    /**
     * ReflectASM 方式：使用 FieldAccess
     */
    private FieldAccess reflectasmAccess;
    private int reflectasmFieldIndex;

    /**
     * VarHandle 方式：使用 FieldVarHandleInvoker
     */
    private FieldVarHandleInvoker varHandleInvoker;
    private int varHandleFieldIndex;

    /**
     * FieldInvokerHelper 方式：使用 FieldInvokerHelper
     */
    private FieldInvokerHelper fieldInvokerHelper;
    private int fieldInvokerFieldIndex;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        entity = new JMH50PublicFieldTestEntity();

        // 初始化标准反射
        standardReflectionField = JMH50PublicFieldTestEntity.class.getDeclaredField("field25");
        standardReflectionField.setAccessible(true);

        // 初始化 ReflectASM
        reflectasmAccess = FieldAccess.get(JMH50PublicFieldTestEntity.class);
        reflectasmFieldIndex = reflectasmAccess.getIndex("field25");

        // 初始化 VarHandle
        varHandleInvoker = FieldVarHandleInvoker.of(JMH50PublicFieldTestEntity.class);
        fieldInvokerHelper = FieldInvokerHelper.of(JMH50PublicFieldTestEntity.class);
        fieldInvokerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field25");
        varHandleFieldIndex = fieldInvokerFieldIndex; // 同一个索引
    }

    /**
     * FieldVarHandleInvoker - 读取字段
     */
    @Benchmark
    public Object fieldVarHandleInvokerGet() {
        return varHandleInvoker.get(varHandleFieldIndex, entity);
    }

    /**
     * FieldVarHandleInvoker - 设置字段
     */
    @Benchmark
    public void fieldVarHandleInvokerSet() {
        varHandleInvoker.set(varHandleFieldIndex, entity, 999);
    }

    /**
     * 标准反射 - 读取字段
     */
    @Benchmark
    public Object standardReflectionGet() throws IllegalAccessException {
        return standardReflectionField.get(entity);
    }

    /**
     * 标准反射 - 设置字段
     */
    @Benchmark
    public void standardReflectionSet() throws IllegalAccessException {
        standardReflectionField.set(entity, 999);
    }

    /**
     * ReflectASM - 读取字段
     */
    @Benchmark
    public Object reflectasmGet() {
        return reflectasmAccess.get(entity, reflectasmFieldIndex);
    }

    /**
     * ReflectASM - 设置字段
     */
    @Benchmark
    public void reflectasmSet() {
        reflectasmAccess.set(entity, reflectasmFieldIndex, 999);
    }

    /**
     * FieldInvokerHelper (包含 VarHandle) - 读取字段
     */
    @Benchmark
    public Object fieldInvokerHelperGet() {
        return fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
    }

    /**
     * FieldInvokerHelper (包含 VarHandle) - 设置字段
     */
    @Benchmark
    public void fieldInvokerHelperSet() {
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 999);
    }

    /**
     * 多字段访问基准测试 - 读取 10 个字段
     * 模拟实际使用场景：访问多个字段
     */
    @Benchmark
    public void reflectUtilGetMultipleFields() throws IllegalAccessException {
        ReflectUtil.getFieldValue(entity, "field25");
        ReflectUtil.setFieldValue(entity, "field25", 1);
        ReflectUtil.getFieldValue(entity, "field25");
        ReflectUtil.setFieldValue(entity, "field25", 2);
        ReflectUtil.getFieldValue(entity, "field25");
        ReflectUtil.setFieldValue(entity, "field25", 3);
        ReflectUtil.getFieldValue(entity, "field25");
        ReflectUtil.setFieldValue(entity, "field25", 4);
        ReflectUtil.getFieldValue(entity, "field25");
        ReflectUtil.setFieldValue(entity, "field25", 5);
    }

    @Benchmark
    public void fieldVarHandleInvokerGetMultipleFields() {
        varHandleInvoker.get(varHandleFieldIndex, entity);
        varHandleInvoker.set(varHandleFieldIndex, entity, 1);
        varHandleInvoker.get(varHandleFieldIndex, entity);
        varHandleInvoker.set(varHandleFieldIndex, entity, 2);
        varHandleInvoker.get(varHandleFieldIndex, entity);
        varHandleInvoker.set(varHandleFieldIndex, entity, 3);
        varHandleInvoker.get(varHandleFieldIndex, entity);
        varHandleInvoker.set(varHandleFieldIndex, entity, 4);
        varHandleInvoker.get(varHandleFieldIndex, entity);
        varHandleInvoker.set(varHandleFieldIndex, entity, 5);
    }

    @Benchmark
    public void fieldInvokerHelperGetMultipleFields() {
        fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 1);
        fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 2);
        fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 3);
        fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 4);
        fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 5);
    }

    @Benchmark
    public void standardReflectionGetMultipleFields() throws IllegalAccessException {
        standardReflectionField.get(entity);
        standardReflectionField.set(entity, 1);
        standardReflectionField.get(entity);
        standardReflectionField.set(entity, 2);
        standardReflectionField.get(entity);
        standardReflectionField.set(entity, 3);
        standardReflectionField.get(entity);
        standardReflectionField.set(entity, 4);
        standardReflectionField.get(entity);
        standardReflectionField.set(entity, 5);
    }

    @Benchmark
    public void reflectasmGetMultipleFields() {
        reflectasmAccess.get(entity, reflectasmFieldIndex);
        reflectasmAccess.set(entity, reflectasmFieldIndex, 1);
        reflectasmAccess.get(entity, reflectasmFieldIndex);
        reflectasmAccess.set(entity, reflectasmFieldIndex, 2);
        reflectasmAccess.get(entity, reflectasmFieldIndex);
        reflectasmAccess.set(entity, reflectasmFieldIndex, 3);
        reflectasmAccess.get(entity, reflectasmFieldIndex);
        reflectasmAccess.set(entity, reflectasmFieldIndex, 4);
        reflectasmAccess.get(entity, reflectasmFieldIndex);
        reflectasmAccess.set(entity, reflectasmFieldIndex, 5);
    }

    /**
     * 不同字段位置的性能测试
     * 测试访问第一个、中间和最后一个字段的性能差异
     */
    @Benchmark
    public Object fieldVarHandleInvokerGetFirstField() {
        return varHandleInvoker.get(0, entity);
    }

    @Benchmark
    public Object fieldVarHandleInvokerGetMiddleField() {
        return varHandleInvoker.get(24, entity);
    }

    @Benchmark
    public Object fieldVarHandleInvokerGetLastField() {
        return varHandleInvoker.get(49, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperGetFirstField() {
        return fieldInvokerHelper.get(0, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperGetMiddleField() {
        return fieldInvokerHelper.get(24, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperGetLastField() {
        return fieldInvokerHelper.get(49, entity);
    }

    @Benchmark
    public Object reflectasmGetFirstField() {
        return reflectasmAccess.get(entity, 0);
    }

    @Benchmark
    public Object reflectasmGetMiddleField() {
        return reflectasmAccess.get(entity, reflectasmAccess.getIndex("field25"));
    }

    @Benchmark
    public Object reflectasmGetLastField() {
        return reflectasmAccess.get(entity, reflectasmAccess.getIndex("field50"));
    }

    /**
     * 使用 Hutool 的 ReflectUtil 工具类
     */
    @Benchmark
    public Object reflectUtilGet() {
        return ReflectUtil.getFieldValue(entity, "field25");
    }

    @Benchmark
    public void reflectUtilSet() {
        ReflectUtil.setFieldValue(entity, "field25", 999);
    }

    /**
     * 性能对比测试 - 所有方法
     * 使用相同的操作模式进行对比
     */
    @Benchmark
    public Object fieldVarHandleInvokerMixedOperations() {
        varHandleInvoker.set(varHandleFieldIndex, entity, 100);
        Object result = varHandleInvoker.get(varHandleFieldIndex, entity);
        varHandleInvoker.set(varHandleFieldIndex, entity, 200);
        return varHandleInvoker.get(varHandleFieldIndex, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperMixedOperations() {
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 100);
        Object result = fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 200);
        return fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
    }

    @Benchmark
    public Object reflectasmMixedOperations() {
        reflectasmAccess.set(entity, reflectasmFieldIndex, 100);
        Object result = reflectasmAccess.get(entity, reflectasmFieldIndex);
        reflectasmAccess.set(entity, reflectasmFieldIndex, 200);
        return reflectasmAccess.get(entity, reflectasmFieldIndex);
    }

    @Benchmark
    public Object standardReflectionMixedOperations() throws IllegalAccessException {
        standardReflectionField.set(entity, 100);
        Object result = standardReflectionField.get(entity);
        standardReflectionField.set(entity, 200);
        return standardReflectionField.get(entity);
    }

    @Benchmark
    public Object reflectUtilMixedOperations() {
        ReflectUtil.setFieldValue(entity, "field25", 100);
        Object result = ReflectUtil.getFieldValue(entity, "field25");
        ReflectUtil.setFieldValue(entity, "field25", 200);
        return ReflectUtil.getFieldValue(entity, "field25");
    }

    /**
     * 主方法 - 直接运行 JMH 基准测试
     */
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{FieldVarHandleInvoker50Benchmark.class.getName()});

    }
}
