package com.nascent.maven.plugin.bee.mojo.xml;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/20
 */
public class BeeXmlTest {

    private static final String XML =
        "C:\\workspace\\java\\ECRP_零售CRM3.0\\CRM-Enterprise"
            + "\\ecrp-saas-open-platform\\ecrp-saas-open-platform-api\\pom.xml";

    @Test
    public void test() throws IOException, SAXException {
        File file = new File(XML);
        BeeXml beeXml = BeeXml.fromFile(file);
        System.out.println(beeXml);
        Assert.assertNotNull(beeXml);
    }

}
