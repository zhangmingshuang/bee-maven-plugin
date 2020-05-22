package com.nascent.maven.plugin.bee.mojo.support;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/15
 */
public interface Support {

    boolean hasMapping(CtClass clazz);

    boolean hasRequestBody(Object[][] annotations);

    boolean hasRequestBody(Object[] annotations);

    String getMapping(CtClass aClass, CtMethod method);
}
