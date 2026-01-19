package com.github.archtiger.bytebean.core.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MethodGroup 测试类
 * 验证参数0-5特化表的全局索引连续性
 *
 * @author ZIJIDELU
 * @datetime 2026/1/19
 */
class MethodGroupTest {

    /**
     * 测试用类，包含各种方法
     */
    static class TestClass {
        public void noArgs() {}
        public void oneArg(int a) {}
        public void oneArgLong(long a) {}
        public void oneArgDouble(double a) {}
        public void oneArgFloat(float a) {}
        public void oneArgBoolean(boolean a) {}
        public void oneArgChar(char a) {}
        public void oneArgByte(byte a) {}
        public void oneArgShort(short a) {}
        public void twoArgs(int a, int b) {}
        public void threeArgs(int a, int b, int c) {}
        public void fourArgs(int a, int b, int c, int d) {}
        public void fiveArgs(int a, int b, int c, int d, int e) {}

        public int returnInt() { return 0; }
        public long returnLong() { return 0L; }
        public double returnDouble() { return 0.0; }
        public float returnFloat() { return 0.0f; }
        public boolean returnBoolean() { return true; }
        public char returnChar() { return 'c'; }
        public byte returnByte() { return 0; }
        public short returnShort() { return 0; }

        // 重载方法 - 同名但参数不同
        public int overloaded(int a) { return a; }
        public int overloaded(int a, int b) { return a + b; }
    }

    /**
     * 测试方法索引全局唯一性
     * 验证：同一个方法在不同列表中的索引必须一致
     */
    @Test
    void testMethodIndexUniqueness() {
        MethodGroup group = MethodGroup.of(TestClass.class);
        assertTrue(group.ok());

        // 构建方法到索引的映射
        Set<Method> allMethods = new HashSet<>();
        MethodIdentify[] methodToIndexMap = new MethodIdentify[100]; // 简单映射

        // 首先从 methodIdentifyList 收集所有方法及其索引
        for (MethodIdentify mi : group.methodIdentifyList()) {
            allMethods.add(mi.method());
            methodToIndexMap[mi.index()] = mi;
        }

        // 验证所有特化列表中的方法索引与 methodIdentifyList 一致
        verifyIndexConsistency(group.method0List(), "method0List", methodToIndexMap);
        verifyIndexConsistency(group.method1List(), "method1List", methodToIndexMap);
        verifyIndexConsistency(group.method2List(), "method2List", methodToIndexMap);
        verifyIndexConsistency(group.method3List(), "method3List", methodToIndexMap);
        verifyIndexConsistency(group.method4List(), "method4List", methodToIndexMap);
        verifyIndexConsistency(group.method5List(), "method5List", methodToIndexMap);
    }

    /**
     * 验证列表中所有方法的索引与主映射一致
     */
    private void verifyIndexConsistency(List<MethodIdentify> list, String listName, MethodIdentify[] methodToIndexMap) {
        for (MethodIdentify mi : list) {
            int index = mi.index();
            MethodIdentify reference = methodToIndexMap[index];
            if (reference == null) {
                fail(String.format("索引 %d 在列表 '%s' 中无效，不在 methodIdentifyList 中", index, listName));
            }
            if (!reference.method().equals(mi.method())) {
                fail(String.format("索引 %d 在列表 '%s' 中对应的方法不一致，期望: %s，实际: %s",
                        index, listName, reference.method().getName(), mi.method().getName()));
            }
        }
    }

    /**
     * 测试索引连续性
     */
    @Test
    void testIndexContinuity() {
        MethodGroup group = MethodGroup.of(TestClass.class);
        assertTrue(group.ok());

        List<MethodIdentify> allMethods = group.methodIdentifyList();

        // 验证索引从0开始且连续
        for (int i = 0; i < allMethods.size(); i++) {
            MethodIdentify mi = allMethods.get(i);
            assertEquals(i, mi.index(),
                    String.format("methodIdentifyList 中的索引不连续，期望 %d，实际 %d，方法: %s",
                            i, mi.index(), mi.method().getName()));
        }
    }

    /**
     * 测试空类的情况
     */
    @Test
    void testEmptyClass() {
        MethodGroup group = MethodGroup.of(Object.class);
        // Object类有方法，所以不应该返回FAIL_METHOD_GROUP
        assertNotNull(group, "MethodGroup 不应该为 null");
    }

