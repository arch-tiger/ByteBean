package com.github.archtiger.core.access.field;

import com.github.archtiger.core.model.FieldAccessInfo;
import com.github.archtiger.definition.invoker.FieldAccess;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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
public final class FieldAccessGenerator {
    private static final TypeCache<String> TYPE_CACHE = new TypeCache.WithInlineExpunction<>(TypeCache.Sort.WEAK);

    private FieldAccessGenerator() {
    }

    /**
     * 为目标类生成 FieldAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 FieldAccess 实现类
     */
    public static FieldAccessInfo generate(Class<?> targetClass) {
        // 步骤1: 收集目标类的所有非静态字段
        List<Field> fields = new ArrayList<>();
        Field[] declaredFields = targetClass.getDeclaredFields();
        if (declaredFields.length == 0) {
            return FieldAccessInfo.fail();
        }

        for (Field f : declaredFields) {
            // 跳过静态字段，因为字段访问器是针对实例字段的
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isPrivate(f.getModifiers())) continue;
            fields.add(f);
        }

        // 检查字段列表是否为空
        // 如果字段列表为空，无法生成有效的 FieldAccess（tableswitch 要求 min <= max）
        if (fields.isEmpty()) {
            return FieldAccessInfo.fail();
        }

        // 步骤1.1: 对字段进行排序
        // 排序规则: 先按字段名排序，再按字段类型描述符排序
        // 例如: int a, long b, int c 会被排序为: a, c, b
        fields.sort(
                Comparator
                        .comparing(Field::getName)
                        .thenComparing(f -> Type.getDescriptor(f.getType()))
        );
        // 步骤2: 构造生成类的全限定名
        // 例如: com.github.archtiger.extensions.User$$FieldAccess
        // $$ 是生成的类常用的命名约定，表示这是生成的辅助类
        String name = targetClass.getName() + "$$FieldAccess";

        Class<?> invokerClass = TYPE_CACHE.findOrInsert(targetClass.getClassLoader(), name, () ->
                // 步骤3: 使用 ByteBuddy 动态生成类
                new ByteBuddy()
                        // 继承 Object 类
                        .subclass(Object.class)
                        // 实现 FieldAccess 接口，定义 get(int, Object) 和 set(int, Object, Object) 方法
                        .implement(FieldAccess.class)
                        // 设置生成类的名称
                        .name(name)
                        // 定义 get 方法: Object get(int index, Object instance)
                        // PUBLIC: 方法访问权限为 public
                        // Object.class: 返回值类型
                        // int.class, Object.class: 参数类型（索引和目标对象）
                        .defineMethod("get", Object.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        // 使用 GetterImpl 作为方法实现的字节码生成器
                        .intercept(new FieldGetterImpl(targetClass, fields))
                        // 定义 set 方法: void set(int index, Object instance, Object value)
                        .defineMethod("set", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, Object.class)
                        // 使用 SetterImpl 作为方法实现的字节码生成器
                        .intercept(new FieldSetterImpl(targetClass, fields))
                        // 基本类型 getter 方法
                        .defineMethod("getByte", byte.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, byte.class))
                        .defineMethod("getShort", short.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, short.class))
                        .defineMethod("getInt", int.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, int.class))
                        .defineMethod("getLong", long.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, long.class))
                        .defineMethod("getFloat", float.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, float.class))
                        .defineMethod("getDouble", double.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, double.class))
                        .defineMethod("getBoolean", boolean.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, boolean.class))
                        .defineMethod("getChar", char.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class)
                        .intercept(new PrimitiveFieldGetterImpl(targetClass, fields, char.class))
                        // 基本类型 setter 方法
                        .defineMethod("setByte", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, byte.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, byte.class))
                        .defineMethod("setShort", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, short.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, short.class))
                        .defineMethod("setInt", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, int.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, int.class))
                        .defineMethod("setLong", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, long.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, long.class))
                        .defineMethod("setFloat", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, float.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, float.class))
                        .defineMethod("setDouble", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, double.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, double.class))
                        .defineMethod("setBoolean", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, boolean.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, boolean.class))
                        .defineMethod("setChar", void.class, Visibility.PUBLIC)
                        .withParameters(int.class, Object.class, char.class)
                        .intercept(new PrimitiveFieldSetterImpl(targetClass, fields, char.class))
                        // 生成字节码
                        .make()
                        .load(targetClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                        .getLoaded()
        );

        return FieldAccessInfo.success(
                (Class<? extends FieldAccess>) invokerClass,
                Collections.unmodifiableList(fields)
        );
    }


}
