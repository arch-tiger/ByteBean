package com.github.archtiger.core.access.field;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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

    private FieldAccessGenerator() {
    }

    /**
     * 为目标类生成 FieldAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 FieldAccess 实现类
     */
    public static Class<? extends FieldAccess> generate(Class<?> targetClass) {
        try {
            // 步骤1: 收集目标类的所有非静态字段
            List<Field> fields = new ArrayList<>();
            for (Field f : targetClass.getDeclaredFields()) {
                // 跳过静态字段，因为字段访问器是针对实例字段的
                if (Modifier.isStatic(f.getModifiers())) continue;
                fields.add(f);
            }

            // 步骤2: 构造生成类的全限定名
            // 例如: com.github.archtiger.extensions.User$$FieldAccess
            // $$ 是生成的类常用的命名约定，表示这是生成的辅助类
            String name = targetClass.getName() + "$$FieldAccess";

            // 步骤3: 使用 ByteBuddy 动态生成类
            DynamicType.Unloaded<?> unloaded = new ByteBuddy()
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
                    // getInt 方法: int getInt(int index, Object instance)
                    .defineMethod("getInt", int.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    // 使用 PrimitiveFieldGetterImpl 作为方法实现的字节码生成器
                    .intercept(new PrimitiveFieldGetterImpl(targetClass, fields))
                    // setInt 方法: void setInt(int index, Object instance, int value)
                    .defineMethod("setInt", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, int.class)
                    // 使用 PrimitiveFieldSetterImpl 作为方法实现的字节码生成器
                    .intercept(new PrimitiveFieldSetterImpl(targetClass, fields))
                    // 生成字节码
                    .make();

            // 步骤4: 加载生成的类
            // targetClass.getClassLoader(): 使用目标类的类加载器
            // 这样生成的类与目标类在同一类加载器命名空间中，可以访问 protected 字段
            // ClassLoadingStrategy.Default.INJECTION: 使用注入策略加载类，而不是使用 ByteArrayClassLoader
            // 注入策略会直接在目标类加载器中定义类，避免类加载器隔离问题
            return (Class<? extends FieldAccess>) unloaded.load(targetClass.getClassLoader(),
                    net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION).getLoaded();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
