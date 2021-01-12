package com.nascent.maven.plugin.bee.mojo.scanner;

import com.nascent.maven.plugin.bee.mojo.context.MojoContexts;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/5/6
 */
public class ClassFileScanner implements Scanner {

  public static final String CLASS_SUFFIX = ".class";

  @Override
  public List<Path> scan(Path folder) throws IOException {
    List<Path> paths = new ArrayList<>();
    try {
      Files.walkFileTree(
          folder,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                throws IOException {
              if (path.getFileName().toString().endsWith(CLASS_SUFFIX)) {
                paths.add(path);
              }
              return super.visitFile(path, attrs);
            }
          });
    } catch (NoSuchFileException e) {
      MojoContexts.getLogger().error("class files not found. may be should rebuild project?");
      MojoContexts.getLogger().errorAndExit(e);
    }
    return paths;
  }
}
