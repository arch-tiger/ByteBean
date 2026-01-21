package com.github.archtiger.bytebean.core.invoker.method.jmh;

import java.io.IOException;

/**
 * MethodInvokerRunner
 *
 * @author ArchTiger
 * @date 2026/1/21 21:23
 */
public class MethodInvokerRunner {
    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{
                MethodInvoker10Benchmark.class.getName(),
                MethodInvoker50Benchmark.class.getName(),
                MethodInvoker100Benchmark.class.getName(),
                MethodInvoker150Benchmark.class.getName(),
                MethodInvoker200Benchmark.class.getName(),
                MethodInvoker250Benchmark.class.getName(),
                MethodInvoker300Benchmark.class.getName(),
                MethodInvoker500Benchmark.class.getName(),
                MethodInvoker1000Benchmark.class.getName()
        });

    }
}