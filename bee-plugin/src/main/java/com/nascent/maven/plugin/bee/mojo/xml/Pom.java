package com.nascent.maven.plugin.bee.mojo.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * the {@code pom.xml} abstract bean.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @since 2021/1/28
 */
@Setter
@Getter
@ToString
public class Pom {
  private String artifactId;
  private String groupId;
  private String version;
  private String packaging;

  private PomParentDependency parent;
  private List<PomDependency> dependencyManagements = new ArrayList<>(2);
  private List<PomDependency> dependencies = new ArrayList<>(16);
  private List<String> modules = new ArrayList<>(6);
  private Map<String, String> properties = new HashMap<>(8);

  public void addDependency(PomDependency dependency) {
    this.dependencies.add(dependency);
  }

  public void addDependencyManagement(PomDependency dependency) {
    this.dependencyManagements.add(dependency);
  }
}