    /**
     * 测试方法分类的正确性
     */
    @Test
    void testMethodClassification() {
        MethodGroup group = MethodGroup.of(TestClass.class);
        assertTrue(group.ok());

        // 验证无参数方法（包括 returnInt, returnLong 等）
        assertTrue(group.method0List().size() >= 1, "至少有1个无参数方法");

        // 验证单参数方法
        assertTrue(group.method1List().size() >= 9, "至少有9个单参数方法");

        // 验证多参数方法
        assertTrue(group.method2List().size() >= 1, "至少有1个双参数方法");
        assertTrue(group.method3List().size() >= 1, "至少有1个三参数方法");
        assertTrue(group.method4List().size() >= 1, "至少有1个四参数方法");
        assertTrue(group.method5List().size() >= 1, "至少有1个五参数方法");
    }

    /**
     * 测试索引在跨列表引用时的一致性
     */
    @Test
    void testIndexConsistencyAcrossLists() {
        MethodGroup group = MethodGroup.of(TestClass.class);

        // 找到 overloaded(int a) 方法（在 method1List 中）
        MethodIdentify overloadedInMethod1 = group.method1List().stream()
                .filter(mi -> mi.method().getName().equals("overloaded") && mi.method().getParameterCount() == 1)
                .findFirst()
                .orElseThrow();

        // 找到 overloaded(int, int) 方法（在 method2List 中）
        MethodIdentify overloadedInMethod2 = group.method2List().stream()
                .filter(mi -> mi.method().getName().equals("overloaded") && mi.method().getParameterCount() == 2)
                .findFirst()
                .orElseThrow();

        // 在 methodIdentifyList 中找到相同的方法
        MethodIdentify sameMethod1InIdentifyList = group.methodIdentifyList().stream()
                .filter(mi -> mi.method().equals(overloadedInMethod1.method()))
                .findFirst()
                .orElseThrow();

        MethodIdentify sameMethod2InIdentifyList = group.methodIdentifyList().stream()
                .filter(mi -> mi.method().equals(overloadedInMethod2.method()))
                .findFirst()
                .orElseThrow();

        // 验证索引一致
        int index1 = overloadedInMethod1.index();
        int index2 = overloadedInMethod2.index();
        assertEquals(index1, sameMethod1InIdentifyList.index(),
                "方法在 method1List 和 methodIdentifyList 中的索引应该一致");
        assertEquals(index2, sameMethod2InIdentifyList.index(),
                "方法在 method2List 和 methodIdentifyList 中的索引应该一致");
    }

    /**
     * 测试特化组的索引连续性（核心：tableswitch优化）
     */
    @Test
    void testSpecializedGroupIndexContinuity() {
        MethodGroup group = MethodGroup.of(TestClass.class);

        // 测试所有参数数量特化列表的索引是否连续
        assertIndexContinuity(group.method0List(), "method0List");
        assertIndexContinuity(group.method1List(), "method1List");
        assertIndexContinuity(group.method2List(), "method2List");
        assertIndexContinuity(group.method3List(), "method3List");
        assertIndexContinuity(group.method4List(), "method4List");
        assertIndexContinuity(group.method5List(), "method5List");
    }

    /**
     * 断言列表中的索引是否连续（无跳跃，但不一定从0开始）
     */
    private void assertIndexContinuity(List<MethodIdentify> list, String listName) {
        if (list.isEmpty()) {
            return;  // 空列表跳过
        }

        // 检查索引是否连续（无跳跃）
        if (list.size() == 1) {
            return;  // 单个元素肯定连续
        }

        int startIndex = list.get(0).index();
        for (int i = 0; i < list.size(); i++) {
            int actualIndex = list.get(i).index();
            int expected = startIndex + i;
            assertEquals(expected, actualIndex,
                    String.format("列表 '%s' 的第 %d 个元素索引不连续，期望 %d，实际 %d",
                            listName, i, expected, actualIndex));
        }
    }

    /**
     * 测试全局索引的唯一性
     */
    @Test
    void testGlobalIndexUniqueness() {
        MethodGroup group = MethodGroup.of(TestClass.class);

        // 收集所有索引
        Set<Integer> allIndices = new HashSet<>();

        for (MethodIdentify mi : group.methodIdentifyList()) {
            assertFalse(allIndices.contains(mi.index()),
                    String.format("全局索引 %d 重复，方法: %s", mi.index(), mi.method().getName()));
            allIndices.add(mi.index());
        }

        // 验证全局索引范围
        assertEquals(allIndices.size(), group.methodIdentifyList().size(),
                "全局索引数量应该与方法数量一致");

        // 验证最大索引
        int maxIndex = allIndices.stream().max(Integer::compare).orElse(-1);
        assertEquals(group.methodIdentifyList().size() - 1, maxIndex,
                "最大索引应该等于方法数量减1");
    }
}
