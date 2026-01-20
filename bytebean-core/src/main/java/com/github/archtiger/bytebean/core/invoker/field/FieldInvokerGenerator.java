package com.github.archtiger.bytebean.core.invoker.field;

import cn.hutool.core.map.reference.WeakKeyValueConcurrentMap;
import com.github.archtiger.bytebean.api.field.FieldInvoker;
import com.github.archtiger.bytebean.core.model.FieldInvokerResult;
import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;
import com.github.archtiger.bytebean.core.support.NameUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.ClassWriter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 字段访问生成器
 * <p>
 * 为目标类生成一个能够通过索引访问所有字段的辅助类。
 * 使用字节码的 tableswitch 指令实现高效的字段访问，避免反射开销。
 * </p>
 *
 * @author archtiger
 * @datetime 2026/1/6
 */
public final class FieldInvokerGenerator {
    private static final Map<Class<?>, FieldInvokerResult> CACHE = new WeakKeyValueConcurrentMap<>();

    private FieldInvokerGenerator() {
    }

    /**
     * 为目标类生成 FieldAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 FieldAccess 实现类
     */
    public static FieldInvokerResult generate(Class<?> targetClass) {
        return CACHE.computeIfAbsent(targetClass, k -> {

            // 步骤1: 收集目标类的所有非静态字段
            List<Field> fields = ByteBeanReflectUtil.getFields(targetClass);
            if (fields.isEmpty()) {
                return FieldInvokerResult.fail();
            }

            // 步骤2: 构造生成类的全限定名
            String invokerName = NameUtil.calcInvokerName(targetClass, FieldInvoker.class);

            // 步骤3: 使用 ByteBuddy 动态生成类
            final Class<? extends FieldInvoker> invokerClass = new ByteBuddy()
                    .subclass(FieldInvoker.class)
                    .modifiers(Visibility.PUBLIC, TypeManifestation.FINAL)
                    // 设置生成类的名称
                    .name(invokerName)
                    // 定义 get 方法: Object get(int index, Object instance)
                    // PUBLIC: 方法访问权限为 public
                    // Object.class: 返回值类型
                    // int.class, Object.class: 参数类型（索引和目标对象）
                    .defineMethod("get", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    // 使用 GetterImpl 作为方法实现的字节码生成器
                    .intercept(new FieldGetterByteCode(targetClass, fields))
                    // 定义 set 方法: void set(int index, Object instance, Object value)
                    .defineMethod("set", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object.class)
                    // 使用 SetterImpl 作为方法实现的字节码生成器
                    .intercept(new FieldSetterByteCode(targetClass, fields))
                    // 基本类型 getter 方法
                    .defineMethod("getByte", byte.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, byte.class))
                    .defineMethod("getShort", short.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, short.class))
                    .defineMethod("getInt", int.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, int.class))
                    .defineMethod("getLong", long.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, long.class))
                    .defineMethod("getFloat", float.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, float.class))
                    .defineMethod("getDouble", double.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, double.class))
                    .defineMethod("getBoolean", boolean.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, boolean.class))
                    .defineMethod("getChar", char.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    .intercept(new PrimitiveFieldGetterByteCode(targetClass, fields, char.class))
                    // 基本类型 setter 方法
                    .defineMethod("setByte", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, byte.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, byte.class))
                    .defineMethod("setShort", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, short.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, short.class))
                    .defineMethod("setInt", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, int.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, int.class))
                    .defineMethod("setLong", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, long.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, long.class))
                    .defineMethod("setFloat", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, float.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, float.class))
                    .defineMethod("setDouble", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, double.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, double.class))
                    .defineMethod("setBoolean", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, boolean.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, boolean.class))
                    .defineMethod("setChar", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, char.class)
                    .intercept(new PrimitiveFieldSetterByteCode(targetClass, fields, char.class))
                    // 自动计算
                    .visit(new AsmVisitorWrapper.ForDeclaredMethods()
                            .writerFlags(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
                    )
                    // 生成字节码
                    .make()
                    .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            return FieldInvokerResult.success(invokerClass, Collections.unmodifiableList(fields));
        });
    }


}
