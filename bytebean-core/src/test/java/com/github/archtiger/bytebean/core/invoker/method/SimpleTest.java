package com.github.archtiger.bytebean.core.invoker.method;

import cn.hutool.core.util.ReflectUtil;
import com.github.archtiger.bytebean.core.utils.ByteBeanReflectUtil;

import java.lang.reflect.Method;

public class SimpleTest {
    static class A {
        public int age;

        public int getAge() {
            return age;
        }

        public void aPublic() {
        }

        protected void aProtected() {
        }

        void aDefault() {
        }

        private void aPrivate() {
        }
    }

    static class B extends A {
        public int age;

        public void bPublic() {
        }

        protected void bProtected() {
        }

        void bDefault() {
        }

        void aPrivate() {
        }

        private void bPrivate() {
        }
    }

    public static void main(String[] args) {
        Method[] declaredMethods = B.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println(declaredMethod.getName());
        }
        System.out.println("======================================");
        Method[] methods = B.class.getMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
        System.out.println("======================================");
        for (Method method : ReflectUtil.getMethods(B.class)) {
            System.out.println(method.getName());
        }
        System.out.println("======================================");
        for (Method method : ByteBeanReflectUtil.getMethods(B.class)) {
            System.out.println(method.getName());
        }
    }
}
