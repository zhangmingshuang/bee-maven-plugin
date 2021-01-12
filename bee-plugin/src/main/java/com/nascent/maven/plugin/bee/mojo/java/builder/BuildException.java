package com.nascent.maven.plugin.bee.mojo.java.builder;

import lombok.Getter;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/16
 */
@Getter
public class BuildException extends Exception {

  private String className;
  private String methodName;

  public BuildException(String className, String methodName, Throwable e) {
    super(e);
    this.className = className;
    this.methodName = methodName;
  }
}
