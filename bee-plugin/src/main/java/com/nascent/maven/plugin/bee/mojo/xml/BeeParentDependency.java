package com.nascent.maven.plugin.bee.mojo.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/18
 */
@Setter
@Getter
@ToString(callSuper = true)
public class BeeParentDependency extends BeeDependency {

  private String relativePath = "../pom.xml";
}
