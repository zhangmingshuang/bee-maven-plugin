package com.nascent.maven.plugin.bee.mojo.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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
    public void test() throws IOException, SAXException, URISyntaxException {
//        URL resource = BeeXmlTest.class.getClassLoader().getResource("pom.xml");
//        File file = new File(resource.toURI());
//        BeeXml beeXml = BeeXml.from(file);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(beeXml);
//        System.out.println(s);
    }

}
