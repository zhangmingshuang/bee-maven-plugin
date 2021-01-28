package com.nascent.maven.plugin.bee.mojo.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * the {@code <dependency>}'s {@code <execlusion>}.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/20
 */
@Setter
@Getter
@ToString
public class PomDependencyExclusion {

  private String groupId;
  private String artifactId;
}
