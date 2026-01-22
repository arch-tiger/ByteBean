package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.api.method.MethodInvoker;
import com.github.archtiger.bytebean.core.model.MethodGroup;
import com.github.archtiger.bytebean.core.model.MethodIdentify;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于MethodHandle的方法调用器（高性能优化版本）
 *
 * @author ArchTiger
 * @date 2026/1/16 19:46
 */
public final class MethodHandleInvoker extends MethodInvoker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodHandle[] EMPTY_HANDLES = new MethodHandle[0];
    private final MethodHandle[] methodHandles;

    // 基本类型参数优化：紧凑数组 + 偏移量/范围控制
    // 数组不再是全局大小，而是针对该类型的实际数量
    private final MethodHandle[] int1Handles;
    private final int int1Offset;
    private final int int1End;

    private final MethodHandle[] long1Handles;
    private final int long1Offset;
    private final int long1End;

    private final MethodHandle[] float1Handles;
    private final int float1Offset;
    private final int float1End;

    private final MethodHandle[] double1Handles;
    private final int double1Offset;
    private final int double1End;

    private final MethodHandle[] boolean1Handles;
    private final int boolean1Offset;
    private final int boolean1End;

    private final MethodHandle[] byte1Handles;
    private final int byte1Offset;
    private final int byte1End;

    private final MethodHandle[] short1Handles;
    private final int short1Offset;
    private final int short1End;

    private final MethodHandle[] char1Handles;
    private final int char1Offset;
    private final int char1End;

    private MethodHandleInvoker(MethodHandle[] methodHandles,
                                MethodHandle[] int1Handles, int int1Offset, int int1End,
                                MethodHandle[] long1Handles, int long1Offset, int long1End,
                                MethodHandle[] float1Handles, int float1Offset, int float1End,
                                MethodHandle[] double1Handles, int double1Offset, int double1End,
                                MethodHandle[] boolean1Handles, int boolean1Offset, int boolean1End,
                                MethodHandle[] byte1Handles, int byte1Offset, int byte1End,
                                MethodHandle[] short1Handles, int short1Offset, int short1End,
                                MethodHandle[] char1Handles, int char1Offset, int char1End) {
        this.methodHandles = methodHandles;
        this.int1Handles = int1Handles;
        this.int1Offset = int1Offset;
        this.int1End = int1End;
        this.long1Handles = long1Handles;
        this.long1Offset = long1Offset;
        this.long1End = long1End;
        this.float1Handles = float1Handles;
        this.float1Offset = float1Offset;
        this.float1End = float1End;
        this.double1Handles = double1Handles;
        this.double1Offset = double1Offset;
        this.double1End = double1End;
        this.boolean1Handles = boolean1Handles;
        this.boolean1Offset = boolean1Offset;
        this.boolean1End = boolean1End;
        this.byte1Handles = byte1Handles;
        this.byte1Offset = byte1Offset;
        this.byte1End = byte1End;
        this.short1Handles = short1Handles;
        this.short1Offset = short1Offset;
        this.short1End = short1End;
        this.char1Handles = char1Handles;
        this.char1Offset = char1Offset;
        this.char1End = char1End;
    }

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

            List<MethodIdentify> method1List = methodGroup.method1List();

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

            for (int i = 0; i < size; i++) {
                Method method = methodGroup.methodAllList().get(i).method();
                try {
                    MethodHandle methodHandle = privateLookup.unreflect(method);
                    // 核心优化：将MethodHandle适配为通用的 (Object, Object...)Object 类型
                    MethodType genericType = MethodType.genericMethodType(method.getParameterCount() + 1);
                    methodHandles[i] = methodHandle.asType(genericType);

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
                    intList.isEmpty() ? EMPTY_HANDLES : intList.toArray(new MethodHandle[0]), intStart, intStart + intList.size(),
                    longList.isEmpty() ? EMPTY_HANDLES : longList.toArray(new MethodHandle[0]), longStart, longStart + longList.size(),
                    floatList.isEmpty() ? EMPTY_HANDLES : floatList.toArray(new MethodHandle[0]), floatStart, floatStart + floatList.size(),
                    doubleList.isEmpty() ? EMPTY_HANDLES : doubleList.toArray(new MethodHandle[0]), doubleStart, doubleStart + doubleList.size(),
                    booleanList.isEmpty() ? EMPTY_HANDLES : booleanList.toArray(new MethodHandle[0]), booleanStart, booleanStart + booleanList.size(),
                    byteList.isEmpty() ? EMPTY_HANDLES : byteList.toArray(new MethodHandle[0]), byteStart, byteStart + byteList.size(),
                    shortList.isEmpty() ? EMPTY_HANDLES : shortList.toArray(new MethodHandle[0]), shortStart, shortStart + shortList.size(),
                    charList.isEmpty() ? EMPTY_HANDLES : charList.toArray(new MethodHandle[0]), charStart, charStart + charList.size()
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

