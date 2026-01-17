package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 基于MethodHandle的方法调用器（高性能优化版本）
 * <p>
 * 优化策略：
 * 1. 使用数组索引访问代替 try-catch，避免异常处理开销
 * 2. 为单参数方法创建专用 MethodHandle，消除 invokeWithArguments 的包装开销
 * 3. 使用 bindTo 预绑定参数，消除运行时的虚调用开销
 * 4. 复用 EMPTY_ARGS 数组，减少内存分配
 *
 * @author ArchTiger
 * @date 2026/1/16 19:46
 */
public class MethodHandleInvoker extends MethodInvoker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    // 多参数方法的 MethodHandles
    private final MethodHandle[] methodHandles;


    private MethodHandleInvoker(MethodHandle[] methodHandles) {
        this.methodHandles = methodHandles;
    }

    public static MethodHandleInvoker of(Class<?> targetClass) {
        try {
            List<Method> methods = ByteBeanReflectUtil.getMethods(targetClass);
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, LOOKUP);
            int size = methods.size();

            MethodHandle[] methodHandles = new MethodHandle[size];

            for (int i = 0; i < size; i++) {
                Method method = methods.get(i);

                try {
                    methodHandles[i] = lookup.unreflect(method);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }

            return new MethodHandleInvoker(methodHandles);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(int index, Object instance, Object... arguments) {
        try {
            return (Object) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(int index, Object instance) {
        try {
            return methodHandles[index].invoke(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke1(int index, Object instance, Object arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int intInvoke(int index, Object instance, Object... arguments) {
        try {
            return (int) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long longInvoke(int index, Object instance, Object... arguments) {
        try {
            return (long) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float floatInvoke(int index, Object instance, Object... arguments) {
        try {
            return (float) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double doubleInvoke(int index, Object instance, Object... arguments) {
        try {
            return (double) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean booleanInvoke(int index, Object instance, Object... arguments) {
        try {
            return (boolean) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte byteInvoke(int index, Object instance, Object... arguments) {
        try {
            return (byte) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short shortInvoke(int index, Object instance, Object... arguments) {
        try {
            return (short) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char charInvoke(int index, Object instance, Object... arguments) {
        try {
            return (char) methodHandles[index].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeInt1(int index, Object instance, int arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeLong1(int index, Object instance, long arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeFloat1(int index, Object instance, float arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeDouble1(int index, Object instance, double arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeBoolean1(int index, Object instance, boolean arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeByte1(int index, Object instance, byte arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeShort1(int index, Object instance, short arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeChar1(int index, Object instance, char arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
