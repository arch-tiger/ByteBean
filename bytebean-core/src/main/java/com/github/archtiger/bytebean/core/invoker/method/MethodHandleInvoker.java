package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.model.MethodGroup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于MethodHandle的方法调用器，为大量方法场景提供高性能调用能力。
 * <p>
 * 当类的方法数量超过阈值（默认400）时，使用MethodHandle实现而非字节码生成，
 * 避免了生成过大字节码导致的类加载性能问题。
 * <p>
 * 该实现通过紧凑数组+偏移量的方式优化了基本类型参数和返回值的方法调用，
 * 减少了内存占用和分支预测失败的概率。
 * <p>
 * <b>数据结构：</b>
 * <ul>
 *   <li>methodHandles - 通用方法Handle数组</li>
 *   <li>int1Handles/long1Handles等 - 单基本类型参数方法的紧凑数组</li>
 *   <li>int1Offset/long1Offset等 - 对应紧凑数组在索引空间的起始偏移量</li>
 *   <li>intReturnHandles等 - 基本类型返回值方法的紧凑数组</li>
 * </ul>
 *
 * @author ArchTiger
 * @since 1.0.0
 */
public final class MethodHandleInvoker extends MethodInvoker {

    /**
     * MethodHandles.Lookup实例，用于创建MethodHandle。
     */
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * 空MethodHandle数组，用于无方法的情况。
     */
    private static final MethodHandle[] EMPTY_HANDLES = new MethodHandle[0];

    /**
     * 通用方法Handle数组，按方法索引排列。
     */
    private final MethodHandle[] methodHandles;

    // 基本类型参数优化：紧凑数组 + 偏移量

    /**
     * 单int参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] int1Handles;

    /**
     * int1Handles在索引空间的起始偏移量。
     */
    private final int int1Offset;

    /**
     * 单long参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] long1Handles;

    /**
     * long1Handles在索引空间的起始偏移量。
     */
    private final int long1Offset;

    /**
     * 单float参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] float1Handles;

    /**
     * float1Handles在索引空间的起始偏移量。
     */
    private final int float1Offset;

    /**
     * 单double参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] double1Handles;

    /**
     * double1Handles在索引空间的起始偏移量。
     */
    private final int double1Offset;

    /**
     * 单boolean参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] boolean1Handles;

    /**
     * boolean1Handles在索引空间的起始偏移量。
     */
    private final int boolean1Offset;

    /**
     * 单byte参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] byte1Handles;

    /**
     * byte1Handles在索引空间的起始偏移量。
     */
    private final int byte1Offset;

    /**
     * 单short参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] short1Handles;

    /**
     * short1Handles在索引空间的起始偏移量。
     */
    private final int short1Offset;

    /**
     * 单char参数方法的MethodHandle紧凑数组。
     */
    private final MethodHandle[] char1Handles;

    /**
     * char1Handles在索引空间的起始偏移量。
     */
    private final int char1Offset;

    // 基本类型返回值优化：紧凑数组 + 偏移量
    // 主要优化无参方法（Getter），在 MethodGroup 中已按返回值类型排序
    private final MethodHandle[] intReturnHandles;
    private final int intReturnOffset;

    private final MethodHandle[] longReturnHandles;
    private final int longReturnOffset;

    private final MethodHandle[] floatReturnHandles;
    private final int floatReturnOffset;

    private final MethodHandle[] doubleReturnHandles;
    private final int doubleReturnOffset;

    private final MethodHandle[] booleanReturnHandles;
    private final int booleanReturnOffset;

    private final MethodHandle[] byteReturnHandles;
    private final int byteReturnOffset;

    private final MethodHandle[] shortReturnHandles;
    private final int shortReturnOffset;

    private final MethodHandle[] charReturnHandles;
    private final int charReturnOffset;

