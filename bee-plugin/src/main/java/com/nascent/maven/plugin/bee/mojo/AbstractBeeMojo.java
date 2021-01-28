package com.nascent.maven.plugin.bee.mojo;

import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.dependency.MojoProjectDependencyProcessor;
import com.nascent.maven.plugin.bee.mojo.dependency.ProjectDependencyProcessor;
import com.nascent.maven.plugin.bee.mojo.support.MojoClassProcessor;
import com.nascent.maven.plugin.bee.mojo.support.MojoResourceProcessor;
import java.util.List;
import java.util.Set;
import javax.annotation.concurrent.ThreadSafe;
import lombok.Getter;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * The {@code Bee} maven plugin abstract bean.
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/15
 */
@Getter
@ThreadSafe
public class AbstractBeeMojo extends AbstractMojo {

  /** 插件使用项目信息 */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject mavenProject;
  /** 配置扫描类的包目录，如com.a.* */
  @Parameter(name = "basicPackage")
  private String basicPackage;
  /** 配置项目引用依赖需要输出的依赖信息表达式，如com.* */
  @Parameter(name = "printDependency", property = "printDependency")
  private String printDependency;
  /** 配置跳过pom中的dependencyManagement依赖 */
  @Parameter(name = "dependencyManagementSkip", property = "dependencyManagementSkip")
  private boolean dependencyManagementSkip;
  /** 模块Class依赖，如果是非jar包依赖，需要配置对应的依赖模块的class依赖。 */
  @Parameter(name = "models")
  private List<String> models;

  private final ProjectDependencyProcessor projectDependencyProcessor =
      new MojoProjectDependencyProcessor();

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    MojoContexts.init(this);
    // 依赖项目依赖
    List<Dependency> dependencies =
        this.projectDependencyProcessor.getDependencies(this.dependencyManagementSkip);
    Set<String> moduleOutputDirectory = null;
    //        MojoProjectDependencyProcessor.getProjectModuleDependenciesPath();

    MojoClassProcessor classProcessor =
        MojoClassProcessor.newInstance()
            .printDependency(this.printDependency)
            // 注册扫描项目的Class
            .registerClassPath(MojoContexts.getBuild().getOutputDirectory())
            .registerClassPath(moduleOutputDirectory)
            // 注册扫描项目的依赖包
            .registerDependencies(dependencies)
            .scan(this.basicPackage);

    // 复制资源会清空目录
    MojoResourceProcessor.generateTo(MojoContexts.getBuild().getTestSourceDirectory());
    classProcessor.generateTo(MojoContexts.getBuild().getTestSourceDirectory());
    MojoContexts.clear();
  }
}
