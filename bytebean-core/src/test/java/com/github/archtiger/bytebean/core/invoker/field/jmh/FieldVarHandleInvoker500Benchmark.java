package com.github.archtiger.bytebean.core.invoker.field.jmh;

import cn.hutool.core.util.ReflectUtil;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.github.archtiger.bytebean.core.invoker.entity.Field500Entity;
import com.github.archtiger.bytebean.core.invoker.field.FieldInvokerHelper;
import com.github.archtiger.bytebean.core.invoker.field.FieldVarHandleInvoker;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 字段访问性能基准测试 - 500字段实体
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
public class FieldVarHandleInvoker500Benchmark {

    private Field500Entity entity;
    private Field standardReflectionField;
    private FieldAccess reflectasmAccess;
    private int reflectasmFieldIndex;
    private int reflectasmFirstFieldIndex;
    private int reflectasmMiddleFieldIndex;
    private int reflectasmLastFieldIndex;

    private FieldVarHandleInvoker varHandleInvoker;
    private int varHandleFieldIndex;
    private int varHandleFirstFieldIndex;
    private int varHandleMiddleFieldIndex;
    private int varHandleLastFieldIndex;

    private FieldInvokerHelper fieldInvokerHelper;
    private int fieldInvokerFieldIndex;
    private int fieldInvokerFirstFieldIndex;
    private int fieldInvokerMiddleFieldIndex;
    private int fieldInvokerLastFieldIndex;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        entity = new Field500Entity();

        standardReflectionField = Field500Entity.class.getDeclaredField("field250");
        standardReflectionField.setAccessible(true);

        reflectasmAccess = FieldAccess.get(Field500Entity.class);
        reflectasmFieldIndex = reflectasmAccess.getIndex("field250");
        reflectasmFirstFieldIndex = reflectasmAccess.getIndex("field1");
        reflectasmMiddleFieldIndex = reflectasmAccess.getIndex("field250");
        reflectasmLastFieldIndex = reflectasmAccess.getIndex("field500");

        varHandleInvoker = FieldVarHandleInvoker.of(Field500Entity.class);
        fieldInvokerHelper = FieldInvokerHelper.of(Field500Entity.class);
        fieldInvokerFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field250");
        fieldInvokerFirstFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field1");
        fieldInvokerMiddleFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field250");
        fieldInvokerLastFieldIndex = fieldInvokerHelper.getFieldGetterIndex("field500");
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
        ReflectUtil.getFieldValue(entity, "field250");
        ReflectUtil.setFieldValue(entity, "field250", 1);
        ReflectUtil.getFieldValue(entity, "field250");
        ReflectUtil.setFieldValue(entity, "field250", 2);
        ReflectUtil.getFieldValue(entity, "field250");
        ReflectUtil.setFieldValue(entity, "field250", 3);
        ReflectUtil.getFieldValue(entity, "field250");
        ReflectUtil.setFieldValue(entity, "field250", 4);
        ReflectUtil.getFieldValue(entity, "field250");
        ReflectUtil.setFieldValue(entity, "field250", 5);
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
        return ReflectUtil.getFieldValue(entity, "field250");
    }

    @Benchmark
    public void reflectUtilSet() {
        ReflectUtil.setFieldValue(entity, "field250", 999);
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
        ReflectUtil.setFieldValue(entity, "field250", 100);
        Object result = ReflectUtil.getFieldValue(entity, "field250");
        ReflectUtil.setFieldValue(entity, "field250", 200);
        return ReflectUtil.getFieldValue(entity, "field250");
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{FieldVarHandleInvoker500Benchmark.class.getName()});
    }
}