    private MethodHandleInvoker(MethodHandle[] methodHandles,
                                MethodHandle[] int1Handles, int int1Offset,
                                MethodHandle[] long1Handles, int long1Offset,
                                MethodHandle[] float1Handles, int float1Offset,
                                MethodHandle[] double1Handles, int double1Offset,
                                MethodHandle[] boolean1Handles, int boolean1Offset,
                                MethodHandle[] byte1Handles, int byte1Offset,
                                MethodHandle[] short1Handles, int short1Offset,
                                MethodHandle[] char1Handles, int char1Offset,
                                MethodHandle[] intReturnHandles, int intReturnOffset,
                                MethodHandle[] longReturnHandles, int longReturnOffset,
                                MethodHandle[] floatReturnHandles, int floatReturnOffset,
                                MethodHandle[] doubleReturnHandles, int doubleReturnOffset,
                                MethodHandle[] booleanReturnHandles, int booleanReturnOffset,
                                MethodHandle[] byteReturnHandles, int byteReturnOffset,
                                MethodHandle[] shortReturnHandles, int shortReturnOffset,
                                MethodHandle[] charReturnHandles, int charReturnOffset) {
        this.methodHandles = methodHandles;
        this.int1Handles = int1Handles;
        this.int1Offset = int1Offset;
        this.long1Handles = long1Handles;
        this.long1Offset = long1Offset;
        this.float1Handles = float1Handles;
        this.float1Offset = float1Offset;
        this.double1Handles = double1Handles;
        this.double1Offset = double1Offset;
        this.boolean1Handles = boolean1Handles;
        this.boolean1Offset = boolean1Offset;
        this.byte1Handles = byte1Handles;
        this.byte1Offset = byte1Offset;
        this.short1Handles = short1Handles;
        this.short1Offset = short1Offset;
        this.char1Handles = char1Handles;
        this.char1Offset = char1Offset;
        this.intReturnHandles = intReturnHandles;
        this.intReturnOffset = intReturnOffset;
        this.longReturnHandles = longReturnHandles;
        this.longReturnOffset = longReturnOffset;
        this.floatReturnHandles = floatReturnHandles;
        this.floatReturnOffset = floatReturnOffset;
        this.doubleReturnHandles = doubleReturnHandles;
        this.doubleReturnOffset = doubleReturnOffset;
        this.booleanReturnHandles = booleanReturnHandles;
        this.booleanReturnOffset = booleanReturnOffset;
        this.byteReturnHandles = byteReturnHandles;
        this.byteReturnOffset = byteReturnOffset;
        this.shortReturnHandles = shortReturnHandles;
        this.shortReturnOffset = shortReturnOffset;
        this.charReturnHandles = charReturnHandles;
        this.charReturnOffset = charReturnOffset;
    }

