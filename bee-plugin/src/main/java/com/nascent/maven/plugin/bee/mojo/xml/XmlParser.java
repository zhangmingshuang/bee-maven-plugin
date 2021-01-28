package com.nascent.maven.plugin.bee.mojo.xml;

import com.google.common.base.Preconditions;
import java.io.FileInputStream;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @since 2021/1/25
 */
public interface XmlParser {

  /**
   * create the pom xml parser.
   *
   * @param fileInputStream file input stream.
   * @return the parser.
   */
  static Pom valueOf(FileInputStream fileInputStream) {
    Preconditions.checkNotNull(fileInputStream, "fileInputStream must be not null");
    return new PomXmlParser().parse(fileInputStream);
  }

  /**
   * do parse.
   *
   * @param inputStream file input stream.
   * @return pom.
   */
  Pom parse(FileInputStream inputStream);
}
