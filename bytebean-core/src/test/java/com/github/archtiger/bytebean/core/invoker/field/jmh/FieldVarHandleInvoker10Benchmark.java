package com.github.archtiger.bytebean.core.invoker.field.jmh;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.github.archtiger.bytebean.core.invoker.entity.Field10Entity;
import com.github.archtiger.bytebean.core.invoker.field.FieldInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.field.FieldVarHandleInvoker;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 字段访问性能基准测试 - 10字段实体
 * <p>
 * 对比五种字段访问方式的性能：
 * 1. Hutool ReflectUtil 反射工具
 * 2. FieldVarHandleInvoker (Java 9+)
 * 3. 标准 Java 反射
 * 4. ReflectASM (字节码生成)
 * 5. FieldInvokerHelper (封装了 FieldVarHandleInvoker)
 *
 * @author ZIJIDELU
 * @datetime 2026/1/21
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@State(Scope.Benchmark)
public class FieldVarHandleInvoker10Benchmark {

    /**
     * 测试实体
     */
    private Field10Entity entity;

    /**
     * 标准反射方式：使用 Field 对象
     */
    private Field standardReflectionField;

    /**
     * ReflectASM 方式：使用 FieldAccess
     */
    private FieldAccess reflectasmAccess;
    private int reflectasmFieldIndex;
    private int reflectasmFirstFieldIndex;
    private int reflectasmMiddleFieldIndex;
    private int reflectasmLastFieldIndex;

    /**
     * VarHandle 方式：使用 FieldVarHandleInvoker
     */
    private FieldVarHandleInvoker varHandleInvoker;
    private int varHandleFieldIndex;
    private int varHandleFirstFieldIndex;
    private int varHandleMiddleFieldIndex;
    private int varHandleLastFieldIndex;

    /**
     * FieldInvokerHelper 方式：使用 FieldInvokerHelper
     */
    private FieldInvokerHelper fieldInvokerHelper;
    private int fieldInvokerFieldIndex;
    private int fieldInvokerFirstFieldIndex;
    private int fieldInvokerMiddleFieldIndex;
    private int fieldInvokerLastFieldIndex;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        entity = new Field10Entity();

        // 初始化标准反射 - 使用中间字段 field5
        standardReflectionField = Field10Entity.class.getDeclaredField("field5");
        standardReflectionField.setAccessible(true);

        // 初始化 ReflectASM
        reflectasmAccess = FieldAccess.get(Field10Entity.class);
        reflectasmFieldIndex = reflectasmAccess.getIndex("field5");
        reflectasmFirstFieldIndex = reflectasmAccess.getIndex("field1");
        reflectasmMiddleFieldIndex = reflectasmAccess.getIndex("field5");
        reflectasmLastFieldIndex = reflectasmAccess.getIndex("field10");

        // 初始化 VarHandle
        varHandleInvoker = FieldVarHandleInvoker.of(Field10Entity.class);
        fieldInvokerHelper = FieldInvokerHelper.of(Field10Entity.class);
        fieldInvokerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field5");
        fieldInvokerFirstFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field1");
        fieldInvokerMiddleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field5");
        fieldInvokerLastFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field10");
        varHandleFieldIndex = fieldInvokerFieldIndex;
        varHandleFirstFieldIndex = fieldInvokerFirstFieldIndex;
        varHandleMiddleFieldIndex = fieldInvokerMiddleFieldIndex;
        varHandleLastFieldIndex = fieldInvokerLastFieldIndex;
    }

    @Benchmark
    public Object fieldVarHandleInvokerGet() {
        return varHandleInvoker.get(varHandleFieldIndex, entity);
    }

    @Benchmark
    public void fieldVarHandleInvokerSet() {
        varHandleInvoker.set(varHandleFieldIndex, entity, 999);
    }

    @Benchmark
    public Object standardReflectionGet() throws IllegalAccessException {
        return standardReflectionField.get(entity);
    }

    @Benchmark
    public void standardReflectionSet() throws IllegalAccessException {
        standardReflectionField.set(entity, 999);
    }

    @Benchmark
    public Object reflectasmGet() {
        return reflectasmAccess.get(entity, reflectasmFieldIndex);
    }

    @Benchmark
    public void reflectasmSet() {
        reflectasmAccess.set(entity, reflectasmFieldIndex, 999);
    }

    @Benchmark
    public Object fieldInvokerHelperGet() {
        return fieldInvokerHelper.get(fieldInvokerFieldIndex, entity);
    }

    @Benchmark
    public void fieldInvokerHelperSet() {
        fieldInvokerHelper.set(fieldInvokerFieldIndex, entity, 999);
    }

    @Benchmark
    public void reflectUtilGetMultipleFields() throws IllegalAccessException {
        ReflectUtil.getFieldValue(entity, "field5");
        ReflectUtil.setFieldValue(entity, "field5", 1);
        ReflectUtil.getFieldValue(entity, "field5");
        ReflectUtil.setFieldValue(entity, "field5", 2);
        ReflectUtil.getFieldValue(entity, "field5");
        ReflectUtil.setFieldValue(entity, "field5", 3);
        ReflectUtil.getFieldValue(entity, "field5");
        ReflectUtil.setFieldValue(entity, "field5", 4);
        ReflectUtil.getFieldValue(entity, "field5");
        ReflectUtil.setFieldValue(entity, "field5", 5);
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

    @Benchmark
    public Object fieldVarHandleInvokerGetFirstField() {
        return varHandleInvoker.get(varHandleFirstFieldIndex, entity);
    }

    @Benchmark
    public Object fieldVarHandleInvokerGetMiddleField() {
        return varHandleInvoker.get(varHandleMiddleFieldIndex, entity);
    }

    @Benchmark
    public Object fieldVarHandleInvokerGetLastField() {
        return varHandleInvoker.get(varHandleLastFieldIndex, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperGetFirstField() {
        return fieldInvokerHelper.get(fieldInvokerFirstFieldIndex, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperGetMiddleField() {
        return fieldInvokerHelper.get(fieldInvokerMiddleFieldIndex, entity);
    }

    @Benchmark
    public Object fieldInvokerHelperGetLastField() {
        return fieldInvokerHelper.get(fieldInvokerLastFieldIndex, entity);
    }

    @Benchmark
    public Object reflectasmGetFirstField() {
        return reflectasmAccess.get(entity, reflectasmFirstFieldIndex);
    }

    @Benchmark
    public Object reflectasmGetMiddleField() {
        return reflectasmAccess.get(entity, reflectasmMiddleFieldIndex);
    }

    @Benchmark
    public Object reflectasmGetLastField() {
        return reflectasmAccess.get(entity, reflectasmLastFieldIndex);
    }

    @Benchmark
    public Object reflectUtilGet() {
        return ReflectUtil.getFieldValue(entity, "field5");
    }

    @Benchmark
    public void reflectUtilSet() {
        ReflectUtil.setFieldValue(entity, "field5", 999);
    }

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
        ReflectUtil.setFieldValue(entity, "field5", 100);
        Object result = ReflectUtil.getFieldValue(entity, "field5");
        ReflectUtil.setFieldValue(entity, "field5", 200);
        return ReflectUtil.getFieldValue(entity, "field5");
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{FieldVarHandleInvoker10Benchmark.class.getName()});
    }
}
