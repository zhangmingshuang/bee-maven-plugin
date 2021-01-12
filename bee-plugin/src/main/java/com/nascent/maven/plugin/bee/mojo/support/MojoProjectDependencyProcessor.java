package com.nascent.maven.plugin.bee.mojo.support;

import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.context.MojoProject;
import com.nascent.maven.plugin.bee.mojo.xml.BeeDependency;
import com.nascent.maven.plugin.bee.mojo.xml.BeeParentDependency;
import com.nascent.maven.plugin.bee.mojo.xml.BeeXml;
import com.nascent.maven.plugin.bee.utils.CollectionUtils;
import com.nascent.maven.plugin.bee.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.xml.sax.SAXException;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/16
 */
public class MojoProjectDependencyProcessor {

  public static List<Dependency> jarDependencies(boolean dependencyManagementSkip) {
    List<Dependency> dependencies = new ArrayList<>();
    MojoProject project = MojoContexts.getProject();
    // 对应maven配置中的dependencyManagement标签
    if (!dependencyManagementSkip && project.getDependencyManagement() != null) {
      List<Dependency> managementDependencies = project.getDependencyManagement().getDependencies();
      if (!CollectionUtils.isEmpty(managementDependencies)) {
        dependencies.addAll(parseDependencies(managementDependencies, false));
      }
    } else {
      MojoContexts.getLogger()
          .warn("DependencyManagement is null. isSkip:" + dependencyManagementSkip);
    }

    if (!CollectionUtils.isEmpty(project.getDependencies())) {
      dependencies.addAll(parseDependencies(project.getDependencies(), true));
    } else {
      MojoContexts.getLogger().warn("Project Dependencies is empty.");
    }
    return dependencies;
  }

  private static List<Dependency> parseDependencies(
      List<Dependency> dependencies, boolean sourceLocationHanld) {
    if (CollectionUtils.isEmpty(dependencies)) {
      return Collections.emptyList();
    }
    List<Dependency> result = new ArrayList<>(dependencies.size());
    for (Dependency dependency : dependencies) {
      parseDependencyToList(result, dependency, sourceLocationHanld);
    }
    return result;
  }

  private static void parseDependencyToList(
      List<Dependency> result, Dependency dependency, boolean sourceLocationHanlde) {
    String version = dependency.getVersion();
    if (version == null) {
      // 如果存在空的版本信息，则表示会有对应的dependencyManagement
      return;
    }
    if (dependency.getScope() != null && !"compile".equals(dependency.getScope())) {
      return;
    }
    if (dependency.getType() != null && !"jar".equals(dependency.getType())) {
      return;
    }
    // 动态版本，此时需要依赖的配置项
    if (isPropertyVersion(version)) {
      // 找到版本
      MojoProject project = MojoContexts.getProject();
      Properties properties = project.getProperties();
      version = properties.getProperty(getPropertyVersion(version));
      if (StringUtils.isEmpty(version)) {
        return;
      }
      dependency.setVersion(version);
    }
    result.add(dependency);
    if (sourceLocationHanlde) {
      // 加载Pom
      MojoProject project = MojoContexts.getProject();
      Path pomFile =
          Paths.get(project.getLocalRepositoryBaseDir()).resolve(getPomFilePath(dependency));
      appendDependencyJarPomDependencies(result, pomFile);
    }
  }

  public static Path getPomFilePath(Dependency dependency) {
    String groupId = dependency.getGroupId();
    String artifactId = dependency.getArtifactId();
    String version = dependency.getVersion();
    return Paths.get(
        groupId.replace(".", File.separator),
        artifactId.replace(".", File.separator),
        version,
        artifactId + "-" + version + ".pom");
  }

  private static String getPropertyVersion(String version) {
    if (StringUtils.isEmpty(version)) {
      return version;
    }
    version = version.trim();
    return version.substring(2, version.length() - 1);
  }

  private static boolean isPropertyVersion(String version) {
    if (StringUtils.isEmpty(version)) {
      return false;
    }
    version = version.trim();
    return version.startsWith("${") && version.endsWith("}");
  }

  private static void appendDependencyJarPomDependencies(List<Dependency> result, Path pomFile) {
    if (pomFile == null) {
      return;
    }
    if (!Files.isExecutable(pomFile)) {
      return;
    }
    try {
      BeeXml xmlFile = BeeXml.from(new FileInputStream(pomFile.toFile()));
      List<BeeDependency> dependencies = xmlFile.getDependencies();
      dependencies.addAll(xmlFile.getDependencyManagement());
      Map<String, String> properties = xmlFile.getProperties();
      if (!CollectionUtils.isEmpty(dependencies)) {
        for (BeeDependency beeDep : dependencies) {
          Dependency dependency = new Dependency();
          dependency.setArtifactId(beeDep.getArtifactId());
          dependency.setGroupId(beeDep.getGroupId());
          dependency.setOptional(beeDep.getOptional());
          dependency.setScope(beeDep.getScope());
          dependency.setType(beeDep.getType());
          if (isPropertyVersion(beeDep.getVersion())) {
            dependency.setVersion(properties.get(getPropertyVersion(beeDep.getVersion())));
          } else {
            dependency.setVersion(beeDep.getVersion());
          }
          parseDependencyToList(result, dependency, false);
        }
      }
    } catch (IOException | SAXException e) {
      MojoContexts.getLogger().error(e);
    }
  }

  public static Set<String> moduleDependencies() {
    // 取得依赖模块Jar包
    MojoProject project = MojoContexts.getProject();
    File pomFile = project.getPomFile();
    if (pomFile == null) {
      return null;
    }
    Path baseDir = project.getBaseDir();
    String outputDirectory = MojoContexts.getBuild().getOutputDirectory();
    String targetClassPath = outputDirectory.replace(baseDir.toString(), "");
    Set<String> moduleTargetClassPaths = new HashSet<>();
    String forward = "";
    try {
      while (pomFile != null) {
        BeeXml xmlFile = BeeXml.from(new FileInputStream(pomFile));
        List<String> modules = xmlFile.getModules();
        if (!CollectionUtils.isEmpty(modules)) {
          for (String module : modules) {
            Path path = baseDir.resolve(Paths.get(forward, module, targetClassPath));
            moduleTargetClassPaths.add(path.toString());
          }
        }
        BeeParentDependency parent = xmlFile.getParent();
        if (parent != null) {
          pomFile = new File(pomFile.getParent() + "/" + parent.getRelativePath());
          forward += "../";
        } else {
          pomFile = null;
        }
      }
    } catch (IOException | SAXException e) {
      MojoContexts.getLogger().debug(e);
    }
    return moduleTargetClassPaths;
  }
}
