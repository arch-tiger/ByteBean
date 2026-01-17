package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.support.ByteBeanReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 基于MethodHandle的方法调用器（高性能优化版本）
 * <p>
 * 优化策略：
 * 1. 使用数组索引访问代替 try-catch，避免异常处理开销
 * 2. 在构建时为每个方法创建专用的 MethodHandle，精确匹配目标对象类型和参数类型
 * 3. 全部使用 invokeExact 进行调用，达到最顶级的性能
 * 4. 根据参数个数创建精确的 MethodHandle 变体，完全避免 invokeWithArguments 的包装开销
 * <p>
 * 核心优化：
 * - 构建时已知 targetClass，固化接收者类型
 * - 构建时已知每个方法的参数类型，创建精确的 MethodType
 * - 为0-10个参数创建精确的 MethodHandle 变体，全部使用 invokeExact
 * - 超过10个参数的情况使用 invokeWithArguments（极少见）
 *
 * @author ArchTiger
 * @date 2026/1/16 19:46
 */
public class MethodHandleInvoker extends MethodInvoker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final int MAX_EXACT_ARGS = 10; // 支持0-10个参数的精确调用

    // 存储不同参数个数的 MethodHandle 数组
    // varHandles[argCount][index] = methodHandle for argCount 个参数的方法
    private final MethodHandle[][] varHandles;

    // 存储每个方法对应的参数个数
    private final int[] argCounts;

    private MethodHandleInvoker(MethodHandle[][] varHandles, int[] argCounts) {
        this.varHandles = varHandles;
        this.argCounts = argCounts;
    }

    public static MethodHandleInvoker of(Class<?> targetClass) {
        try {
            List<Method> methods = ByteBeanReflectUtil.getMethods(targetClass);
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(targetClass, LOOKUP);
            int size = methods.size();

            // 初始化数组
            MethodHandle[][] varHandles = new MethodHandle[MAX_EXACT_ARGS + 1][size];
            int[] argCounts = new int[size];

            for (int i = 0; i < size; i++) {
                Method method = methods.get(i);
                Class<?>[] paramTypes = method.getParameterTypes();
                int argCount = paramTypes.length;

                try {
                    // 创建基础 MethodHandle
                    MethodHandle baseHandle = lookup.unreflect(method);

                    // 记录参数个数
                    argCounts[i] = argCount;

                    // 为每个参数个数变体创建精确的 MethodHandle
                    if (argCount <= MAX_EXACT_ARGS) {
                        // 构建精确的方法类型：(TargetClass, Param1, Param2, ...) -> ReturnType
                        MethodType exactType = MethodType.methodType(
                                method.getReturnType(),
                                targetClass,
                                paramTypes
                        );

                        // 转换为精确类型，编译器可以正确生成 invokeExact 的类型签名
                        varHandles[argCount][i] = baseHandle.asType(exactType);
                    } else {
                        // 超过10个参数，创建标准类型用于 invoke（比 invokeWithArguments 更快）
                        MethodType standardType = MethodType.methodType(
                                method.getReturnType(),
                                targetClass,
                                paramTypes
                        );
                        varHandles[MAX_EXACT_ARGS][i] = baseHandle.asType(standardType);
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            return new MethodHandleInvoker(varHandles, argCounts);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (Object) handle.invokeExact(instance);
                    case 1:
                        return (Object) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (Object) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(int index, Object instance) {
        try {
            // 使用 invokeExact 获得最高性能
            return (Object) varHandles[0][index].invokeExact(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke1(int index, Object instance, Object arg) {
        try {
            // 使用 invoke 而不是 invokeExact，因为参数类型不匹配
            return varHandles[1][index].invoke(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int intInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (int) handle.invokeExact(instance);
                    case 1:
                        return (int) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (int) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (int) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (int) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long longInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (long) handle.invokeExact(instance);
                    case 1:
                        return (long) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (long) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (long) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (long) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float floatInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (float) handle.invokeExact(instance);
                    case 1:
                        return (float) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (float) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (float) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (float) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double doubleInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (double) handle.invokeExact(instance);
                    case 1:
                        return (double) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (double) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (double) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (double) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean booleanInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (boolean) handle.invokeExact(instance);
                    case 1:
                        return (boolean) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (boolean) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (boolean) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (boolean) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte byteInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (byte) handle.invokeExact(instance);
                    case 1:
                        return (byte) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (byte) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (byte) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (byte) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short shortInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (short) handle.invokeExact(instance);
                    case 1:
                        return (short) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (short) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (short) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (short) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char charInvoke(int index, Object instance, Object... arguments) {
        try {
            int argCount = argCounts[index];
            if (argCount <= MAX_EXACT_ARGS) {
                MethodHandle handle = varHandles[argCount][index];
                switch (argCount) {
                    case 0:
                        return (char) handle.invokeExact(instance);
                    case 1:
                        return (char) handle.invokeExact(instance, arguments[0]);
                    case 2:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1]);
                    case 3:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                    case 4:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                    case 5:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                    case 6:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                    case 7:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                    case 8:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                    case 9:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                    case 10:
                        return (char) handle.invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                    default:
                        return (char) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
                }
            } else {
                return (char) varHandles[MAX_EXACT_ARGS][index].invoke(instance, arguments);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeInt1(int index, Object instance, int arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeLong1(int index, Object instance, long arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeFloat1(int index, Object instance, float arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeDouble1(int index, Object instance, double arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeBoolean1(int index, Object instance, boolean arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeByte1(int index, Object instance, byte arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeShort1(int index, Object instance, short arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invokeChar1(int index, Object instance, char arg) {
        try {
            // 使用 invokeExact，因为参数类型匹配
            return (Object) varHandles[1][index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
