package com.nascent.maven.plugin.bee.mojo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.Test;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/18
 */
public class MethodTest {

    public static class Param {

        private int a;
        private int b;
    }

    public static class Test {

        public String test(String a) {
            return "hai";
        }

        public Integer test2(Integer one, Param param) {
            return 1;
        }
    }

    @org.junit.Test
    public void test() throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(Test.class.getName());
        CtMethod test = ctClass.getDeclaredMethod("test");
        System.out.println(test);
    }
}
