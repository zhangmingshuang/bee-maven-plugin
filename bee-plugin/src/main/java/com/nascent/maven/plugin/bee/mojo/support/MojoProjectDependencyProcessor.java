package com.nascent.maven.plugin.bee.mojo.support;

import com.nascent.maven.plugin.bee.constant.Config;
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
import java.util.regex.Matcher;
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

  private static final int CAPACITY = 32;

  private MojoProjectDependencyProcessor() {}

  /**
   * process all the dependencies jars.
   *
   * @param skipDependencyManagement skip {@code <DependencyManagement>} tag dependencies.
   * @return all the dependencies jars.
   */
  @SuppressWarnings("MethodWithMoreThanThreeNegations")
  public static List<Dependency> processJarDependencies(boolean skipDependencyManagement) {
    List<Dependency> dependencies = new ArrayList<>(8);
    // the project that run in bee plugin.
    MojoProject project = MojoContexts.getProject();
    // 对应maven配置中的dependencyManagement标签
    if (!skipDependencyManagement && project.getDependencyManagement() != null) {
      List<Dependency> managementDependencies = project.getDependencyManagement().getDependencies();
      if (!CollectionUtils.isEmpty(managementDependencies)) {
        dependencies.addAll(parseDependencies(managementDependencies, false));
      }
    }

    List<Dependency> projectDependencies = project.getDependencies();
    if (!CollectionUtils.isEmpty(projectDependencies)) {
      dependencies.addAll(parseDependencies(projectDependencies, true));
    }
    return dependencies;
  }

  private static List<Dependency> parseDependencies(
      List<Dependency> dependencies, boolean sourceLocationHandle) {
    if (CollectionUtils.isEmpty(dependencies)) {
      return Collections.emptyList();
    }
    List<Dependency> result = new ArrayList<>(dependencies.size());
    for (Dependency dependency : dependencies) {
      parseDependencyToList(result, dependency, sourceLocationHandle);
    }
    return result;
  }

  private static void parseDependencyToList(
      List<Dependency> result, Dependency dependency, boolean sourceLocationHanlde) {
    String version = dependency.getVersion();
    boolean isCompile =
        StringUtils.isEmpty(dependency.getScope()) || Config.COMPILE.equals(dependency.getScope());
    boolean isJar =
        StringUtils.isEmpty(dependency.getType()) || Config.JAR.equals(dependency.getType());
    if (version == null || !isCompile || !isJar) {
      // 如果存在空的版本信息，则表示会有对应的dependencyManagement
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
        Config.DOT_PATTERN.matcher(groupId).replaceAll(Matcher.quoteReplacement(File.separator)),
        Config.DOT_PATTERN.matcher(artifactId).replaceAll(Matcher.quoteReplacement(File.separator)),
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
    //noinspection SingleCharacterStartsWith
    return version.trim().startsWith("${") && version.endsWith("}");
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
      return Collections.emptySet();
    }
    Path baseDir = project.getBaseDir();
    String outputDirectory = MojoContexts.getBuild().getOutputDirectory();
    String targetClassPath = outputDirectory.replace(baseDir.toString(), "");
    Set<String> moduleTargetClassPaths = new HashSet<>(8);
    StringBuilder forward = new StringBuilder(CAPACITY);
    try {
      while (pomFile != null) {
        BeeXml xmlFile = BeeXml.from(new FileInputStream(pomFile));
        List<String> modules = xmlFile.getModules();
        if (!CollectionUtils.isEmpty(modules)) {
          for (String module : modules) {
            Path path = baseDir.resolve(Paths.get(forward.toString(), module, targetClassPath));
            moduleTargetClassPaths.add(path.toString());
          }
        }
        BeeParentDependency parent = xmlFile.getParent();
        if (parent != null) {
          pomFile = new File(pomFile.getParent() + "/" + parent.getRelativePath());
          forward.append("../");
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
