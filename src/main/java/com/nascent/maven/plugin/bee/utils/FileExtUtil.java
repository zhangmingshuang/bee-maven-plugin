package com.nascent.maven.plugin.bee.utils;

import com.nascent.maven.plugin.bee.mojo.MojoEnv;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileExtUtil {

    private FileExtUtil() {

    }

    @SuppressWarnings("java:S3864")
    public static Boolean ensureExistEmptyDir(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        } else {
            // 清空文件夹
            // Files.walk - return all files/directories below rootPath including
            // .sorted - sort the list in reverse order, so the directory itself comes after the including
            // subdirectories and files
            // .map - map the Path to File
            // .peek - is there only to show which entry is processed
            // .forEach - calls the .delete() method on every File object
            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder()).map(Path::toFile)
                    .peek(p -> MojoEnv.getLogger().debug(p.toString()))
                    .forEach(File::delete);
            }
        }
        return Boolean.TRUE;
    }
}