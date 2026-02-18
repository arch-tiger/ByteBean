package com.github.archtiger.bytebean.core.utils;

import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

/**
 * 辅助类，用于 ASM 字节码生成时的一些辅助操作
 *
 * @author ZIJIDELU
 * @datetime 2026/1/6 11:12
 */
public class AsmUtil {


    /**
     * 自动拆箱或强制类型转换非primitive 类型
     *
     * @param mv   方法访问器
     * @param type 字段类型
     */
    public static void unboxOrCast(MethodVisitor mv, Class<?> type) {
        if (type.isPrimitive()) {
            unboxIfNeeded(mv, type);
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(type));
        }
    }

    /**
     * 自动装箱 primitive 类型
     *
     * @param mv   方法访问器
     * @param type 字段类型
     */
    public static void boxIfNeeded(MethodVisitor mv, Class<?> type) {
        if (!type.isPrimitive()) {
            return;
        }
        if (type == int.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (type == long.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (type == boolean.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (type == double.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (type == float.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (type == char.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (type == byte.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (type == short.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else {
            throw new UnsupportedOperationException(type.toString());
        }
    }

    /**
     * 自动拆箱 primitive 类型
     *
     * @param mv   方法访问器
     * @param type 字段类型
     */
    public static void unboxIfNeeded(MethodVisitor mv, Class<?> type) {
        if (!type.isPrimitive()) {
            return;
        }
        if (type == int.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else if (type == long.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        } else if (type == boolean.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        } else if (type == double.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        } else if (type == float.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
        } else if (type == char.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
        } else if (type == byte.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
        } else if (type == short.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
        } else {
            throw new UnsupportedOperationException(type.toString());
        }
    }

    /**
     * 根据类型获取对应的加载指令
     * <p>
     * 适用于从局部变量加载基本类型或引用类型值的场景，支持 field、method、constructor、bean 等
     *
     * @param type 类型
     * @return 加载指令 opcode (ILOAD, LLOAD, FLOAD, DLOAD, ALOAD)
     */
    public static int getLoadOpcode(Class<?> type) {
        if (type == long.class) {
            return Opcodes.LLOAD;
        } else if (type == float.class) {
            return Opcodes.FLOAD;
        } else if (type == double.class) {
            return Opcodes.DLOAD;
        } else if (type.isPrimitive()) {
            // byte, short, int, char, boolean 都使用 ILOAD
            return Opcodes.ILOAD;
        } else {
            // 引用类型使用 ALOAD
            return Opcodes.ALOAD;
        }
    }

    /**
     * 根据类型获取对应的返回指令
     * <p>
     * 适用于方法返回基本类型或引用类型值的场景，支持 field、method、constructor、bean 等
     *
     * @param type 类型
     * @return 返回指令 opcode (IRETURN, LRETURN, FRETURN, DRETURN, RETURN, ARETURN)
     */
    public static int getReturnOpcode(Class<?> type) {
        if (type == void.class) {
            return Opcodes.RETURN;
        } else if (type == long.class) {
            return Opcodes.LRETURN;
        } else if (type == float.class) {
            return Opcodes.FRETURN;
        } else if (type == double.class) {
            return Opcodes.DRETURN;
        } else if (type.isPrimitive()) {
            // byte, short, int, char, boolean 都使用 IRETURN
            return Opcodes.IRETURN;
        } else {
            // 引用类型使用 ARETURN
            return Opcodes.ARETURN;
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
    public static void throwIAEForField(MethodVisitor mv) {
        throwIAE(mv, "Invalid field index: ");
    }

    /**
     * 生成抛出 IllegalArgumentException 的字节码
     *
     * @param mv 方法访问器
     */
    public static void throwIAEForMethod(MethodVisitor mv) {
        throwIAE(mv, "Invalid method index: ");
    }

    /**
     * 生成抛出 IllegalArgumentException 的字节码
     *
     * @param mv 方法访问器
     */
    public static void throwIAEForConstructor(MethodVisitor mv) {
        throwIAE(mv, "Invalid constructor index: ");
    }

    /**
     * 生成抛出 IllegalArgumentException 的字节码
     * <p>
     * 生成的字节码等价于:
     * <pre>
     * throw new IllegalArgumentException(messagePrefix + index);
     * </pre>
     * </p>
     *
     * @param mv            方法访问器
     * @param messagePrefix 错误消息前缀
     */
    public static void throwIAE(MethodVisitor mv, String messagePrefix) {
        // ============================================================
        // 步骤1: 创建 StringBuilder 用于构建异常信息
        // ============================================================
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        // ============================================================
        // 步骤2: 追加错误消息前缀
        // ============================================================
        mv.visitLdcInsn(messagePrefix);
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
