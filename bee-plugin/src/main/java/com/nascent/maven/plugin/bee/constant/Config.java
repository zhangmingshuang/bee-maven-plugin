package com.nascent.maven.plugin.bee.constant;

import java.util.regex.Pattern;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/12
 */
public class Config {

  /** 对应pom.xml中的resource.target属性 */
  public static final String RESOURCES_TARGET = "resources.target";
  /** 对应pom.xml中的resource.directory属性 */
  public static final String RESOURCES_DIRECTORY = "resources.directory";

  public static final String POM_XML = "pom.xml";

  /** JAVA文件后缀 */
  public static final String JAVA_SUFFIX = ".java";
  /** 配置文件名称 */
  public static final String PROPERTIES = "/bee.properties";
  /** 换行符 */
  @SuppressWarnings("AccessOfSystemProperties")
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");

  public static final String LOCATION = "http://127.0.0.1:8080";
  public static final String THIS = "this";
  public static final String POINT = ".";
  public static final String VOID = "void";
  public static final String DOLLAR = "$";
  public static final String COMPILE = "compile";
  public static final String JAR = "jar";
  public static final Pattern DOT_PATTERN = Pattern.compile(".", Pattern.LITERAL);
}