    /**
     * 创建基于MethodHandle的方法调用器
     *
     * @param targetClass 目标类
     * @return MethodHandleInvoker 实例
     */
    public static MethodHandleInvoker of(Class<?> targetClass) {
        try {
            MethodGroup methodGroup = MethodGroup.of(targetClass);
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(targetClass, LOOKUP);
            int size = methodGroup.methodAllList().size();

            MethodHandle[] methodHandles = new MethodHandle[size];

            // 统计各类型数量并计算偏移量
            // 由于 MethodGroup 已经按类型排序，我们可以假设相同类型的方法索引是连续的（在 method1List 范围内）
            // 注意：methodAllList = method0List + method1List + ...
            // 所以我们需要遍历 method1List 来确定各类型的范围

            // 辅助方法：构建紧凑数组
            // 实际上我们可以直接遍历 methodAllList，因为全局索引就是 methodAllList 的索引

            List<MethodHandle> intList = new ArrayList<>();
            int intStart = -1;

            List<MethodHandle> longList = new ArrayList<>();
            int longStart = -1;

            List<MethodHandle> floatList = new ArrayList<>();
            int floatStart = -1;

            List<MethodHandle> doubleList = new ArrayList<>();
            int doubleStart = -1;

            List<MethodHandle> booleanList = new ArrayList<>();
            int booleanStart = -1;

            List<MethodHandle> byteList = new ArrayList<>();
            int byteStart = -1;

            List<MethodHandle> shortList = new ArrayList<>();
            int shortStart = -1;

            List<MethodHandle> charList = new ArrayList<>();
            int charStart = -1;

            List<MethodHandle> intReturnList = new ArrayList<>();
            int intReturnStart = -1;

            List<MethodHandle> longReturnList = new ArrayList<>();
            int longReturnStart = -1;

            List<MethodHandle> floatReturnList = new ArrayList<>();
            int floatReturnStart = -1;

            List<MethodHandle> doubleReturnList = new ArrayList<>();
            int doubleReturnStart = -1;

            List<MethodHandle> booleanReturnList = new ArrayList<>();
            int booleanReturnStart = -1;

            List<MethodHandle> byteReturnList = new ArrayList<>();
            int byteReturnStart = -1;

            List<MethodHandle> shortReturnList = new ArrayList<>();
            int shortReturnStart = -1;

            List<MethodHandle> charReturnList = new ArrayList<>();
            int charReturnStart = -1;

            for (int i = 0; i < size; i++) {
                Method method = methodGroup.methodAllList().get(i).method();
                try {
                    MethodHandle methodHandle = privateLookup.unreflect(method);
                    // 核心优化：将MethodHandle适配为通用的 (Object, Object...)Object 类型
                    MethodType genericType = MethodType.genericMethodType(method.getParameterCount() + 1);
                    methodHandles[i] = methodHandle.asType(genericType);

                    // 返回值优化：为返回基本类型的方法创建专用Handle (Object, Object[])Primitive
                    // 仅优化无参方法 (Getter)，利用 MethodGroup 中的返回值排序特性
                    if (method.getParameterCount() == 0) {
                        Class<?> returnType = method.getReturnType();
                        if (returnType.isPrimitive() && returnType != void.class) {
                            MethodType objParamType = methodHandle.type();
                            // 将所有参数类型转为 Object (包括 receiver)
                            for (int k = 0; k < objParamType.parameterCount(); k++) {
                                objParamType = objParamType.changeParameterType(k, Object.class);
                            }
                            // 保持返回值为基本类型，适配参数
                            MethodHandle objParamsMh = methodHandle.asType(objParamType);
                            // 使用 asSpreader 接受 Object[] 参数
                            MethodHandle spreaderMh = objParamsMh.asSpreader(Object[].class, 0);

                            if (returnType == int.class) {
                                if (intReturnStart == -1) intReturnStart = i;
                                intReturnList.add(spreaderMh);
                            } else if (returnType == long.class) {
                                if (longReturnStart == -1) longReturnStart = i;
                                longReturnList.add(spreaderMh);
                            } else if (returnType == float.class) {
                                if (floatReturnStart == -1) floatReturnStart = i;
                                floatReturnList.add(spreaderMh);
                            } else if (returnType == double.class) {
                                if (doubleReturnStart == -1) doubleReturnStart = i;
                                doubleReturnList.add(spreaderMh);
                            } else if (returnType == boolean.class) {
                                if (booleanReturnStart == -1) booleanReturnStart = i;
                                booleanReturnList.add(spreaderMh);
                            } else if (returnType == byte.class) {
                                if (byteReturnStart == -1) byteReturnStart = i;
                                byteReturnList.add(spreaderMh);
                            } else if (returnType == short.class) {
                                if (shortReturnStart == -1) shortReturnStart = i;
                                shortReturnList.add(spreaderMh);
                            } else if (returnType == char.class) {
                                if (charReturnStart == -1) charReturnStart = i;
                                charReturnList.add(spreaderMh);
                            }
                        }
                    }

                    // 针对单参数基本类型方法的优化收集
                    if (method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        if (paramType == int.class) {
                            if (intStart == -1) intStart = i;
                            intList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, int.class)));
                        } else if (paramType == long.class) {
                            if (longStart == -1) longStart = i;
                            longList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, long.class)));
                        } else if (paramType == float.class) {
                            if (floatStart == -1) floatStart = i;
                            floatList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, float.class)));
                        } else if (paramType == double.class) {
                            if (doubleStart == -1) doubleStart = i;
                            doubleList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, double.class)));
                        } else if (paramType == boolean.class) {
                            if (booleanStart == -1) booleanStart = i;
                            booleanList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, boolean.class)));
                        } else if (paramType == byte.class) {
                            if (byteStart == -1) byteStart = i;
                            byteList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, byte.class)));
                        } else if (paramType == short.class) {
                            if (shortStart == -1) shortStart = i;
                            shortList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, short.class)));
                        } else if (paramType == char.class) {
                            if (charStart == -1) charStart = i;
                            charList.add(methodHandle.asType(MethodType.methodType(Object.class, Object.class, char.class)));
                        }
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            // 构造结果
            return new MethodHandleInvoker(
                    methodHandles,
                    intList.isEmpty() ? EMPTY_HANDLES : intList.toArray(new MethodHandle[0]), intStart,
                    longList.isEmpty() ? EMPTY_HANDLES : longList.toArray(new MethodHandle[0]), longStart,
                    floatList.isEmpty() ? EMPTY_HANDLES : floatList.toArray(new MethodHandle[0]), floatStart,
                    doubleList.isEmpty() ? EMPTY_HANDLES : doubleList.toArray(new MethodHandle[0]), doubleStart,
                    booleanList.isEmpty() ? EMPTY_HANDLES : booleanList.toArray(new MethodHandle[0]), booleanStart,
                    byteList.isEmpty() ? EMPTY_HANDLES : byteList.toArray(new MethodHandle[0]), byteStart,
                    shortList.isEmpty() ? EMPTY_HANDLES : shortList.toArray(new MethodHandle[0]), shortStart,
                    charList.isEmpty() ? EMPTY_HANDLES : charList.toArray(new MethodHandle[0]), charStart,
                    intReturnList.isEmpty() ? EMPTY_HANDLES : intReturnList.toArray(new MethodHandle[0]), intReturnStart,
                    longReturnList.isEmpty() ? EMPTY_HANDLES : longReturnList.toArray(new MethodHandle[0]), longReturnStart,
                    floatReturnList.isEmpty() ? EMPTY_HANDLES : floatReturnList.toArray(new MethodHandle[0]), floatReturnStart,
                    doubleReturnList.isEmpty() ? EMPTY_HANDLES : doubleReturnList.toArray(new MethodHandle[0]), doubleReturnStart,
                    booleanReturnList.isEmpty() ? EMPTY_HANDLES : booleanReturnList.toArray(new MethodHandle[0]), booleanReturnStart,
                    byteReturnList.isEmpty() ? EMPTY_HANDLES : byteReturnList.toArray(new MethodHandle[0]), byteReturnStart,
                    shortReturnList.isEmpty() ? EMPTY_HANDLES : shortReturnList.toArray(new MethodHandle[0]), shortReturnStart,
                    charReturnList.isEmpty() ? EMPTY_HANDLES : charReturnList.toArray(new MethodHandle[0]), charReturnStart
            );

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(int index, Object instance, Object... arguments) {
        try {
            return switch (arguments.length) {
                case 0 -> methodHandles[index].invokeExact(instance);
                case 1 -> methodHandles[index].invokeExact(instance, arguments[0]);
                case 2 -> methodHandles[index].invokeExact(instance, arguments[0], arguments[1]);
                case 3 -> methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2]);
                case 4 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3]);
                case 5 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4]);
                case 6 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]);
                case 7 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
                case 8 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7]);
                case 9 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8]);
                case 10 ->
                        methodHandles[index].invokeExact(instance, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7], arguments[8], arguments[9]);
                default -> methodHandles[index].invokeWithArguments(instance, arguments);
            };
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(int index, Object instance) {
        try {
            return methodHandles[index].invokeExact(instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke1(int index, Object instance, Object arg) {
        try {
            return methodHandles[index].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke2(int index, Object instance, Object arg1, Object arg2) {
        try {
            return methodHandles[index].invokeExact(instance, arg1, arg2);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke3(int index, Object instance, Object arg1, Object arg2, Object arg3) {
        try {
            return methodHandles[index].invokeExact(instance, arg1, arg2, arg3);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke4(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4) {
        try {
            return methodHandles[index].invokeExact(instance, arg1, arg2, arg3, arg4);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke5(int index, Object instance, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        try {
            return methodHandles[index].invokeExact(instance, arg1, arg2, arg3, arg4, arg5);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int intInvoke(int index, Object instance, Object... arguments) {
        try {
            return (int) intReturnHandles[index - intReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long longInvoke(int index, Object instance, Object... arguments) {
        try {
            return (long) longReturnHandles[index - longReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public float floatInvoke(int index, Object instance, Object... arguments) {
        try {
            return (float) floatReturnHandles[index - floatReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public double doubleInvoke(int index, Object instance, Object... arguments) {
        try {
            return (double) doubleReturnHandles[index - doubleReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean booleanInvoke(int index, Object instance, Object... arguments) {
        try {
            return (boolean) booleanReturnHandles[index - booleanReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public byte byteInvoke(int index, Object instance, Object... arguments) {
        try {
            return (byte) byteReturnHandles[index - byteReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public short shortInvoke(int index, Object instance, Object... arguments) {
        try {
            return (short) shortReturnHandles[index - shortReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public char charInvoke(int index, Object instance, Object... arguments) {
        try {
            return (char) charReturnHandles[index - charReturnOffset].invokeExact(instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeInt1(int index, Object instance, int arg) {
        try {
            return int1Handles[index - int1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeLong1(int index, Object instance, long arg) {
        try {
            return long1Handles[index - long1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeFloat1(int index, Object instance, float arg) {
        try {
            return float1Handles[index - float1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeDouble1(int index, Object instance, double arg) {
        try {
            return double1Handles[index - double1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeBoolean1(int index, Object instance, boolean arg) {
        try {
            return boolean1Handles[index - boolean1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeByte1(int index, Object instance, byte arg) {
        try {
            return byte1Handles[index - byte1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeShort1(int index, Object instance, short arg) {
        try {
            return short1Handles[index - short1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object invokeChar1(int index, Object instance, char arg) {
        try {
            return char1Handles[index - char1Offset].invokeExact(instance, arg);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

