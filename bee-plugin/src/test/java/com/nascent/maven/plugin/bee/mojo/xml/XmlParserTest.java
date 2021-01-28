package com.nascent.maven.plugin.bee.mojo.xml;

import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.context.MojoLog;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @since 2021/1/25
 */
@Slf4j
public class XmlParserTest {

  @BeforeAll
  public static void setUp() {
    MojoContexts.addMojoContext(MojoLog.newInstance(new TestMojoLog()));
  }

  @Test
  @SuppressWarnings("AccessOfSystemProperties")
  public void testPomXmlParse() throws FileNotFoundException {
    String baseDir = System.getProperty("user.dir");
    String pomFile = baseDir + "/pom.xml";
    System.out.println(pomFile);
    Pom xmlParser = XmlParser.valueOf(new FileInputStream(pomFile));
    String artifactId = xmlParser.getParent().getArtifactId();
    Assertions.assertEquals("bee", artifactId, "artifactId not match");
  }
}
