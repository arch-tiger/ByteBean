package com.github.archtiger.bytebean.core.invoker.field.jmh;

import java.io.IOException;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/21 14:21
 */
public class FieldInvokerHelperRunner {
    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{
                FieldInvokerHelper10Benchmark.class.getName(),
                FieldInvokerHelper50Benchmark.class.getName(),
                FieldInvokerHelper150Benchmark.class.getName(),
                FieldInvokerHelper200Benchmark.class.getName(),
                FieldInvokerHelper250Benchmark.class.getName(),
                FieldInvokerHelper300Benchmark.class.getName(),
                FieldInvokerHelper500Benchmark.class.getName(),
                FieldInvokerHelper1000Benchmark.class.getName()
        });
    }
}
