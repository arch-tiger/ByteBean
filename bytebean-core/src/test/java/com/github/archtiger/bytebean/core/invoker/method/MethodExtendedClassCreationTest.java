package com.github.archtiger.bytebean.core.invoker.method;

import com.github.archtiger.bytebean.core.model.MethodInvokerResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试方法访问器的并发创建和缓存机制
 *
 * @author ArchTiger
 * @date 2026/1/14 23:37
 */
class MethodExtendedClassCreationTest {
    /**
     * 测试多线程环境下对同一个类并发调用 generate 的场景
     * 验证缓存的并发访问是否安全
     * <p>
     * 测试策略：
     * 1. 使用高并发（500线程）同时调用 generate
     * 2. 使用 CountDownLatch 确保所有线程真正同时访问 generate 方法
     * 3. 验证所有调用都成功且返回相同的缓存结果
     * 4. 测试 computeIfAbsent 在高并发下的行为（只有一个线程会创建类，其他获取缓存）
     */
    @Test
    void testConcurrentGenerationForSameClass() throws InterruptedException, ExecutionException {
        final Class<?> testClass = TestMethodEntity.class;
        final int threadCount = 500;
        final long timeoutSeconds = 30;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);

        List<Throwable> exceptions = new CopyOnWriteArrayList<>();
        List<MethodInvokerResult> results = new CopyOnWriteArrayList<>();

        // 创建所有任务，等待启动信号
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Future<?> future = executor.submit(() -> {
                try {
                    // 等待所有线程就绪后同时启动
                    startLatch.await();

                    // 每个线程只调用一次 generate，确保真正的并发访问
                    MethodInvokerResult result = MethodInvokerGenerator.generate(testClass);
                    assertNotNull(result, "Result should not be null");
                    assertTrue(result.ok(), "Result should be success");
                    assertNotNull(result.methodInvokerClass(), "Invoker class should not be null");
                    results.add(result);
                } catch (Throwable t) {
                    exceptions.add(t);
                } finally {
                    completionLatch.countDown();
                }
            });
            futures.add(future);
        }

        // 启动所有线程
        startLatch.countDown();

        // 等待所有任务完成（带超时）
        boolean completed = completionLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        if (!completed) {
            executor.shutdownNow();
            fail("Test timed out after " + timeoutSeconds + " seconds");
        }

        // 关闭线程池
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // 验证没有异常发生
        if (!exceptions.isEmpty()) {
            exceptions.forEach(t -> t.printStackTrace());
            fail("Concurrent generation failed with " + exceptions.size() + " exceptions");
        }

        // 验证所有调用都成功
        assertEquals(threadCount, results.size(),
                "Expected " + threadCount + " results, got " + results.size());

        // 验证所有返回的是相同的 methodInvokerClass（因为使用了缓存）
        Class<?> firstInvokerClass = results.get(0).methodInvokerClass();
        for (MethodInvokerResult result : results) {
            assertEquals(firstInvokerClass, result.methodInvokerClass(),
                    "All results should return the same cached invoker class");
        }
    }


}
