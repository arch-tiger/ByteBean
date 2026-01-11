package com.github.archtiger.extensions;

import com.github.archtiger.core.support.AsmUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

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
public final class FieldAccessGeneratorBak {

    private FieldAccessGeneratorBak() {}

    /**
     * 为目标类生成 FieldAccess 接口的实现类
     *
     * @param targetClass 目标类
     * @return 生成的 FieldAccess 实现类
     */
    public static Class<? extends FieldAccessBak> generate(Class<?> targetClass) {
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
                    .implement(FieldAccessBak.class)
                    // 设置生成类的名称
                    .name(name)
                    // 定义 get 方法: Object get(int index, Object instance)
                    // PUBLIC: 方法访问权限为 public
                    // Object.class: 返回值类型
                    // int.class, Object.class: 参数类型（索引和目标对象）
                    .defineMethod("get", Object.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class)
                    // 使用 GetterImpl 作为方法实现的字节码生成器
                    .intercept(new GetterImpl(targetClass, fields))
                    // 定义 set 方法: void set(int index, Object instance, Object value)
                    .defineMethod("set", void.class, Visibility.PUBLIC)
                    .withParameters(int.class, Object.class, Object.class)
                    // 使用 SetterImpl 作为方法实现的字节码生成器
                    .intercept(new SetterImpl(targetClass, fields))
                    // 生成字节码
                    .make();

            // 步骤4: 加载生成的类
            // targetClass.getClassLoader(): 使用目标类的类加载器
            // 这样生成的类与目标类在同一类加载器命名空间中，可以访问 protected 字段
            // ClassLoadingStrategy.Default.INJECTION: 使用注入策略加载类，而不是使用 ByteArrayClassLoader
            // 注入策略会直接在目标类加载器中定义类，避免类加载器隔离问题
            return (Class<? extends FieldAccessBak>) unloaded.load(targetClass.getClassLoader(),
                    net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION).getLoaded();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter 方法字节码实现
     * <p>
     * 生成的字节码逻辑:
     * <pre>
     * public Object get(int index, Object instance) {
     *     // 1. 将 instance 强制转换为目标类型
     *     Target target = (Target) instance;
     *
     *     // 2. 根据 index 使用 tableswitch 跳转到对应字段
     *     switch (index) {
     *         case 0: return target.field0;
     *         case 1: return target.field1;
     *         ...
     *         default: throw new IllegalArgumentException();
     *     }
     * }
     * </pre>
     * </p>
     */
    static final class GetterImpl implements Implementation {
        private final Class<?> targetClass;
        private final List<Field> fields;

        GetterImpl(Class<?> targetClass, List<Field> fields) {
            this.targetClass = targetClass;
            this.fields = fields;
        }

        @Override
        public ByteCodeAppender appender(Target implementationTarget) {
            // 返回一个字节码追加器，负责生成方法的字节码指令
            return (mv, ctx, md) -> {
                // 获取目标类的内部名称（ASM 格式，例如: com/github/archtiger/extensions/User）
                String owner = Type.getInternalName(targetClass);
                // 获取生成类的内部名称
                String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

                // ============================================================
                // 步骤1: 将参数中的 instance 强制转换为目标类型
                // ============================================================
                // 方法签名: Object get(int index, Object instance)
                // 局部变量表布局:
                //   slot 0: this (实例方法的隐式参数)
                //   slot 1: int index (第一个显式参数)
                //   slot 2: Object instance (第二个显式参数)
                //   slot 3: Target castedInstance (临时变量，存放转换后的目标对象)

                // 加载 slot 2 的 Object instance 到操作数栈顶
                mv.visitVarInsn(Opcodes.ALOAD, 2);
                // 检查类型转换: 将 Object 检查转换为目标类型
                // CHECKCAST 会验证对象是否可以安全转换为目标类型
                // 如果类型不匹配，抛出 ClassCastException
                mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
                // 将转换后的对象存储到 slot 3（临时变量）
                mv.visitVarInsn(Opcodes.ASTORE, 3);

                // ============================================================
                // 步骤2: 加载索引参数，准备进行 switch 分支选择
                // ============================================================
                // 加载 slot 1 的 int index 到操作数栈顶
                mv.visitVarInsn(Opcodes.ILOAD, 1);

                // 创建 default 分支的标签（当 index 超出范围时跳转到这里）
                Label defaultLabel = new Label();
                // 创建每个 case 分支对应的标签数组
                Label[] labels = new Label[fields.size()];
                for (int i = 0; i < labels.length; i++) labels[i] = new Label();

                // ============================================================
                // 步骤3: 生成 tableswitch 指令
                // ============================================================
                // tableswitch 是 Java 字节码的高效分支指令
                // 它通过一个跳转表实现 O(1) 时间复杂度的分支选择
                // 比一系列 if-else 或 lookupswitch 更快（lookupswitch 是 O(log n)）
                //
                // 参数说明:
                //   min (0): switch 的最小 case 值
                //   max (fields.size() - 1): switch 的最大 case 值
                //   defaultLabel: 当 index 不在 [min, max] 范围内时的默认跳转目标
                //   labels: 每个 case 对应的跳转目标标签数组
                //
                // 工作原理:
                //   JVM 在执行 tableswitch 时，根据 index 的值直接从跳转表中读取目标地址
                //   例如: index = 2，直接读取 labels[2] 对应的地址并跳转
                //
                // 为什么选择 tableswitch 而不是 lookupswitch:
                //   1. tableswitch 使用索引查找，时间复杂度 O(1)
                //   2. lookupswitch 使用二分查找，时间复杂度 O(log n)
                //   3. 字段索引是连续的 0, 1, 2, ...，非常适合 tableswitch
                mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

                // ============================================================
                // 步骤4: 为每个字段生成对应的 case 分支
                // ============================================================
                for (int i = 0; i < fields.size(); i++) {
                    Field f = fields.get(i);
                    // 定义当前 case 的标签位置
                    mv.visitLabel(labels[i]);

                    // ========================================================
                    // 步骤4.1: 插入 StackMapFrame
                    // ========================================================
                    // StackMapFrame 用于描述方法执行过程中特定位置的局部变量表和操作数栈状态
                    // JVM 验证器使用这些信息来验证字节码的正确性
                    //
                    // 为什么需要插入 StackMapFrame:
                    //   1. Java 7+ 的类文件必须包含 StackMapTable 属性
                    //   2. 每个跳转目标（包括 switch 的 case 和 default）都必须有对应的 frame
                    //   3. frame 告诉 JVM 在此位置时的状态，用于类型验证
                    //
                    // StackMapFrame 参数说明:
                    //   Opcodes.F_NEW: 使用新的 stack map 格式（Java 7+）
                    //   4 (locals): 局部变量表中的有效变量数量
                    //   new Object[] { ... }: 每个局部变量的类型描述
                    //   0 (stack): 操作数栈中的元素数量（此处为空栈）
                    //   new Object[0]: 操作数栈中的元素类型（此处为空）
                    mv.visitFrame(Opcodes.F_NEW, 4,
                            new Object[] {
                                    // slot 0: this（生成类的实例）
                                    generatedClassName,
                                    // slot 1: int index（switch 的条件值）
                                    Opcodes.INTEGER,
                                    // slot 2: Object instance（原始参数）
                                    "java/lang/Object",
                                    // slot 3: Target castedInstance（转换后的目标对象）
                                    owner
                            },
                            0, new Object[0]);  // 操作数栈为空

                    // ========================================================
                    // 步骤4.2: 读取字段值
                    // ========================================================
                    // 加载 slot 3 的 castedInstance 到操作数栈顶
                    mv.visitVarInsn(Opcodes.ALOAD, 3);

                    // GETFIELD 指令: 读取对象的实例字段
                    // 工作原理:
                    //   1. 从操作数栈顶弹出对象引用
                    //   2. 根据字段名和描述符定位字段
                    //   3. 读取字段的值并压入操作数栈
                    //
                    // 参数说明:
                    //   owner: 对象的类内部名称（例如: com/github/archtiger/extensions/User）
                    //   f.getName(): 字段名称（例如: "name"）
                    //   Type.getDescriptor(f.getType()): 字段类型描述符
                    //     - int -> I
                    //     - long -> J
                    //     - String -> Ljava/lang/String;
                    //     - boolean -> Z
                    mv.visitFieldInsn(Opcodes.GETFIELD,
                            owner, f.getName(), Type.getDescriptor(f.getType()));

                    // ========================================================
                    // 步骤4.3: 基本类型装箱（Boxing）
                    // ========================================================
                    // 如果字段是基本类型，需要将其装箱为对应的包装类型
                    // 例如: int -> Integer, long -> Long
                    //
                    // 为什么需要装箱:
                    //   方法的返回类型是 Object，而基本类型（int, long 等）不能直接赋值给 Object
                    //   必须通过装箱转换为对应的包装类
                    AsmUtil.boxIfNeeded(mv, f.getType());

                    // ========================================================
                    // 步骤4.4: 返回结果
                    // ========================================================
                    // ARETURN 指令: 从操作数栈顶弹出引用并返回
                    // 对应方法签名的返回类型 Object
                    mv.visitInsn(Opcodes.ARETURN);
                }

                // ============================================================
                // 步骤5: 处理 default 分支（索引越界）
                // ============================================================
                mv.visitLabel(defaultLabel);
                // 插入 StackMapFrame（与 case 分支相同的局部变量表状态）
                mv.visitFrame(Opcodes.F_NEW, 4,
                        new Object[] {
                                generatedClassName,
                                Opcodes.INTEGER,
                                "java/lang/Object",
                                owner
                        },
                        0, new Object[0]);

                // 抛出 IllegalArgumentException 异常
                throwIAE(mv);

                // ============================================================
                // 步骤6: 计算并返回方法的栈和局部变量大小
                // ============================================================
                // Size(maxStack, maxLocals)
                // maxStack: 操作数栈的最大深度（峰值）
                // maxLocals: 局部变量表的最大槽数量
                //
                // 为什么 maxStack = 4:
                //   在 GETFIELD 指令执行后，栈中可能有:
                //   - 对象引用（如果后续操作需要）+ 字段值（可能是 long/double 占2个槽）
                //   实际上根据字段类型，最大深度为 2（long/double）或 1（其他）
                //   但为了安全起见，设置为 4
                //
                // 为什么 maxLocals = 4:
                //   代码中显式使用的局部变量索引:
                //   - slot 0: this
                //   - slot 1: int index
                //   - slot 2: Object instance
                //   - slot 3: Target castedInstance（临时变量）
                //   最大索引是 3，所以需要 4 个槽（0-3）
                return new ByteCodeAppender.Size(4, 4);
            };
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            // 预处理阶段，不修改类型信息，直接返回
            return instrumentedType;
        }
    }

    /**
     * Setter 方法字节码实现
     * <p>
     * 生成的字节码逻辑:
     * <pre>
     * public void set(int index, Object instance, Object value) {
     *     // 1. 将 instance 强制转换为目标类型
     *     Target target = (Target) instance;
     *
     *     // 2. 根据 index 使用 tableswitch 跳转到对应字段
     *     switch (index) {
     *         case 0: target.field0 = (FieldType)value; break;
     *         case 1: target.field1 = (FieldType)value; break;
     *         ...
     *         default: throw new IllegalArgumentException();
     *     }
     * }
     * </pre>
     * </p>
     */
    static final class SetterImpl implements Implementation {
        private final Class<?> targetClass;
        private final List<Field> fields;

        SetterImpl(Class<?> targetClass, List<Field> fields) {
            this.targetClass = targetClass;
            this.fields = fields;
        }

        @Override
        public ByteCodeAppender appender(Target implementationTarget) {
            return (mv, ctx, md) -> {
                // 获取目标类的内部名称
                String owner = Type.getInternalName(targetClass);
                // 获取生成类的内部名称
                String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

                // ============================================================
                // 步骤1: 将 instance 强制转换为目标类型
                // ============================================================
                // 方法签名: void set(int index, Object instance, Object value)
                // 局部变量表布局:
                //   slot 0: this
                //   slot 1: int index
                //   slot 2: Object instance
                //   slot 3: Object value
                //   slot 4: Target castedInstance（临时变量）

                // 加载 slot 2 的 Object instance
                mv.visitVarInsn(Opcodes.ALOAD, 2);
                // 检查类型转换
                mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
                // 存储到 slot 4
                mv.visitVarInsn(Opcodes.ASTORE, 4);

                // ============================================================
                // 步骤2: 加载索引参数
                // ============================================================
                mv.visitVarInsn(Opcodes.ILOAD, 1);

                // ============================================================
                // 步骤3: 生成 tableswitch 指令
                // ============================================================
                Label defaultLabel = new Label();
                Label[] labels = new Label[fields.size()];
                for (int i = 0; i < labels.length; i++) labels[i] = new Label();

                // 生成 tableswitch，原理与 Getter 相同
                mv.visitTableSwitchInsn(0, fields.size() - 1, defaultLabel, labels);

                // ============================================================
                // 步骤4: 为每个字段生成对应的 case 分支
                // ============================================================
                for (int i = 0; i < fields.size(); i++) {
                    Field f = fields.get(i);
                    mv.visitLabel(labels[i]);

                    // ========================================================
                    // 步骤4.1: 插入 StackMapFrame
                    // ========================================================
                    mv.visitFrame(Opcodes.F_NEW, 5,
                            new Object[] {
                                    // slot 0: this
                                    generatedClassName,
                                    // slot 1: int index
                                    Opcodes.INTEGER,
                                    // slot 2: Object instance
                                    "java/lang/Object",
                                    // slot 3: Object value
                                    "java/lang/Object",
                                    // slot 4: Target castedInstance
                                    owner
                            },
                            0, new Object[0]);  // 操作数栈为空

                    // ========================================================
                    // 步骤4.2: 准备字段赋值
                    // ========================================================
                    // 加载 slot 4 的 castedInstance（目标对象引用）
                    mv.visitVarInsn(Opcodes.ALOAD, 4);

                    // 加载 slot 3 的 value（要设置的值）
                    mv.visitVarInsn(Opcodes.ALOAD, 3);

                    // ========================================================
                    // 步骤4.3: 基本类型拆箱（Unboxing）
                    // ========================================================
                    // 如果字段是基本类型，需要将 Object 类型的 value 拆箱为基本类型
                    // 例如: Integer -> int, Long -> long
                    //
                    // 为什么需要拆箱:
                    //   PUTFIELD 指令需要实际的字段类型值
                    //   不能直接将 Object 引引用存储到 int/long 等基本类型字段中
                    AsmUtil.unboxOrCast(mv, f.getType());

                    // ========================================================
                    // 步骤4.4: 写入字段值
                    // ========================================================
                    // PUTFIELD 指令: 设置对象的实例字段
                    // 工作原理:
                    //   1. 从操作数栈顶弹出字段值
                    //   2. 从操作数栈顶弹出对象引用
                    //   3. 根据字段名和描述符定位字段
                    //   4. 将字段值写入对象的字段中
                    //
                    // 参数说明:
                    //   owner: 对象的类内部名称
                    //   f.getName(): 字段名称
                    //   Type.getDescriptor(f.getType()): 字段类型描述符
                    mv.visitFieldInsn(Opcodes.PUTFIELD,
                            owner, f.getName(), Type.getDescriptor(f.getType()));

                    // ========================================================
                    // 步骤4.5: 返回
                    // ========================================================
                    // RETURN 指令: 从 void 方法返回
                    mv.visitInsn(Opcodes.RETURN);
                }

                // ============================================================
                // 步骤5: 处理 default 分支
                // ============================================================
                mv.visitLabel(defaultLabel);
                mv.visitFrame(Opcodes.F_NEW, 5,
                        new Object[] {
                                generatedClassName,
                                Opcodes.INTEGER,
                                "java/lang/Object",
                                "java/lang/Object",
                                owner
                        },
                        0, new Object[0]);

                throwIAE(mv);

                // ============================================================
                // 步骤6: 计算并返回方法的栈和局部变量大小
                // ============================================================
                // maxStack = 5: 操作数栈的最大深度
                //   可能的栈状态: 对象引用 + value 引用（拆箱前）+ 拆箱后的值
                //
                // maxLocals = 5: 局部变量表的最大槽数量
                //   使用了 slot 0-4，所以需要 5 个槽
                return new ByteCodeAppender.Size(5, 5);
            };
        }

        @Override
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }
    }

    /**
     * 生成抛出 IllegalArgumentException 的字节码
     * <p>
     * 生成的字节码等价于:
     * <pre>
     * throw new IllegalArgumentException("Invalid field index: " + index);
     * </pre>
     * </p>
     *
     * @param mv 方法访问器
     */
    static void throwIAE(MethodVisitor mv) {
        // ============================================================
        // 步骤1: 创建 StringBuilder 用于构建异常信息
        // ============================================================
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        // ============================================================
        // 步骤2: 追加错误消息前缀
        // ============================================================
        mv.visitLdcInsn("Invalid field index: ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        // ============================================================
        // 步骤3: 追加索引值
        // ============================================================
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);

        // ============================================================
        // 步骤4: 转换为 String
        // ============================================================
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

        // ============================================================
        // 步骤5: 创建异常对象
        // ============================================================
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(Opcodes.DUP_X1);
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);

        // ============================================================
        // 步骤6: 抛出异常
        // ============================================================
        mv.visitInsn(Opcodes.ATHROW);
    }
}
