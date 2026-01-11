package com.github.archtiger.core.access.method;

import com.github.archtiger.core.support.AsmUtil;
import com.github.archtiger.core.support.ByteCodeSizeUtil;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 单参数基本类型方法调用实现类
 * <p>
 * 实现 MethodAccess 的单参数基本类型方法（invokeInt1, invokeLong1 等）
 * 参数直接使用基本类型，无需拆箱，返回值需要装箱
 *
 * @author ZIJIDELU
 * @datetime 2026/1/11
 */
public final class MethodPrimitiveP1InvokerImpl implements Implementation {
    private final Class<?> targetClass;
    private final List<Method> methods;
    private final Class<?> primitiveType;

    public MethodPrimitiveP1InvokerImpl(Class<?> targetClass, List<Method> methods, Class<?> primitiveType) {
        this.targetClass = targetClass;
        this.methods = methods;
        this.primitiveType = primitiveType;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return (mv, ctx, md) -> {
            String owner = Type.getInternalName(targetClass);
            String generatedClassName = implementationTarget.getInstrumentedType().getInternalName();

            // 计算 value 参数的 slot 大小和 castedInstance 的 slot 位置
            int valueSlotSize = ByteCodeSizeUtil.slotSize(primitiveType);
            int castedInstanceSlot = 3 + valueSlotSize; // value 占 slot 3 (或 3-4)，castedInstance 在 value 之后
            int maxLocals = castedInstanceSlot + 1; // castedInstance 占 1 个 slot

            // slot: 0=this, 1=index, 2=instance, 3=value(primitive, 可能占2个slot), castedInstanceSlot=castedInstance
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
            mv.visitVarInsn(Opcodes.ASTORE, castedInstanceSlot);

            mv.visitVarInsn(Opcodes.ILOAD, 1);

            Label defaultLabel = new Label();
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            mv.visitTableSwitchInsn(0, methods.size() - 1, defaultLabel, labels);

            // case 0..N
            for (int i = 0; i < methods.size(); i++) {
                Method method = methods.get(i);
                mv.visitLabel(labels[i]);

                // 获取基本类型的 Frame 描述符
                Object primitiveFrameType = getPrimitiveFrameType(primitiveType);
                // Frame 有 5 个元素: [this, index, instance, arg, castedInstance]
                // long/double 在 frame 中只占一个元素，但实际占用 2 个 slot
                int frameLocals = 5;
                mv.visitFrame(Opcodes.F_NEW, frameLocals, new Object[]{
                        generatedClassName,
                        Opcodes.INTEGER,
                        "java/lang/Object",
                        primitiveFrameType,
                        owner
                }, 0, new Object[0]);

                // 只处理参数类型匹配的方法，其他类型跳转到 default 分支
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length != 1 || paramTypes[0] != primitiveType) {
                    mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    continue;
                }

                // 加载目标对象
                mv.visitVarInsn(Opcodes.ALOAD, castedInstanceSlot);

                // 加载基本类型的 value 参数
                mv.visitVarInsn(AsmUtil.getLoadOpcode(primitiveType), 3);

                // 调用方法
                mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        owner,
                        method.getName(),
                        Type.getMethodDescriptor(method),
                        false
                );

                // 返回值装箱
                if (method.getReturnType() == void.class) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    AsmUtil.boxIfNeeded(mv, method.getReturnType());
                }

                // 返回 Object
                mv.visitInsn(Opcodes.ARETURN);
            }

            // default
            mv.visitLabel(defaultLabel);
            Object primitiveFrameType = getPrimitiveFrameType(primitiveType);
            // Frame 有 5 个元素: [this, index, instance, arg, castedInstance]
            int frameLocals = 5;
            mv.visitFrame(Opcodes.F_NEW, frameLocals, new Object[]{
                    generatedClassName,
                    Opcodes.INTEGER,
                    "java/lang/Object",
                    primitiveFrameType,
                    owner
            }, 0, new Object[0]);

            AsmUtil.throwIAEForMethod(mv);

            // 计算最大栈深度
            int maxStack = 0;
            for (Method method : methods) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1 && paramTypes[0] == primitiveType) {
                    boolean returnsVoid = method.getReturnType() == void.class;
                    int methodStack = 1 + ByteCodeSizeUtil.slotSize(primitiveType); // target + param
                    if (!returnsVoid && method.getReturnType().isPrimitive()) {
                        methodStack += 1; // 返回值装箱临时栈
                    }
                    // 实际上，对于 Object 返回值，也需要装箱
                    if (!returnsVoid && !method.getReturnType().isPrimitive()) {
                        // Object 类型不需要额外栈空间
                    }
                    
                    if (methodStack > maxStack) {
                        maxStack = methodStack;
                    }
                }
            }

            if (maxStack < 4) {
                maxStack = 4;
            }

            return new ByteCodeAppender.Size(maxStack, maxLocals);
        };
    }

    /**
     * 获取基本类型的 Frame 描述符
     */
    private static Object getPrimitiveFrameType(Class<?> type) {
        if (type == long.class) {
            return Opcodes.LONG;
        } else if (type == float.class) {
            return Opcodes.FLOAT;
        } else if (type == double.class) {
            return Opcodes.DOUBLE;
        } else {
            // byte, short, int, char, boolean 都使用 INTEGER
            return Opcodes.INTEGER;
        }
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
