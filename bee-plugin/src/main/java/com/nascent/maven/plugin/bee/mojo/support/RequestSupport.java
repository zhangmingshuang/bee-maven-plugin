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
public interface RequestSupport {

    /**
     * 是否存在请求地址映射
     *
     * @param clazz 实体
     * @return
     */
    boolean hasMapping(CtClass clazz);

    /**
     * 是否有RequestBody
     *
     * @param annotations
     * @return
     */
    boolean hasRequestBody(Object[][] annotations);

    /**
     * 是否有RequestBody
     *
     * @param annotations
     * @return
     */
    boolean hasRequestBody(Object[] annotations);

    /**
     * 获取请求映射地址
     *
     * @param aClass 实体
     * @param method 方法
     * @return
     */
    String getMapping(CtClass aClass, CtMethod method);
}
