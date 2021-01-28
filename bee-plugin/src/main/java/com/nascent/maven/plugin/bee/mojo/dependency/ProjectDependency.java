package com.nascent.maven.plugin.bee.mojo.dependency;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.maven.model.Dependency;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @since 2021/1/25
 */
@Setter
@Getter
@ToString
public class ProjectDependency {

  private List<Dependency> dependencies;
  private List<Dependency> dependencyManagements;
  private Set<String> modules;
}
