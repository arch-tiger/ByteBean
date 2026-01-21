package com.github.archtiger.bytebean.core.invoker.field.jmh;

import java.io.IOException;

/**
 * @author ZIJIDELU
 * @datetime 2026/1/21 14:21
 */
public class FieldVarHandleInvokerRunner {
    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{
                FieldVarHandleInvoker10Benchmark.class.getName(),
                FieldVarHandleInvoker50Benchmark.class.getName(),
                FieldVarHandleInvoker150Benchmark.class.getName(),
                FieldVarHandleInvoker200Benchmark.class.getName(),
                FieldVarHandleInvoker250Benchmark.class.getName(),
                FieldVarHandleInvoker300Benchmark.class.getName(),
                FieldVarHandleInvoker500Benchmark.class.getName(),
                FieldVarHandleInvoker1000Benchmark.class.getName()
        });
    }
}
