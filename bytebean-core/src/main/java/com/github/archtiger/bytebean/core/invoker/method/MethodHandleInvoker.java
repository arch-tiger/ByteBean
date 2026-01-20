package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.model.MethodGroup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * 基于MethodHandle的方法调用器（高性能优化版本）
 *
 * @author ArchTiger
 * @date 2026/1/16 19:46
 */
public final class MethodHandleInvoker extends MethodInvoker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final MethodHandle[] methodHandles;


    private MethodHandleInvoker(MethodHandle[] methodHandles) {
        this.methodHandles = methodHandles;
    }

    public static MethodHandleInvoker of(Class<?> targetClass) {
        try {
            MethodGroup methodGroup = MethodGroup.of(targetClass);
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(targetClass, LOOKUP);
            MethodHandle[] methodHandles = new MethodHandle[methodGroup.methodAllList().size()];
            for (int i = 0; i < methodGroup.methodAllList().size(); i++) {
                Method method = methodGroup.methodAllList().get(i).method();
                try {
                    MethodHandle methodHandle = privateLookup.unreflect(method);
                    methodHandles[i] = methodHandle;
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
            return switch (arguments.length) {
                case 0 -> methodHandles[index].invoke(instance);
                case 1 -> methodHandles[index].invoke(instance, arguments[0]);
                case 2 -> methodHandles[index].invoke(instance, arguments[0], arguments[1]);
                case 3 -> methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2]);
                case 4 -> methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                case 5 ->
                        methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                case 6 ->
                        methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                case 7 ->
                        methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                case 8 ->
                        methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                case 9 ->
                        methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                case 10 ->
                        methodHandles[index].invoke(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                default -> methodHandles[index].invokeWithArguments(instance, arguments);
            };
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
    public Object invoke2(int index, Object instance, Object arg1, Object arg2) {
        try {
            return methodHandles[index].invoke(instance, arg1, arg2);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke3(int index, Object instance, Object arg1, Object arg2, Object arg3) {
        try {
            return methodHandles[index].invoke(instance, arg1, arg2, arg3);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke4(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4) {
        try {
            return methodHandles[index].invoke(instance, arg1, arg2, arg3, arg4);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke5(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        try {
            return methodHandles[index].invoke(instance, arg1, arg2, arg3, arg4, arg5);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int intInvoke(int index, Object instance, Object... arguments) {
        return (int) invoke(index, instance, arguments);
    }

    @Override
    public long longInvoke(int index, Object instance, Object... arguments) {
        return (long) invoke(index, instance, arguments);
    }

    @Override
    public float floatInvoke(int index, Object instance, Object... arguments) {
        return (float) invoke(index, instance, arguments);
    }

    @Override
    public double doubleInvoke(int index, Object instance, Object... arguments) {
        return (double) invoke(index, instance, arguments);
    }

    @Override
    public boolean booleanInvoke(int index, Object instance, Object... arguments) {
        return (boolean) invoke(index, instance, arguments);
    }

    @Override
    public byte byteInvoke(int index, Object instance, Object... arguments) {
        return (byte) invoke(index, instance, arguments);
    }

    @Override
    public short shortInvoke(int index, Object instance, Object... arguments) {
        return (short) invoke(index, instance, arguments);
    }

    @Override
    public char charInvoke(int index, Object instance, Object... arguments) {
        return (char) invoke(index, instance, arguments);
    }

    @Override
    public Object invokeInt1(int index, Object instance, int arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeLong1(int index, Object instance, long arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeFloat1(int index, Object instance, float arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeDouble1(int index, Object instance, double arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeBoolean1(int index, Object instance, boolean arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeByte1(int index, Object instance, byte arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeShort1(int index, Object instance, short arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeChar1(int index, Object instance, char arg) {
        try {
            return methodHandles[index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

