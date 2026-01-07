package com.github.archtiger.core.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Invoker 生成规则验证
 * <p>
 * 提供基础验证和绑定目标类的验证，统一管理 Field / Method / Constructor 访问规则
 *
 * @author ZIJIDELU
 * @datetime 2026/1/7
 */
public final class InvokerRule {

    private InvokerRule() {
        // 禁止实例化
    }

    // ==================== Field 验证 ====================

    /**
     * 基础验证：只检查访问权限，不考虑 targetClass
     */
    public static boolean canAccessField(Field field) {
        int mods = field.getModifiers();

        if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
            return true;
        }

        // package-private（default）: 可以通过同包访问
        return !Modifier.isPrivate(mods);
    }

    /**
     * 绑定目标类验证
     *
     * @param targetClass 目标类
     * @param field       目标字段
     * @return true 可生成，false 不可生成
     */
    public static boolean canAccessField(Class<?> targetClass, Field field) {
        // 字段必须属于 targetClass 或其父类
        if (!field.getDeclaringClass().isAssignableFrom(targetClass)) {
            return false;
        }
        return canAccessField(field);
    }

    // ==================== Field Setter 验证 ====================

    /**
     * 验证字段是否可以被 Setter 调用
     *
     * @param field 字段
     * @return true 可写
     */
    public static boolean canWriteField(Field field) {
        int mods = field.getModifiers();

        // 不能是 final 字段
        if (Modifier.isFinal(mods)) {
            return false;
        }

        // 权限检查
        return canAccessField(field);
    }

    /**
     * 验证字段是否可以被 targetClass 的 Setter 调用
     *
     * @param targetClass 目标类
     * @param field       字段
     * @return true 可生成 Setter
     */
    public static boolean canWriteField(Class<?> targetClass, Field field) {
        if (!field.getDeclaringClass().isAssignableFrom(targetClass)) {
            return false;
        }
        return canWriteField(field);
    }

    // ==================== Method 验证 ====================

    /**
     * 基础验证：只检查方法访问权限，不考虑 targetClass
     */
    public static boolean canAccessMethod(Method method) {
        int mods = method.getModifiers();

        if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
            return true;
        }

        // package-private（default）
        return !Modifier.isPrivate(mods);
    }

    /**
     * 绑定目标类验证
     *
     * @param targetClass 目标类
     * @param method      目标方法
     * @return true 可生成，false 不可生成
     */
    public static boolean canAccessMethod(Class<?> targetClass, Method method) {
        if (!method.getDeclaringClass().isAssignableFrom(targetClass)) {
            return false;
        }
        return canAccessMethod(method);
    }

    // ==================== Constructor 验证 ====================

    /**
     * 基础验证：只检查构造函数访问权限
     */
    public static boolean canAccessConstructor(Constructor<?> constructor) {
        int mods = constructor.getModifiers();

        if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
            return true;
        }

        // package-private（default）构造器允许
        return !Modifier.isPrivate(mods);
    }

    /**
     * 绑定目标类验证（可选）
     *
     * @param targetClass 目标类
     * @param constructor 构造函数
     * @return true 可生成，false 不可生成
     */
    public static boolean canAccessConstructor(Class<?> targetClass, Constructor<?> constructor) {
        // 构造器必须属于 targetClass
        if (!constructor.getDeclaringClass().equals(targetClass)) {
            return false;
        }
        return canAccessConstructor(constructor);
    }
}
