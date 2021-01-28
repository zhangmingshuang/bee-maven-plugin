package com.nascent.maven.plugin.bee.mojo.xml;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * the {@code pom.xml}'s {@code <dependency>} in {@code <dependencies>} or {@code
 * <dependencyManagement>} childs.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/20
 */
@Setter
@Getter
@ToString
public class PomDependency {

  private String groupId;
  private String artifactId;
  private String version;
  private String type;
  private String scope;
  private String optional;
  private List<PomDependencyExclusion> exclusions;
}
