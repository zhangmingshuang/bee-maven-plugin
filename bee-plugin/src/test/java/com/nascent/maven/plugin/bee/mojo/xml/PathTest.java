package com.nascent.maven.plugin.bee.mojo.xml;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
public class PathTest {

  @Test
  public void test() {
    Path path = Paths.get("/test/ab/ab");
    System.out.println(path);
    System.out.println(path.resolve("/ab/ab/cde").toString());
  }
}
