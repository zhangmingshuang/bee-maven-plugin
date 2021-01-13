package com.nascent.maven.plugin.bee.mojo.support;

import bee.com.nascent.maven.plugin.process.Param;
import com.nascent.maven.plugin.bee.constant.Config;
import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import com.nascent.maven.plugin.bee.mojo.java.builder.BeeTestFileBuilder;
import com.nascent.maven.plugin.bee.mojo.java.builder.BuildException;
import com.nascent.maven.plugin.bee.mojo.scanner.ClassFileScanner;
import com.nascent.maven.plugin.bee.mojo.scanner.Scanner;
import com.nascent.maven.plugin.bee.utils.CollectionUtils;
import com.nascent.maven.plugin.bee.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.model.Dependency;
import org.springframework.util.AntPathMatcher;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/16
 */
public class MojoClassProcessor {

  private static final ClassPool CLASS_POOL = ClassPool.getDefault();
  private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
  private static final Set<String> APPENDED_CLASS_PATHS = new HashSet<>(8);
  private static final Scanner SCANNER = new ClassFileScanner();
  /** 扫描到的类 */
  private final List<CtClass> scannedClasses = new ArrayList<>(16);

  private String printDependency;

  public static MojoClassProcessor newInstance() {
    return new MojoClassProcessor();
  }

  public MojoClassProcessor registerDependencies(List<Dependency> dependencies) {
    if (!CollectionUtils.isEmpty(dependencies)) {
      dependencies.forEach(this::appendDependency);
    }
    return this;
  }

  public MojoClassProcessor registerClassPath(String classPath) {
    try {
      CLASS_POOL.appendClassPath(classPath);
    } catch (NotFoundException e) {
      MojoContexts.getLogger().warn("classpath:[ " + classPath + "] not exists.");
    }
    return this;
  }

  public MojoClassProcessor registerClassPath(Set<String> classPaths) {
    if (CollectionUtils.isEmpty(classPaths)) {
      return this;
    }
    for (String classPath : classPaths) {
      this.registerClassPath(classPath);
    }
    return this;
  }

  private void appendDependency(Dependency dependency) {
    String jarKey =
        ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
    if (APPENDED_CLASS_PATHS.contains(jarKey)) {
      return;
    }

    String scope = dependency.getScope();
    String groupId = dependency.getGroupId();
    String artifactId = dependency.getArtifactId();
    String version = dependency.getVersion();
    String type = dependency.getType();

    if (!Config.COMPILE.equals(scope) && !StringUtils.isEmpty(scope)) {
      MojoContexts.getLogger()
          .debug("notCompileScope:" + groupId + ":" + artifactId + ":" + version + ":" + scope);
      return;
    }
    String basedir = MojoContexts.getProject().getLocalRepositoryBaseDir();
    // 找到File
    if (StringUtils.isEmpty(type)) {
      type = "jar";
    }
    String folder =
        Config.DOT_PATTERN.matcher(groupId).replaceAll(Matcher.quoteReplacement(File.separator));
    Path jarFile =
        Paths.get(basedir, folder, artifactId, version, artifactId + "-" + version + "." + type);

    if (ArtifactUtils.isSnapshot(dependency.getVersion())) {
      Param<Path> param = this.getSnapshotJar(jarKey, jarFile);
      jarKey = param.getKey();
      jarFile = param.getValue();
    }

    if (Files.notExists(jarFile)) {
      MojoContexts.getLogger()
          .debug("dependencySkip: " + groupId + ":" + artifactId + ":" + version + " not exists.");
      // 如果Jar包不存在，则可能是项目下的模块
      return;
    }

    APPENDED_CLASS_PATHS.add(jarKey);
    this.appendClassPath(jarKey, jarFile);
  }

  private Param<Path> getSnapshotJar(String jarKey, Path jarFile) {
    Path repositories = jarFile.getParent().resolve("_remote.repositories");
    try {
      List<String> lines = Files.readAllLines(repositories);
      for (String line : lines) {
        if (line == null || line.charAt(0) == '#') {
          continue;
        }
        String pomOrJar = line.substring(0, line.indexOf('>'));
        if (pomOrJar.endsWith("jar")) {
          jarFile = jarFile.getParent().resolve(pomOrJar);
        }
        jarKey = jarKey + '>' + pomOrJar;
      }
    } catch (IOException e) {
      // ignore
    }
    return new Param<Path>(jarKey, jarFile);
  }

  private void appendClassPath(String jarKey, Path jarFile) {
    String jar = jarFile.toString();
    try {
      if (jar.endsWith("-sources.jar")) {
        jar = jar.replace("-sources.jar", ".jar");
      }
      CLASS_POOL.appendClassPath(jar);
      if (!StringUtils.isEmpty(this.printDependency)
          && PATH_MATCHER.match(this.printDependency, jarKey)) {
        MojoContexts.getLogger().info("appendClassPath:" + jarKey + " > " + jar);
      }
      MojoContexts.getLogger().debug("appendClassPath:" + jarKey + " > " + jar);
    } catch (NotFoundException e) {
      MojoContexts.getLogger().debug("appendClassPathNotFound:" + jarKey + " > " + jar);
    }
  }

  public MojoClassProcessor generateTo(String folder) {
    if (!CollectionUtils.isEmpty(this.scannedClasses)) {
      String packageName = MojoContexts.getPlugin().getResourcetPackageName();
      Path path = Paths.get(folder);
      MojoContexts.getLogger().info("class generate path:" + path);
      MojoContexts.getLogger().info("class generate size:" + this.scannedClasses.size());
      MojoContexts.getLogger().info("class generate basicPackageName:" + packageName);
      try {
        for (CtClass scannedClass : this.scannedClasses) {
          BeeTestFileBuilder.builder(scannedClass).javaFile(packageName).writeTo(path);
        }
      } catch (BuildException e) {
        MojoContexts.getLogger().error("error on " + e.getClassName() + "#" + e.getMethodName());
        MojoContexts.getLogger().errorAndExit(e);
      } catch (IOException e) {
        MojoContexts.getLogger().error("write file to:" + path);
        MojoContexts.getLogger().errorAndExit(e);
      }
    } else {
      MojoContexts.getLogger().warn("not class files were scanned.");
    }
    return this;
  }

  public MojoClassProcessor scan(String scanPackagePattern) {
    Path path = Paths.get(MojoContexts.getBuild().getOutputDirectory());
    MojoContexts.getLogger().info("scanClassTarget:" + path);
    List<Path> filePaths = null;
    try {
      filePaths = SCANNER.scan(path);
      List<CtClass> classes = new ArrayList<>(filePaths.size());
      boolean pattern = !StringUtils.isEmpty(scanPackagePattern);
      if (pattern) {
        scanPackagePattern = "*" + File.separator + scanPackagePattern.replace(".", File.separator);
        MojoContexts.getLogger().info("scanClassTargetPathPattern:" + scanPackagePattern);
      }
      for (Path file : filePaths) {
        if (file == null
            || (!StringUtils.isEmpty(scanPackagePattern)
                && !PATH_MATCHER.match(scanPackagePattern, file.toString()))) {
          continue;
        }
        CtClass ctClass = CLASS_POOL.makeClass(new FileInputStream(file.toFile()));
        classes.add(ctClass);
      }
      this.scannedClasses.addAll(classes);
    } catch (IOException e) {
      MojoContexts.getLogger().errorAndExit(e);
    }
    return this;
  }

  public MojoClassProcessor printDependency(String printDependency) {
    this.printDependency = printDependency;
    return this;
  }
}
