package com.nascent.maven.plugin.bee.mojo.xml;

import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/22
 */
public class EqTest {

    @Test
    public void test() {
        int i = 1;
        Integer ie = 1;
        Assert.assertTrue(Objects.equals(i, ie));
    }
}
