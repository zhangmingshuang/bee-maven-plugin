package com.nascent.maven.plugin.bee.mojo.dependency;

import java.util.List;
import org.apache.maven.model.Dependency;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @since 2021/1/22
 */
public interface ProjectDependencyProcessor {

  /**
   * get project maven dependencies.
   *
   * <p>a maven project dependencies tag includes {@code dependencyManagement} and {@code
   * dependencies}.
   *
   * @param skipDependencyManagement skip maven config's {@code dependencyManagement} tag.
   * @return project maven dependencies.
   */
  List<Dependency> getDependencies(boolean skipDependencyManagement);

  ProjectDependency getProjectDependency();
}
