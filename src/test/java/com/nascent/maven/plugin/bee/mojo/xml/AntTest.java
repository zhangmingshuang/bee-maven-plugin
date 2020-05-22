package com.nascent.maven.plugin.bee.mojo.xml;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/20
 */
public class AntTest {

    @Test
    public void test() {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match(
            "project/properties/*",
            "project/properties/project.build.sourceEncoding");
        System.out.println(match);
        Assert.assertTrue(match);
    }
}
